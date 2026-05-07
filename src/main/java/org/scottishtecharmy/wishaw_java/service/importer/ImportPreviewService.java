package org.scottishtecharmy.wishaw_java.service.importer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scottishtecharmy.wishaw_java.dto.response.ImportPreviewResponse;
import org.scottishtecharmy.wishaw_java.entity.ImportBatch;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.exception.ResourceNotFoundException;
import org.scottishtecharmy.wishaw_java.repository.ImportBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImportPreviewService {

    private final ImportBatchRepository importBatchRepository;
    private final ObjectMapper objectMapper;

    public ImportPreviewResponse getPreview(Long batchId) {
        ImportBatch batch = importBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Import batch not found: " + batchId));
        return toPreviewResponse(batch, loadState(batch));
    }

    public ImportBatchState loadState(ImportBatch batch) {
        if (batch.getSummaryJson() == null || batch.getSummaryJson().isBlank()) {
            return new ImportBatchState();
        }

        try {
            return objectMapper.readValue(batch.getSummaryJson(), ImportBatchState.class);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Unable to read import preview state");
        }
    }

    public String serialize(ImportBatchState state) {
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Unable to store import preview state");
        }
    }

    public ImportPreviewResponse toPreviewResponse(ImportBatch batch, ImportBatchState state) {
        List<ImportPreviewResponse.ImportRowDetail> rows = state.getRows().stream()
                .map(row -> ImportPreviewResponse.ImportRowDetail.builder()
                        .rowNumber(row.getRowNumber())
                        .status(row.getStatus())
                        .message(row.getMessage())
                        .rawData(toJson(row.getData()))
                        .build())
                .toList();

        int validRows = (int) state.getRows().stream().filter(row -> "OK".equals(row.getStatus())).count();
        int warningRows = (int) state.getRows().stream().filter(row -> "WARNING".equals(row.getStatus())).count();
        int errorRows = (int) state.getRows().stream().filter(row -> "ERROR".equals(row.getStatus())).count();

        return ImportPreviewResponse.builder()
                .batchId(batch.getId())
                .fileName(batch.getFileName())
                .status(batch.getStatus().name())
                .totalRows(state.getRows().size())
                .validRows(validRows)
                .warningRows(warningRows)
                .errorRows(errorRows)
                .unmappedPlayers(state.getUnmappedPlayers())
                .rows(rows)
                .build();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return String.valueOf(value);
        }
    }
}