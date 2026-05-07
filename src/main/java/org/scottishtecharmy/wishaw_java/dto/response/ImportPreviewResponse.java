package org.scottishtecharmy.wishaw_java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * // FRONTEND_INTEGRATION: Keep preview DTO explicit so UI can show validation issues before final commit.
 */
@Getter
@Builder
@AllArgsConstructor
public class ImportPreviewResponse {
    private Long batchId;
    private String fileName;
    private String status;
    private int totalRows;
    private int validRows;
    private int errorRows;
    private int warningRows;
    private List<String> unmappedPlayers;
    private List<ImportRowDetail> rows;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ImportRowDetail {
        private int rowNumber;
        private String status;
        private String message;
        private String rawData;
    }
}
