param(
    [switch]$Foreground
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$pythonExe = "python"
$scriptPath = Join-Path $repoRoot "scripts\bulk_import_archives.py"
$outputDir = Join-Path $repoRoot "import-results"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$stdoutLog = Join-Path $outputDir "bulk-import-$timestamp.log"
$stderrLog = Join-Path $outputDir "bulk-import-$timestamp.err.log"

if (-not (Test-Path $outputDir)) {
    New-Item -Path $outputDir -ItemType Directory | Out-Null
}

if (-not (Test-Path $scriptPath)) {
    throw "Import script not found: $scriptPath"
}

$arguments = @("""$scriptPath""")

if ($Foreground) {
    & $pythonExe @arguments
    exit $LASTEXITCODE
}

$process = Start-Process -FilePath $pythonExe `
    -ArgumentList $arguments `
    -WorkingDirectory $repoRoot `
    -RedirectStandardOutput $stdoutLog `
    -RedirectStandardError $stderrLog `
    -PassThru

Write-Output "PID=$($process.Id)"
Write-Output "STDOUT=$stdoutLog"
Write-Output "STDERR=$stderrLog"
