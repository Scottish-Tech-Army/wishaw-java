package org.scottishtecharmy.wishaw_java.dto;

import java.util.List;

public final class ImportDtos {

    private ImportDtos() {
    }

    public record SpreadsheetImportSheetDto(
            String sheetName,
            String tableName,
            int headerRowNumber,
            int dataRowCount
    ) {
    }

    public record SpreadsheetImportResponse(
            long importRunId,
            String workbookName,
            boolean dropExisting,
            int importedSheets,
            int importedRows,
            List<SpreadsheetImportSheetDto> sheets,
            String message
    ) {
    }
}