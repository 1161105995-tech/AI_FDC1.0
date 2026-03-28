param(
    [string]$DbHost = "localhost",
    [string]$DbName = "smart_archive",
    [string]$DbUser = "postgres",
    [string]$DbPassword = "postgres",
    [string]$PostgresBin = "C:\Program Files\PostgreSQL\18\bin",
    [string]$BackendPort = "8080",
    [switch]$SkipRestart,
    [switch]$SkipFileDelete
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendRoot = Join-Path $repoRoot "backend"
$storageRoot = Join-Path $backendRoot "storage\archive-files"
$javaExe = "D:\java\jdk-17.0.11+9\bin\java.exe"
$javaRunArgs = if (Test-Path "X:\java-run.args") { "X:\java-run.args" } else { Join-Path $backendRoot "java-run.args" }
$stdoutLog = if (Test-Path "X:\") { "X:\backend-workflow.log" } else { Join-Path $backendRoot "backend-workflow.log" }
$stderrLog = if (Test-Path "X:\") { "X:\backend-workflow.err.log" } else { Join-Path $backendRoot "backend-workflow.err.log" }
$psqlExe = Join-Path $PostgresBin "psql.exe"

if (-not (Test-Path $psqlExe)) {
    throw "psql.exe not found: $psqlExe"
}

function Invoke-Psql {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Sql
    )

    $env:PGPASSWORD = $DbPassword
    try {
        & $psqlExe -h $DbHost -U $DbUser -d $DbName -v ON_ERROR_STOP=1 -c $Sql
    }
    finally {
        Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
    }
}

Write-Host "1/4 Stop backend service..."
$backendPids = Get-NetTCPConnection -LocalPort $BackendPort -State Listen -ErrorAction SilentlyContinue |
    Select-Object -ExpandProperty OwningProcess -Unique
foreach ($backendProcessId in $backendPids) {
    Stop-Process -Id $backendProcessId -Force
}

Write-Host "2/4 Truncate archive, attachment, vector, transfer, and workflow instance data..."
$truncateSql = @'
DO $$
DECLARE
    target_tables text[] := ARRAY[
        'arc_archive_chunk_vector',
        'arc_archive_content_chunk',
        'arc_archive_content',
        'arc_archive_attachment',
        'arc_archive_ext_value',
        'arc_archive_paper',
        'arc_archive_ai_task',
        'arc_archive',
        'arc_archive_create_session',
        'arc_archive_object',
        'arc_archive_receipt',
        'arc_borrow_record',
        'arc_catalog_task',
        'arc_disposal_record',
        'arc_inventory_task',
        'wf_workflow_history',
        'wf_workflow_task',
        'wf_workflow_instance',
        'act_hi_actinst',
        'act_hi_attachment',
        'act_hi_comment',
        'act_hi_detail',
        'act_hi_entitylink',
        'act_hi_identitylink',
        'act_hi_procinst',
        'act_hi_taskinst',
        'act_hi_tsk_log',
        'act_hi_varinst',
        'act_ru_actinst',
        'act_ru_deadletter_job',
        'act_ru_entitylink',
        'act_ru_event_subscr',
        'act_ru_execution',
        'act_ru_external_job',
        'act_ru_history_job',
        'act_ru_identitylink',
        'act_ru_job',
        'act_ru_suspended_job',
        'act_ru_task',
        'act_ru_timer_job',
        'act_ru_variable',
        'act_evt_log'
    ];
    sql_text text;
BEGIN
    SELECT 'TRUNCATE TABLE ' || string_agg(format('%I', tablename), ', ') || ' RESTART IDENTITY CASCADE'
      INTO sql_text
      FROM pg_tables
     WHERE schemaname = 'public'
       AND tablename = ANY(target_tables);

    IF sql_text IS NOT NULL THEN
        EXECUTE sql_text;
    END IF;
END $$;
'@
Invoke-Psql -Sql $truncateSql

if (-not $SkipFileDelete) {
    Write-Host "3/4 Delete stored archive files..."
    if (Test-Path $storageRoot) {
        Get-ChildItem -Path $storageRoot -Force | Remove-Item -Recurse -Force
    }
    else {
        Write-Host "Storage directory not found, skipping file deletion."
    }
}
else {
    Write-Host "3/4 Skip file deletion."
}

if (-not $SkipRestart) {
    Write-Host "4/4 Restart backend service..."
    if (-not (Test-Path $javaExe)) {
        throw "Java executable not found: $javaExe"
    }
    if (-not (Test-Path $javaRunArgs)) {
        throw "Java run args file not found: $javaRunArgs"
    }

    Start-Process -FilePath $javaExe `
        -ArgumentList "@$javaRunArgs" `
        -WorkingDirectory $(if (Test-Path "X:\") { "X:\" } else { $backendRoot }) `
        -RedirectStandardOutput $stdoutLog `
        -RedirectStandardError $stderrLog | Out-Null
}
else {
    Write-Host "4/4 Skip backend restart."
}

Write-Host "Verification:"
$verifySql = @'
SELECT 'arc_archive' AS table_name, COUNT(*)::bigint AS total FROM arc_archive
UNION ALL
SELECT 'arc_archive_attachment', COUNT(*)::bigint FROM arc_archive_attachment
UNION ALL
SELECT 'arc_archive_chunk_vector', COUNT(*)::bigint FROM arc_archive_chunk_vector
UNION ALL
SELECT 'arc_archive_receipt', COUNT(*)::bigint FROM arc_archive_receipt
UNION ALL
SELECT 'wf_workflow_instance', COUNT(*)::bigint FROM wf_workflow_instance
UNION ALL
SELECT 'wf_workflow_task', COUNT(*)::bigint FROM wf_workflow_task
UNION ALL
SELECT 'act_ru_execution', COUNT(*)::bigint FROM act_ru_execution
UNION ALL
SELECT 'act_ru_task', COUNT(*)::bigint FROM act_ru_task
UNION ALL
SELECT 'act_hi_procinst', COUNT(*)::bigint FROM act_hi_procinst
ORDER BY table_name;
'@
Invoke-Psql -Sql $verifySql

Write-Host "Cleanup completed."
