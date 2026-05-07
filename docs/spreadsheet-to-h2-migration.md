# Spreadsheet To H2 Migration

This runbook imports the sample workbook from:

`C:\Users\5613574\Downloads\LTC-eSports\LTC-eSports\reference-files\digital-badge-tracker-sample.xlsx`

into a file-based H2 database for staging and inspection.

## Why Staging Tables First

The workbook contains multiple unrelated sheet shapes:

- module badge definitions
- module schedules
- legacy points trackers
- player points summary sheets

Some of these sheets do not map one-to-one into the current domain schema because they contain anonymous columns such as `Player 1`, `Player 2`, and legacy aggregate badge totals rather than normalized user identities and award events.

To avoid a lossy or misleading import, the migration loads each sheet into a dedicated staging table in H2 and records the header mapping in metadata tables.

This gives you:

- faithful preservation of the spreadsheet data
- repeatable imports
- direct SQL visibility into every sheet
- a clean base for later transformation into domain entities

## Files Added

- PowerShell runner: `scripts/import-digital-badge-tracker-to-h2.ps1`
- Importer class: `src/main/java/org/scottishtecharmy/wishaw_java/migration/SpreadsheetToH2ImportRunner.java`

## What The Import Creates

Metadata tables:

- `SPREADSHEET_IMPORT_RUNS`
- `SPREADSHEET_IMPORT_SHEETS`
- `SPREADSHEET_IMPORT_HEADERS`

One staging table per workbook sheet, for example:

- `IMPORT_ROAD_TO_DIAMOND_MODULE1`
- `IMPORT_RTD_MODULE1_SCHEDULE`
- `IMPORT_RTD_MODULE_1_POINTS_TRACKER`
- `IMPORT_ROCKET_LEAGUE_PLAYER_POINTS`
- `IMPORT_DEFEAT_THE_EDNDER_DRAGON_MCMO`
- `IMPORT_DTED_MCMODULE_1_POINTS_TRACKE`
- `IMPORT_MINECRAFT_PLAYER_POINTS`

Each staging table includes:

- `IMPORT_RUN_ID`
- `SOURCE_ROW_NUMBER`
- sanitized sheet columns as `CLOB`

## Import Steps

1. Open PowerShell.
2. Change to the backend folder:

```powershell
Set-Location 'C:\Users\5613574\Downloads\ti-nauts_Hackathon_projects\51a7db-tfg_hack_wishaw-java'
```

3. Run the import script:

```powershell
.\scripts\import-digital-badge-tracker-to-h2.ps1
```

If PowerShell blocks script execution, run:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\import-digital-badge-tracker-to-h2.ps1
```

## What The Script Does

1. Takes a snapshot copy of the workbook into `target\import\digital-badge-tracker-import.xlsx`.
2. If the workbook is open in Excel, it falls back to Excel automation and saves a read-only copy.
3. Runs the Spring Boot importer in non-web mode.
4. Writes the imported data to a file-based H2 database at:

```text
target\import\digital-badge-tracker.mv.db
```

## How To Inspect The Imported Data

Start the backend against the imported file-based H2 database:

```powershell
Set-Location 'C:\Users\5613574\Downloads\ti-nauts_Hackathon_projects\51a7db-tfg_hack_wishaw-java'
java -Dspring.profiles.active=h2 -Dspring.datasource.url="jdbc:h2:file:./target/import/digital-badge-tracker;MODE=PostgreSQL;AUTO_SERVER=TRUE" -Dspring.datasource.driver-class-name=org.h2.Driver -Dspring.datasource.username=sa -Dspring.datasource.password= -Dspring.jpa.hibernate.ddl-auto=update -jar .\target\wishaw-java-0.0.1-SNAPSHOT.jar
```

Then open:

```text
http://localhost:8080/h2-console
```

Use these H2 console settings:

- JDBC URL: `jdbc:h2:file:./target/import/digital-badge-tracker;MODE=PostgreSQL;AUTO_SERVER=TRUE`
- User Name: `sa`
- Password: leave blank

## Useful Queries

See import runs:

```sql
SELECT * FROM SPREADSHEET_IMPORT_RUNS ORDER BY ID DESC;
```

See sheet-to-table mapping:

```sql
SELECT SHEET_NAME, TABLE_NAME, HEADER_ROW_NUMBER, DATA_ROW_COUNT
FROM SPREADSHEET_IMPORT_SHEETS
ORDER BY SHEET_NAME;
```

See original header mapping for a sheet:

```sql
SELECT COLUMN_POSITION, ORIGINAL_HEADER, SANITIZED_COLUMN
FROM SPREADSHEET_IMPORT_HEADERS
WHERE TABLE_NAME = 'IMPORT_ROAD_TO_DIAMOND_MODULE1'
ORDER BY COLUMN_POSITION;
```

Inspect imported data:

```sql
SELECT * FROM IMPORT_ROAD_TO_DIAMOND_MODULE1;
SELECT * FROM IMPORT_RTD_MODULE1_SCHEDULE;
SELECT * FROM IMPORT_ROCKET_LEAGUE_PLAYER_POINTS;
SELECT * FROM IMPORT_MINECRAFT_PLAYER_POINTS;
```

## Known Workbook Mapping From The Sample

These sheets were detected in the sample workbook:

- `Road to Diamond- Module1`
- `RTD Module1 - Schedule`
- `RTD Module 1 - Points Tracker`
- `Rocket League Player Points`
- `Defeat The Ednder Dragon - MCMo`
- `DtED MCModule 1 - Points Tracke`
- `Minecraft Player Points`

## Next Transformation Step

Once the workbook data is staged in H2, the next phase can transform it into domain tables such as:

- `MAIN_BADGES`
- `SUB_BADGES`
- `LEARNING_MODULES`
- `MODULE_SESSION_ITEMS`

The legacy player points sheets should be treated carefully because they contain anonymous player columns and aggregate totals rather than normalized player identities and per-award history.