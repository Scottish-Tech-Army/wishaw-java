package org.scottishtecharmy.wishaw_java.service.importer;

import org.scottishtecharmy.wishaw_java.dto.response.ImportPreviewResponse;
import org.scottishtecharmy.wishaw_java.entity.ImportBatch;
import org.scottishtecharmy.wishaw_java.entity.ImportRowAudit;
import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.enums.ImportRowStatus;
import org.scottishtecharmy.wishaw_java.enums.ImportStatus;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.repository.ImportBatchRepository;
import org.scottishtecharmy.wishaw_java.repository.ImportRowAuditRepository;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.scottishtecharmy.wishaw_java.util.CsvParser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CsvImportService {

    private static final Logger log = LoggerFactory.getLogger(CsvImportService.class);

    private final ImportBatchRepository importBatchRepository;
    private final ImportRowAuditRepository importRowAuditRepository;
    private final UserAccountRepository userAccountRepository;
    private final ImportPreviewService importPreviewService;

    // FRONTEND_INTEGRATION: React import wizard will call preview first, then player mapping, then commit.
    // FRONTEND_INTEGRATION: Keep preview DTO explicit so UI can show validation issues before final commit.
    public ImportPreviewResponse uploadCsv(MultipartFile file, String username) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("CSV file is required");
        }

        byte[] bytes = readBytes(file);
        String checksum = sha256(bytes);
        Optional<ImportBatch> existingBatch = importBatchRepository.findByChecksum(checksum);
        if (existingBatch.isPresent()) {
            return importPreviewService.getPreview(existingBatch.get().getId());
        }

        ImportBatch batch = new ImportBatch();
        batch.setFileName(file.getOriginalFilename());
        batch.setImportType("POINTS_TRACKER_CSV");
        batch.setChecksum(checksum);
        userAccountRepository.findByUsername(username).ifPresent(batch::setUploadedBy);
        batch.setStatus(ImportStatus.UPLOADED);
        batch = importBatchRepository.save(batch);

        List<String[]> parsedRows = parseCsv(bytes);
        if (parsedRows.isEmpty()) {
            throw new BadRequestException("CSV file is empty");
        }

        String[] headers = parsedRows.get(0);
        ImportBatchState state = new ImportBatchState();
        Set<String> unmappedPlayers = new java.util.LinkedHashSet<>();

        for (int index = 1; index < parsedRows.size(); index++) {
            int rowNumber = index + 1;
            Map<String, String> data = toRowMap(headers, parsedRows.get(index));
            String status = "OK";
            String message = "Ready to import";

            String usernameValue = getValue(data, "username", "playerUsername", "player");
            String badgeCategoryCode = getValue(data, "badgeCategoryCode", "category", "badge");

            if (usernameValue == null || usernameValue.isBlank()) {
                status = "ERROR";
                message = "username column is required";
            } else if (badgeCategoryCode == null || badgeCategoryCode.isBlank()) {
                status = "ERROR";
                message = "badgeCategoryCode column is required";
            } else if (userAccountRepository.findByUsername(usernameValue).isEmpty()) {
                status = "WARNING";
                message = "Player not found and must be mapped to an existing player before commit";
                unmappedPlayers.add(usernameValue);
            }

            ImportRowState rowState = new ImportRowState(rowNumber, status, message, data);
            state.getRows().add(rowState);
            saveAudit(batch, rowState);
            if ("WARNING".equals(status)) {
                log.warn("Import preview warning for batch {} row {}: {}", batch.getId(), rowNumber, message);
            }
        }

        state.setUnmappedPlayers(new ArrayList<>(unmappedPlayers));
        batch.setStatus(ImportStatus.PREVIEW_READY);
        batch.setSummaryJson(importPreviewService.serialize(state));
        importBatchRepository.save(batch);

        return importPreviewService.toPreviewResponse(batch, state);
    }

    private void saveAudit(ImportBatch batch, ImportRowState rowState) {
        ImportRowAudit audit = new ImportRowAudit();
        audit.setImportBatch(batch);
        audit.setSourceSection("points-tracker");
        audit.setSourceRowNumber(rowState.getRowNumber());
        audit.setStatus(mapRowStatus(rowState.getStatus()));
        audit.setMessage(rowState.getMessage());
        audit.setRawDataJson(importPreviewService.serialize(new ImportBatchState(List.of(rowState), Map.of(), List.of())));
        importRowAuditRepository.save(audit);
    }

    private ImportRowStatus mapRowStatus(String status) {
        return switch (status) {
            case "OK" -> ImportRowStatus.OK;
            case "WARNING" -> ImportRowStatus.WARNING;
            case "ERROR" -> ImportRowStatus.ERROR;
            default -> ImportRowStatus.SKIPPED;
        };
    }

    private List<String[]> parseCsv(byte[] bytes) {
        try {
            return CsvParser.parse(new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new BadRequestException("Unable to parse CSV file");
        }
    }

    private Map<String, String> toRowMap(String[] headers, String[] row) {
        Map<String, String> data = new LinkedHashMap<>();
        for (int index = 0; index < headers.length; index++) {
            String header = headers[index] != null ? headers[index].trim() : "column" + index;
            String value = index < row.length ? row[index] : "";
            data.put(header, value);
        }
        return data;
    }

    private String getValue(Map<String, String> data, String... candidates) {
        for (String candidate : candidates) {
            if (data.containsKey(candidate)) {
                return data.get(candidate);
            }
            String matched = data.keySet().stream()
                    .filter(key -> key.equalsIgnoreCase(candidate))
                    .findFirst()
                    .orElse(null);
            if (matched != null) {
                return data.get(matched);
            }
        }
        return null;
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new BadRequestException("Unable to read uploaded file");
        }
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new BadRequestException("Checksum algorithm is unavailable");
        }
    }
}
