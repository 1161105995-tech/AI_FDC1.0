import fs from 'node:fs'
import path from 'node:path'
import { execFileSync } from 'node:child_process'

const baseUrl = 'http://localhost:8080'
const testDir = 'D:/AI project/AI-search/测试文件'
const psql = 'C:/Program Files/PostgreSQL/18/bin/psql.exe'

async function postJson(url, body) {
  const response = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  })
  return response.json()
}

async function getJson(url) {
  const response = await fetch(url)
  return response.json()
}

async function uploadFile(sessionCode, filePath) {
  const form = new FormData()
  form.append('attachmentRole', 'ELECTRONIC')
  form.append('attachmentTypeCode', 'SUPPORTING_ATTACHMENT')
  form.append('remark', 'batch-verify')
  const fileBuffer = fs.readFileSync(filePath)
  form.append('file', new Blob([fileBuffer]), path.basename(filePath))
  const response = await fetch(`${baseUrl}/api/archive-management/create/sessions/${sessionCode}/attachments`, {
    method: 'POST',
    body: form
  })
  return response.json()
}

function queryCounts(archiveId) {
  const sql = `
select
  coalesce((select count(*) from arc_archive_content where archive_id = ${archiveId} and delete_flag = 'N'), 0) as content_count,
  coalesce((select count(*) from arc_archive_content_chunk where archive_id = ${archiveId} and delete_flag = 'N'), 0) as chunk_count,
  coalesce((select count(*) from arc_archive_chunk_vector where archive_id = ${archiveId} and delete_flag = 'N'), 0) as vector_count
`
  const output = execFileSync(psql, ['-h', 'localhost', '-U', 'postgres', '-d', 'smart_archive', '-t', '-A', '-F', '|', '-c', sql], {
    env: { ...process.env, PGPASSWORD: 'postgres' },
    encoding: 'utf8'
  }).trim()
  const [contentCount, chunkCount, vectorCount] = output.split('|').map(item => Number(item || 0))
  return { contentCount, chunkCount, vectorCount }
}

const files = fs.readdirSync(testDir)
  .filter(name => !name.startsWith('~$'))
  .map(name => path.join(testDir, name))
  .filter(file => fs.statSync(file).isFile())
  .sort((a, b) => a.localeCompare(b, 'zh-CN'))
const results = []

for (const filePath of files) {
  const fileName = path.basename(filePath)
  try {
    const session = await postJson(`${baseUrl}/api/archive-management/create/sessions`, { createMode: 'AUTO' })
    const sessionCode = session.data.sessionCode
    const upload = await uploadFile(sessionCode, filePath)
    if (!upload.success || !upload.data) {
      throw new Error(`upload failed: ${JSON.stringify(upload)}`)
    }
    const detail = await getJson(`${baseUrl}/api/archive-management/create/sessions/${sessionCode}`)
    const typeCode = detail.data.documentTypeCodeGuess || 'DOC'
    const defaults = await getJson(`${baseUrl}/api/archive-management/create/defaults?companyProjectCode=1987&documentTypeCode=${encodeURIComponent(typeCode)}&archiveDestination=BEIJING`)
    const baseNameHash = Math.abs([...fileName].reduce((sum, char) => sum * 31 + char.charCodeAt(0), 7))
    const create = await postJson(`${baseUrl}/api/archive-management/create/archives`, {
      sessionCode,
      createMode: 'AUTO',
      documentTypeCode: typeCode,
      companyProjectCode: '1987',
      beginPeriod: detail.data.aiParseResult?.beginPeriod || '2026-03',
      endPeriod: detail.data.aiParseResult?.endPeriod || '2026-03',
      businessCode: `AUTO-${baseNameHash}`,
      documentName: `AUTO_${baseNameHash}`,
      dutyPerson: 'AUTO_TESTER',
      dutyDepartment: 'ARCHIVE_QA',
      documentDate: '2026-03-26',
      securityLevelCode: defaults.data.securityLevelCode || 'INTERNAL',
      sourceSystem: 'BATCH_VERIFY',
      archiveDestination: defaults.data.archiveDestination || 'BEIJING',
      originPlace: 'Beijing',
      carrierTypeCode: 'ELECTRONIC',
      remark: 'batch-verify',
      aiArchiveSummary: null,
      documentOrganizationCode: defaults.data.documentOrganizationCode || 'CN-001',
      retentionPeriodYears: defaults.data.retentionPeriodYears || 10,
      archiveTypeCode: 'GENERAL',
      countryCode: defaults.data.countryCode || 'CN',
      extValues: detail.data.aiParseResult?.extendedValues || {}
    })
    if (!create.success || !create.data?.archiveId) {
      throw new Error(`create failed: ${JSON.stringify(create)}`)
    }
    const counts = queryCounts(create.data.archiveId)
    results.push({
      fileName,
      uploadSuccess: upload.success,
      guessedType: detail.data.documentTypeCodeGuess,
      previewLength: detail.data.aiParseResult?.extractedTextPreview?.length || 0,
      archiveId: create.data.archiveId,
      ...counts
    })
  } catch (error) {
    results.push({
      fileName,
      error: error instanceof Error ? error.message : String(error)
    })
  }
}

const outputPath = 'D:/AI project/smart-archive-management-system/frontend/archive-batch-verify-result.json'
fs.writeFileSync(outputPath, JSON.stringify(results, null, 2), 'utf8')
console.log(JSON.stringify({
  total: results.length,
  failed: results.filter(item => item.error).length,
  outputPath
}, null, 2))
