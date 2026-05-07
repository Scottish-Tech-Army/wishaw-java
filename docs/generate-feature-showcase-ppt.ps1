param(
    [string]$OutputPath = "$(Split-Path -Parent $PSCommandPath)\wishaw-platform-feature-showcase.pptx"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Get-OleColor([string]$hex) {
    $red = [Convert]::ToInt32($hex.Substring(1, 2), 16)
    $green = [Convert]::ToInt32($hex.Substring(3, 2), 16)
    $blue = [Convert]::ToInt32($hex.Substring(5, 2), 16)
    return $red + ($green * 256) + ($blue * 65536)
}

function Add-TextBlock($slide, [string]$text, [double]$left, [double]$top, [double]$width, [double]$height, [int]$fontSize, [string]$color, [string]$fontName = 'Aptos', [bool]$bold = $false) {
    $shape = $slide.Shapes.AddTextbox(1, $left, $top, $width, $height)
    $shape.TextFrame.TextRange.Text = $text
    $shape.TextFrame.TextRange.Font.Name = $fontName
    $shape.TextFrame.TextRange.Font.Size = $fontSize
    $shape.TextFrame.TextRange.Font.Bold = [int]$bold
    $shape.TextFrame.TextRange.Font.Color.RGB = Get-OleColor $color
    return $shape
}

function Add-Panel($slide, [double]$left, [double]$top, [double]$width, [double]$height, [string]$fillColor, [string]$lineColor, [single]$transparency = 0.0) {
    $shape = $slide.Shapes.AddShape(1, $left, $top, $width, $height)
    $shape.Fill.ForeColor.RGB = Get-OleColor $fillColor
    $shape.Fill.Transparency = $transparency
    $shape.Line.ForeColor.RGB = Get-OleColor $lineColor
    $shape.Line.Transparency = 0.15
    return $shape
}

function Add-Footer($slide, [int]$index) {
    Add-TextBlock $slide ("Wishaw eSports Platform  |  Feature Showcase  |  Slide {0}" -f $index) 32 506 656 20 10 '#94A3B8' | Out-Null
}

function Add-BulletLines($slide, [string[]]$lines, [double]$left, [double]$top, [double]$width, [double]$height) {
    $shape = Add-TextBlock $slide (($lines | ForEach-Object { [char]0x2022 + ' ' + $_ }) -join "`r") $left $top $width $height 18 '#E2E8F0' 'Aptos' $false
    $shape.TextFrame.TextRange.ParagraphFormat.Bullet.Visible = 0
    $shape.TextFrame.TextRange.ParagraphFormat.SpaceAfter = 7
    return $shape
}

function Add-StatCard($slide, [string]$title, [string]$value, [double]$left, [double]$top, [string]$accentColor) {
    Add-Panel $slide $left $top 160 86 '#132033' '#22314A' 0.0 | Out-Null
    Add-TextBlock $slide $title ($left + 14) ($top + 14) 130 20 11 '#94A3B8' 'Aptos' $false | Out-Null
    Add-TextBlock $slide $value ($left + 14) ($top + 34) 132 32 24 '#F8FAFC' 'Aptos Display' $true | Out-Null
    $accent = Add-Panel $slide ($left + 122) ($top + 14) 24 24 $accentColor $accentColor 0.1
    $accent.Line.Visible = 0
}

function New-FeatureSlide($presentation, [int]$index, [hashtable]$content) {
    $slide = $presentation.Slides.Add($index, 12)
    $slide.FollowMasterBackground = 0
    $slide.Background.Fill.ForeColor.RGB = Get-OleColor '#0F172A'

    $band = Add-Panel $slide 0 0 720 36 $content.BandColor '#1E293B' 0.0
    $band.Line.Visible = 0

    Add-TextBlock $slide $content.Eyebrow 34 46 220 18 11 '#67E8F9' 'Aptos' $true | Out-Null
    Add-TextBlock $slide $content.Title 34 68 410 66 28 '#F8FAFC' 'Aptos Display' $true | Out-Null
    Add-TextBlock $slide $content.Description 34 140 390 62 15 '#CBD5E1' 'Aptos' $false | Out-Null

    Add-BulletLines $slide $content.Bullets 34 220 390 180 | Out-Null

    Add-Panel $slide 456 58 228 360 '#111C2E' '#22314A' 0.0 | Out-Null
    Add-TextBlock $slide 'What changed' 478 82 140 18 11 '#94A3B8' 'Aptos' $true | Out-Null
    Add-TextBlock $slide $content.SideTitle 478 104 174 48 22 '#F8FAFC' 'Aptos Display' $true | Out-Null
    Add-TextBlock $slide $content.SideText 478 164 180 72 14 '#CBD5E1' 'Aptos' $false | Out-Null

    Add-StatCard $slide $content.Stat1Title $content.Stat1Value 478 258 $content.StatColor1
    Add-StatCard $slide $content.Stat2Title $content.Stat2Value 478 352 $content.StatColor2

    Add-Footer $slide $index
}

$outputDirectory = Split-Path -Parent $OutputPath
if (-not (Test-Path $outputDirectory)) {
    New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
}

if (Test-Path $OutputPath) {
    Remove-Item $OutputPath -Force
}

$powerPoint = $null
$presentation = $null

try {
    $powerPoint = New-Object -ComObject PowerPoint.Application
    $powerPoint.Visible = -1
    $presentation = $powerPoint.Presentations.Add()
    $presentation.PageSetup.SlideSize = 15

    $slide1 = $presentation.Slides.Add(1, 12)
    $slide1.FollowMasterBackground = 0
    $slide1.Background.Fill.ForeColor.RGB = Get-OleColor '#08111F'

    $heroBar = Add-Panel $slide1 0 0 720 540 '#08111F' '#08111F' 0.0
    $heroBar.Line.Visible = 0
    $accentLeft = Add-Panel $slide1 0 0 196 540 '#0B5C73' '#0B5C73' 0.78
    $accentLeft.Line.Visible = 0
    $accentRight = Add-Panel $slide1 530 0 190 540 '#D97706' '#D97706' 0.84
    $accentRight.Line.Visible = 0
    $heroPanel = Add-Panel $slide1 34 46 652 424 '#0F172A' '#22314A' 0.0
    $heroPanel.Line.Transparency = 0.35

    Add-TextBlock $slide1 'Wishaw eSports Platform' 58 82 300 20 13 '#67E8F9' 'Aptos' $true | Out-Null
    Add-TextBlock $slide1 'Feature Showcase Deck' 58 110 400 54 30 '#F8FAFC' 'Aptos Display' $true | Out-Null
    Add-TextBlock $slide1 'A presentation of the strongest features built across the player and admin experiences, including tournament controls, badge progression, module operations, and the new spreadsheet import pipeline.' 58 174 410 92 17 '#CBD5E1' 'Aptos' $false | Out-Null

    Add-StatCard $slide1 'Admin upload run' '7 sheets / 99 rows' 58 296 '#14B8A6'
    Add-StatCard $slide1 'Security check' 'Player import = 403' 228 296 '#22C55E'
    Add-StatCard $slide1 'Live stack' 'Frontend 3000  |  API 8080' 398 296 '#F59E0B'

    Add-TextBlock $slide1 'Deck focus' 498 120 120 20 12 '#94A3B8' 'Aptos' $true | Out-Null
    Add-TextBlock $slide1 '1. Player growth and badges`r2. Tournament guardrails`r3. Admin operations and import tools`r4. Validation-ready local workflow' 498 150 150 120 16 '#F8FAFC' 'Aptos' $false | Out-Null

    Add-Footer $slide1 1

    New-FeatureSlide $presentation 2 @{
        BandColor = '#1D4ED8'
        Eyebrow = 'PLAYER GROWTH ENGINE'
        Title = 'Badges, modules, and visible progress now work together'
        Description = 'The player dashboard now surfaces XP, badge levels, sub-badge activity, and module momentum in one place instead of scattering that context across multiple screens.'
        Bullets = @(
            'Badge progress and level states are visible directly from the dashboard and badge journey views.',
            'Sub-badge progress is tied back to module activity so players can see what they have earned and what is next.',
            'Player stats and learning progress remain readable for both casual users and demo audiences.'
        )
        SideTitle = 'Learning loop visibility'
        SideText = 'The platform now feels like one connected progression experience: learn through modules, earn sub-badges, and track outcomes without losing context.'
        Stat1Title = 'Dashboard signal'
        Stat1Value = 'XP + badges + modules'
        StatColor1 = '#38BDF8'
        Stat2Title = 'User benefit'
        Stat2Value = 'Clear next steps'
        StatColor2 = '#60A5FA'
    }

    New-FeatureSlide $presentation 3 @{
        BandColor = '#0F766E'
        Eyebrow = 'TOURNAMENT CONTROL'
        Title = 'Tournament actions are safer, cleaner, and role-aware'
        Description = 'Competition flows now prevent duplicate actions, keep publishing in admin hands only, and show more trustworthy participant numbers by counting registered users only.'
        Bullets = @(
            'Publish buttons are exclusive to admins; players no longer see or trigger publish actions.',
            'Join and publish buttons disable after successful requests to prevent duplicate submissions.',
            'Participant totals now use REGISTERED state only, reducing misleading counts after withdrawals.'
        )
        SideTitle = 'Operational guardrails'
        SideText = 'This removes common demo and production failure modes: double joins, double publishes, and inflated participant counts.'
        Stat1Title = 'Publish control'
        Stat1Value = 'Admin only'
        StatColor1 = '#2DD4BF'
        Stat2Title = 'Count logic'
        Stat2Value = 'REGISTERED only'
        StatColor2 = '#34D399'
    }

    New-FeatureSlide $presentation 4 @{
        BandColor = '#B45309'
        Eyebrow = 'ADMIN OPERATIONS'
        Title = 'Admin tooling now covers activities, sports, and better defaults'
        Description = 'The admin workspace was extended beyond tournaments so staff can manage learning activities, sport definitions, and cleaner data defaults from the same application shell.'
        Bullets = @(
            'Admin activities are handled through module CRUD, with create, update, and delete support wired to the existing routes and APIs.',
            'New sports now default to the cricket icon so admin forms do not create empty visual states.',
            'The admin navigation and dashboards now better reflect the platform as an operations tool, not only a player portal.'
        )
        SideTitle = 'Fewer dead ends'
        SideText = 'Admins can create and manage core catalogue data without dropping into database-only workflows or patched demo data.'
        Stat1Title = 'Activity control'
        Stat1Value = 'CRUD enabled'
        StatColor1 = '#F59E0B'
        Stat2Title = 'Sport defaults'
        Stat2Value = 'Cricket icon prefilled'
        StatColor2 = '#FB923C'
    }

    New-FeatureSlide $presentation 5 @{
        BandColor = '#7C3AED'
        Eyebrow = 'IMPORT LAB'
        Title = 'Admins can upload spreadsheets and run H2 imports from the UI'
        Description = 'The original spreadsheet migration logic was refactored into a reusable service and exposed through an admin-only upload flow, so imports can now run from inside the product.'
        Bullets = @(
            'Import Lab lives in the admin navigation and accepts spreadsheet uploads from the browser.',
            'The backend now reuses the same spreadsheet import logic for startup-run migrations and admin uploads.',
            'Live validation confirmed the sample workbook imported 7 sheets and 99 rows, while player access was blocked with HTTP 403.'
        )
        SideTitle = 'Data ops inside the app'
        SideText = 'This closes the gap between technical migration tooling and the admin interface, which makes demos and local data refreshes much easier.'
        Stat1Title = 'Endpoint'
        Stat1Value = '/api/admin/imports/spreadsheets'
        StatColor1 = '#A78BFA'
        Stat2Title = 'Validation'
        Stat2Value = 'Admin pass  |  Player denied'
        StatColor2 = '#C084FC'
    }

    $slide6 = $presentation.Slides.Add(6, 12)
    $slide6.FollowMasterBackground = 0
    $slide6.Background.Fill.ForeColor.RGB = Get-OleColor '#0F172A'
    $closingBand = Add-Panel $slide6 0 0 720 42 '#14532D' '#14532D' 0.0
    $closingBand.Line.Visible = 0
    Add-TextBlock $slide6 'READY FOR DEMO AND HANDOFF' 34 56 220 18 11 '#86EFAC' 'Aptos' $true | Out-Null
    Add-TextBlock $slide6 'Local run state and demo talking points' 34 78 360 48 28 '#F8FAFC' 'Aptos Display' $true | Out-Null
    Add-TextBlock $slide6 'Use this deck alongside the live app to walk through the player experience first, then switch to the admin workspace for tournament controls, content management, and Import Lab.' 34 136 420 58 16 '#CBD5E1' 'Aptos' $false | Out-Null

    Add-Panel $slide6 34 220 300 170 '#111C2E' '#22314A' 0.0 | Out-Null
    Add-TextBlock $slide6 'Suggested demo order' 54 240 180 20 14 '#F8FAFC' 'Aptos' $true | Out-Null
    Add-TextBlock $slide6 "1. Home page feature sliders`r2. Badge and module progress`r3. Tournament admin controls`r4. Import Lab spreadsheet upload" 54 272 240 92 17 '#E2E8F0' 'Aptos' $false | Out-Null

    Add-Panel $slide6 364 220 322 170 '#111C2E' '#22314A' 0.0 | Out-Null
    Add-TextBlock $slide6 'Verified local environment' 384 240 200 20 14 '#F8FAFC' 'Aptos' $true | Out-Null
    Add-TextBlock $slide6 "Frontend: http://127.0.0.1:3000`rBackend: http://localhost:8080`rHealth: actuator status UP`rSpreadsheet import: sample workbook succeeded" 384 272 250 96 17 '#E2E8F0' 'Aptos' $false | Out-Null

    Add-Footer $slide6 6

    $presentation.SaveAs($OutputPath)
}
finally {
    if ($presentation -ne $null) {
        $presentation.Close()
    }
    if ($powerPoint -ne $null) {
        $powerPoint.Quit()
    }
}

Write-Output $OutputPath