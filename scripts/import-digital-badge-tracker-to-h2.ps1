param(
    [string]$WorkbookPath = 'C:\Users\5613574\Downloads\LTC-eSports\LTC-eSports\reference-files\digital-badge-tracker-sample.xlsx',
    [string]$DatabasePath = '.\target\import\digital-badge-tracker',
    [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..')
$importDirectory = Join-Path $repoRoot 'target\import'
$snapshotPath = Join-Path $importDirectory 'digital-badge-tracker-import.xlsx'

function Copy-WorkbookSnapshot {
    param(
        [string]$SourcePath,
        [string]$TargetPath
    )

    try {
        Copy-Item $SourcePath $TargetPath -Force
        return
    } catch {
        $excel = $null
        $workbook = $null

        try {
            $excel = New-Object -ComObject Excel.Application
            $excel.Visible = $false
            $excel.DisplayAlerts = $false
            $workbook = $excel.Workbooks.Open($SourcePath, 0, $true)
            $workbook.SaveCopyAs($TargetPath)
        } finally {
            if ($workbook) { $workbook.Close($false) | Out-Null }
            if ($excel) { $excel.Quit() }
            if ($workbook) { [System.Runtime.Interopservices.Marshal]::ReleaseComObject($workbook) | Out-Null }
            if ($excel) { [System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null }
            [GC]::Collect()
            [GC]::WaitForPendingFinalizers()
        }
    }
}

if (-not (Test-Path $WorkbookPath)) {
    throw "Workbook not found: $WorkbookPath"
}

New-Item -ItemType Directory -Force -Path $importDirectory | Out-Null
Copy-WorkbookSnapshot -SourcePath $WorkbookPath -TargetPath $snapshotPath

Push-Location $repoRoot
try {
    if (-not $SkipBuild) {
        mvn.cmd -B -ntp -DskipTests compile
    }

    $workbookArgument = (Resolve-Path $snapshotPath).ProviderPath
    $databaseArgument = $DatabasePath -replace '\\', '/'
    $jvmArguments = @(
        '-Dspring.profiles.active=h2',
        '-Dserver.port=0',
        "-Dspring.datasource.url=jdbc:h2:file:$databaseArgument;MODE=PostgreSQL;AUTO_SERVER=TRUE",
        '-Dspring.datasource.driver-class-name=org.h2.Driver',
        '-Dspring.datasource.username=sa',
        '-Dspring.datasource.password=',
        '-Dspring.jpa.hibernate.ddl-auto=update',
        '-Dapp.seed.enabled=false',
        '-Dapp.spreadsheet-import.enabled=true',
        "-Dapp.spreadsheet-import.workbook-path=$workbookArgument",
        '-Dapp.spreadsheet-import.drop-existing=true',
        '-Dapp.spreadsheet-import.exit-after-run=true'
    ) -join ' '

    mvn.cmd -B -ntp "-Dspring-boot.run.jvmArguments=$jvmArguments" spring-boot:run

    Write-Host ''
    Write-Host 'Spreadsheet migration completed.' -ForegroundColor Green
    Write-Host 'Imported H2 database base path:' -ForegroundColor Cyan
    Write-Host $databaseArgument
    Write-Host 'Expected H2 files:' -ForegroundColor Cyan
    Write-Host "$databaseArgument.mv.db"
    Write-Host ''
    Write-Host 'To inspect the imported data, start the backend against the same file-based H2 URL and open /h2-console.' -ForegroundColor Yellow
} finally {
    Pop-Location
}