package org.scottishtecharmy.wishaw_java.controller;

import org.scottishtecharmy.wishaw_java.dto.request.ImportPlayerMappingRequest;
import org.scottishtecharmy.wishaw_java.dto.response.ImportCommitResponse;
import org.scottishtecharmy.wishaw_java.dto.response.ImportPreviewResponse;
import org.scottishtecharmy.wishaw_java.service.importer.CsvImportService;
import org.scottishtecharmy.wishaw_java.service.importer.ImportCommitService;
import org.scottishtecharmy.wishaw_java.service.importer.ImportPreviewService;
import org.scottishtecharmy.wishaw_java.service.importer.PlayerMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/import")
@RequiredArgsConstructor
public class ImportController {

    private final CsvImportService csvImportService;
    private final PlayerMappingService playerMappingService;
    private final ImportPreviewService importPreviewService;
    private final ImportCommitService importCommitService;

    // FRONTEND_INTEGRATION: React import wizard will call preview first, then player mapping, then commit.
    @PostMapping("/csv/upload")
    public ImportPreviewResponse uploadCsv(@RequestParam("file") MultipartFile file, Authentication authentication) {
        return csvImportService.uploadCsv(file, authentication.getName());
    }

    @PostMapping("/{batchId}/map-players")
    public ImportPreviewResponse mapPlayers(@PathVariable Long batchId, @RequestBody ImportPlayerMappingRequest request) {
        return playerMappingService.mapPlayers(batchId, request);
    }

    @GetMapping("/{batchId}/preview")
    public ImportPreviewResponse preview(@PathVariable Long batchId) {
        return importPreviewService.getPreview(batchId);
    }

    @PostMapping("/{batchId}/commit")
    public ImportCommitResponse commit(@PathVariable Long batchId) {
        return importCommitService.commit(batchId);
    }

    @GetMapping("/{batchId}/report")
    public ImportPreviewResponse report(@PathVariable Long batchId) {
        return importPreviewService.getPreview(batchId);
    }
}