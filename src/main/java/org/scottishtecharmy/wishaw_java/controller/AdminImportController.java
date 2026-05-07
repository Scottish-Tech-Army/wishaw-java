package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.scottishtecharmy.wishaw_java.config.ApiPaths;
import org.scottishtecharmy.wishaw_java.dto.ImportDtos;
import org.scottishtecharmy.wishaw_java.exception.BadRequestException;
import org.scottishtecharmy.wishaw_java.migration.SpreadsheetImportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping({ApiPaths.V1 + "/admin/imports", ApiPaths.LEGACY + "/admin/imports"})
@Tag(name = "Admin Import", description = "Admin-only spreadsheet upload and H2 import endpoints")
public class AdminImportController {

    private final SpreadsheetImportService spreadsheetImportService;

    public AdminImportController(SpreadsheetImportService spreadsheetImportService) {
        this.spreadsheetImportService = spreadsheetImportService;
    }

    @PostMapping(value = "/spreadsheets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportDtos.SpreadsheetImportResponse uploadSpreadsheet(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "dropExisting", defaultValue = "true") boolean dropExisting) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please choose a file to import");
        }

        String safeFilename = resolveFilename(file.getOriginalFilename());
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("wishaw-spreadsheet-import-", fileSuffix(safeFilename));
            file.transferTo(tempFile);
            return spreadsheetImportService.importWorkbook(tempFile, safeFilename, dropExisting);
        } catch (BadRequestException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new BadRequestException("Failed to process uploaded file");
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                }
            }
        }
    }

    private String resolveFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "uploaded-workbook";
        }
        String normalized = originalFilename.replace('\\', '/');
        int index = normalized.lastIndexOf('/');
        return index >= 0 ? normalized.substring(index + 1) : normalized;
    }

    private String fileSuffix(String filename) {
        int index = filename.lastIndexOf('.');
        return index >= 0 ? filename.substring(index) : ".bin";
    }
}