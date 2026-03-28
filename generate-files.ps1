# Output directory
$outputDir = "D:\AI project\smart-archive-management-system\测试文件目录"

# Ensure output directory exists
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force
}

# Generate files
$totalFiles = 1000
Write-Host "Starting to generate $totalFiles business transaction documents..."

for ($i = 1; $i -le $totalFiles; $i++) {
    # Generate file names
    $fileName = "Document_$i"
    
    # Generate DOC file
    $docFilePath = Join-Path $outputDir "$fileName.doc"
    Write-Host "Generating DOC file: $docFilePath"
    "Business transaction document content" | Out-File -FilePath $docFilePath -Encoding UTF8
    
    # Generate PDF file
    $pdfFilePath = Join-Path $outputDir "$fileName.pdf"
    Write-Host "Generating PDF file: $pdfFilePath"
    "Business transaction document content" | Out-File -FilePath $pdfFilePath -Encoding UTF8
    
    if ($i % 100 -eq 0) {
        Write-Host "Generated $i files"
    }
}

Write-Host "Generation completed! Generated $totalFiles DOC files and $totalFiles PDF files."
