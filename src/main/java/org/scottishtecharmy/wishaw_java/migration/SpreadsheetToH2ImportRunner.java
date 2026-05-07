package org.scottishtecharmy.wishaw_java.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@ConditionalOnProperty(prefix = "app.spreadsheet-import", name = "enabled", havingValue = "true")
public class SpreadsheetToH2ImportRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SpreadsheetToH2ImportRunner.class);

    private final SpreadsheetImportService spreadsheetImportService;
    private final ConfigurableApplicationContext applicationContext;
    private final String workbookPath;
    private final boolean dropExisting;
    private final boolean exitAfterRun;

    public SpreadsheetToH2ImportRunner(SpreadsheetImportService spreadsheetImportService,
                                       ConfigurableApplicationContext applicationContext,
                                       @Value("${app.spreadsheet-import.workbook-path:}") String workbookPath,
                                       @Value("${app.spreadsheet-import.drop-existing:true}") boolean dropExisting,
                                       @Value("${app.spreadsheet-import.exit-after-run:false}") boolean exitAfterRun) {
        this.spreadsheetImportService = spreadsheetImportService;
        this.applicationContext = applicationContext;
        this.workbookPath = workbookPath;
        this.dropExisting = dropExisting;
        this.exitAfterRun = exitAfterRun;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (workbookPath == null || workbookPath.isBlank()) {
            throw new IllegalStateException("app.spreadsheet-import.workbook-path must be provided when spreadsheet import is enabled");
        }

        Path sourceWorkbook = Path.of(workbookPath);
        if (!Files.exists(sourceWorkbook)) {
            throw new IllegalStateException("Spreadsheet workbook not found: " + sourceWorkbook);
        }

        var result = spreadsheetImportService.importWorkbook(sourceWorkbook, dropExisting);
        log.info("Startup spreadsheet import complete. importRunId={}, workbook={}, importedSheets={}",
                result.importRunId(),
                result.workbookName(),
                result.importedSheets());

        if (exitAfterRun) {
            log.info("Spreadsheet import requested graceful shutdown after completion");
            int exitCode = SpringApplication.exit(applicationContext, () -> 0);
            System.exit(exitCode);
        }
    }
}