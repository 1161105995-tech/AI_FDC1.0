# 输出目录
$outputDir = "D:\AI project\AI-search\测试文件目录"

# 确保输出目录存在
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force
}

# 生成文件
$totalFiles = 1000
Write-Host "开始生成$totalFiles个商业交易文档..."

for ($i = 1; $i -le $totalFiles; $i++) {
    # 生成文件名
    $fileName = "文档_$i"
    
    # 生成DOC文件
    $docFilePath = Join-Path $outputDir "$fileName.doc"
    Write-Host "生成DOC文件: $docFilePath"
    "商业交易文档内容" | Out-File -FilePath $docFilePath -Encoding UTF8
    
    # 生成PDF文件
    $pdfFilePath = Join-Path $outputDir "$fileName.pdf"
    Write-Host "生成PDF文件: $pdfFilePath"
    "商业交易文档内容" | Out-File -FilePath $pdfFilePath -Encoding UTF8
    
    if ($i % 100 -eq 0) {
        Write-Host "已生成$i个文件"
    }
}

Write-Host "生成完成！共生成$totalFiles个DOC文件和$totalFiles个PDF文件。"
