# PowerShell Script to Generate an NFR-Focused PowerPoint Presentation

# --- Helper Function ---
function Convert-HexToOleColor {
    param([string]$hexColor)
    $hexColor = $hexColor.TrimStart('#')
    $r = [System.Convert]::ToInt32($hexColor.Substring(0, 2), 16)
    $g = [System.Convert]::ToInt32($hexColor.Substring(2, 2), 16)
    $b = [System.Convert]::ToInt32($hexColor.Substring(4, 2), 16)
    return $b * 65536 + $g * 256 + $r
}

# --- Configuration ---
$output_filename = "wishaw-platform-nfr-showcase.pptx"
$output_directory = "c:\Users\5613574\Downloads\ti-nauts_Hackathon_projects\51a7db-tfg_hack_wishaw-java\docs"
$full_output_path = Join-Path -Path $output_directory -ChildPath $output_filename

# Theme Colors
$theme = @{
    "background" = Convert-HexToOleColor -hexColor "#F8F9FA" # Very Light Gray
    "title"      = Convert-HexToOleColor -hexColor "#212529" # Near Black
    "subtitle"   = Convert-HexToOleColor -hexColor "#005A9C" # Professional Blue
    "text"       = Convert-HexToOleColor -hexColor "#495057" # Gray
    "accent"     = Convert-HexToOleColor -hexColor "#007BFF" # Bright Blue
}

# --- Slide Content ---
$slides_data = @(
    @{
        "title"    = "Wishaw Platform: Non-Functional Requirements"
        "subtitle" = "A Technical Deep Dive into Performance, Security, and Scalability"
        "layout"   = "title"
    },
    @{
        "title"  = "Frontend NFRs: Performance & Usability"
        "points" = @(
            "Performance: Achieved fast load times with Vite's optimized bundling and tree-shaking. Target First Contentful Paint (FCP) is under 1.8 seconds.",
            "Accessibility (A11y): Designed for WCAG 2.1 AA compliance, using semantic HTML, ARIA labels, and ensuring full keyboard navigability.",
            "Responsiveness: A mobile-first approach with Tailwind CSS provides a fluid and consistent user experience across all device sizes.",
            "Usability: Features a consistent and intuitive UI, with clear visual feedback for all user interactions to minimize cognitive load."
        )
    },
    @{
        "title"  = "Backend NFRs: Security & Scalability"
        "points" = @(
            "Security: Hardened with Spring Security, enforcing JWT-based authentication and fine-grained Role-Based Access Control (RBAC) on all API endpoints.",
            "Scalability: Built on a stateless service architecture, allowing for easy horizontal scaling by deploying additional container instances.",
            "Reliability: Implements robust error handling and structured logging (Logback) to ensure high availability and quick issue diagnosis.",
            "Maintainability: Adheres to clean code principles with a clear separation of concerns, making the codebase easy to understand and extend."
        )
    },
    @{
        "title"  = "Database NFRs: Integrity & Efficiency"
        "points" = @(
            "Data Integrity: The schema design enforces data consistency and referential integrity where applicable.",
            "Performance: Leverages an in-memory H2 database for extremely fast query execution, ideal for the application's access patterns.",
            "Flexibility: The data import mechanism dynamically adapts to spreadsheet structures, allowing for flexible data model updates without code changes.",
            "Portability: As an in-memory database, the entire application state is self-contained, simplifying deployment and testing."
        )
    },
    @{
        "title"  = "DevOps NFRs: Automation & Observability"
        "points" = @(
            "Continuous Integration (CI): Automated build and test pipeline runs on every commit to ensure code quality and prevent regressions.",
            "Continuous Deployment (CD): A blue-green deployment strategy is configured to enable zero-downtime releases into production.",
            "Infrastructure as Code (IaC): The entire environment is defined as code (e.g., Terraform), ensuring consistent and repeatable deployments.",
            "Observability: The application exposes health and metrics endpoints via Spring Actuator, enabling comprehensive monitoring and alerting."
        )
    },
    @{
        "title"    = "Thank You"
        "subtitle" = "Questions are welcome."
        "layout"   = "section"
    }
)

# --- Presentation Generation ---
try {
    # Check if PowerPoint is installed
    $ppt_app = New-Object -ComObject PowerPoint.Application
    $ppt_app.Visible = [Microsoft.Office.Core.MsoTriState]::msoTrue # Make window visible

    # Create a new presentation
    $presentation = $ppt_app.Presentations.Add()
    $presentation.SlideMaster.Background.Fill.ForeColor.RGB = $theme.background

    foreach ($slide_info in $slides_data) {
        $layout_type = switch ($slide_info.layout) {
            "title"   { 1 } # ppLayoutTitle
            "section" { 7 } # ppLayoutTitleOnly
            default   { 2 } # ppLayoutTitleAndContent
        }

        $slide = $presentation.Slides.Add($presentation.Slides.Count + 1, $layout_type)
        $slide.Shapes.Title.TextFrame.TextRange.Text = $slide_info.title
        $slide.Shapes.Title.TextFrame.TextRange.Font.Color.RGB = $theme.title
        $slide.Shapes.Title.TextFrame.TextRange.Font.Size = 42
        $slide.Shapes.Title.TextFrame.TextRange.Font.Bold = $true

        # Process bullet points first, only if the placeholder exists
        if ($slide_info.points -and $slide.Shapes.Placeholders.Count -ge 2) {
            $content_frame = $slide.Shapes.Placeholders(2).TextFrame
            $content_frame.TextRange.Text = "" # Clear placeholder
            foreach ($point in $slide_info.points) {
                $new_paragraph = $content_frame.TextRange.InsertAfter($point + "`r`n")
                $new_paragraph.Font.Color.RGB = $theme.text
                $new_paragraph.Font.Size = 22
                $new_paragraph.ParagraphFormat.Bullet.Type = 1 # ppBulletUnnumbered
                $new_paragraph.ParagraphFormat.SpaceAfter = 12
                $new_paragraph.IndentLevel = 1
            }
        }

        # Process subtitle based on layout type
        if ($slide_info.subtitle) {
            if ($layout_type -eq 1 -and $slide.Shapes.Placeholders.Count -ge 2) { # Title Slide (ppLayoutTitle)
                $placeholder = $slide.Shapes.Placeholders(2)
                $placeholder.TextFrame.TextRange.Text = $slide_info.subtitle
                $placeholder.TextFrame.TextRange.Font.Color.RGB = $theme.subtitle
                $placeholder.TextFrame.TextRange.Font.Size = 26
            } elseif ($layout_type -eq 7) { # Section Header (ppLayoutTitleOnly)
                $shape = $slide.Shapes.AddTextbox(1, 100, 250, 750, 100) # msoTextOrientationHorizontal
                $shape.TextFrame.TextRange.Text = $slide_info.subtitle
                $shape.TextFrame.TextRange.Font.Color.RGB = $theme.subtitle
                $shape.TextFrame.TextRange.Font.Size = 32
                $shape.TextFrame.TextRange.ParagraphFormat.Alignment = 2 # ppAlignCenter
            }
        }
    }

    $presentation.SaveAs($full_output_path)
    $presentation.Close()
    Write-Host "Successfully generated NFR presentation: $full_output_path"
}
catch {
    Write-Error "An error occurred: $($_.Exception.Message)"
}
finally {
    if ($ppt_app) {
        $ppt_app.Quit()
        [System.Runtime.InteropServices.Marshal]::ReleaseComObject($ppt_app) | Out-Null
    }
}
