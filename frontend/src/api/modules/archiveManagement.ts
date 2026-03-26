import http, { apiRequest } from '../http'
import type {
  ArchiveAskResult,
  ArchiveAiModelSummary,
  ArchiveCreateOptions,
  ArchiveCreateSession,
  ArchiveDefaultResolve,
  ArchiveQueryResult,
  ArchiveRecordSummary,
  DocumentTypeExtField,
  LabelValueOption
} from '../../types'

export interface DocumentTypeExtFieldCreateCommand {
  fieldName: string
  fieldType: 'TEXT' | 'DICT'
  dictCategoryCode?: string
  requiredFlag: 'Y' | 'N'
  enabledFlag: 'Y' | 'N'
  formSortOrder: number
  queryEnabledFlag: 'Y' | 'N'
  querySortOrder: number
}

export interface ArchiveCreateCommand {
  sessionCode?: string
  createMode?: 'AUTO' | 'MANUAL'
  documentTypeCode: string
  companyProjectCode: string
  beginPeriod: string
  endPeriod: string
  businessCode?: string
  documentName: string
  dutyPerson: string
  dutyDepartment: string
  documentDate: string
  securityLevelCode: string
  sourceSystem?: string
  archiveDestination?: string
  originPlace?: string
  carrierTypeCode: 'ELECTRONIC' | 'PAPER' | 'HYBRID'
  remark?: string
  aiArchiveSummary?: string
  documentOrganizationCode: string
  retentionPeriodYears: number
  archiveTypeCode: string
  countryCode?: string
  customRule?: string
  extValues?: Record<string, string>
  paperInfo?: {
    plannedCopyCount?: number
    actualCopyCount?: number
    remark?: string
  }
}

export interface ArchiveQueryCommand {
  keyword?: string
  documentTypeCode?: string
  companyProjectCode?: string
  archiveTypeCode?: string
  carrierTypeCode?: string
  securityLevelCode?: string
  beginPeriod?: string
  endPeriod?: string
  documentName?: string
  businessCode?: string
  dutyPerson?: string
  archiveDestination?: string
  sourceSystem?: string
  documentOrganizationCode?: string
  extFilters?: Record<string, string>
}

export interface ArchiveAskCommand {
  question: string
  documentTypeCode?: string
  companyProjectCode?: string
}

export function fetchDocumentTypeExtFields(documentTypeCode: string) {
  return apiRequest<DocumentTypeExtField[]>(http.get(`/api/base-data/document-types/${documentTypeCode}/ext-fields`))
}

export function fetchEffectiveDocumentTypeExtFields(documentTypeCode: string) {
  return apiRequest<DocumentTypeExtField[]>(http.get(`/api/base-data/document-types/${documentTypeCode}/ext-fields/effective`))
}

export function createDocumentTypeExtField(documentTypeCode: string, data: DocumentTypeExtFieldCreateCommand) {
  return apiRequest<DocumentTypeExtField>(http.post(`/api/base-data/document-types/${documentTypeCode}/ext-fields`, data))
}

export function updateDocumentTypeExtField(documentTypeCode: string, fieldCode: string, data: DocumentTypeExtFieldCreateCommand) {
  return apiRequest<DocumentTypeExtField>(http.put(`/api/base-data/document-types/${documentTypeCode}/ext-fields/${fieldCode}`, data))
}

export function deleteDocumentTypeExtField(documentTypeCode: string, fieldCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/document-types/${documentTypeCode}/ext-fields/${fieldCode}`))
}

export function fetchArchiveCreateOptions() {
  return apiRequest<ArchiveCreateOptions>(http.get('/api/archive-management/create/options'))
}

export function resolveArchiveDefaults(params: {
  companyProjectCode: string
  documentTypeCode: string
  customRule?: string
  archiveDestination?: string
}) {
  return apiRequest<ArchiveDefaultResolve>(http.get('/api/archive-management/create/defaults', { params }))
}

export function createArchiveSession(createMode: 'AUTO' | 'MANUAL') {
  return apiRequest<ArchiveCreateSession>(http.post('/api/archive-management/create/sessions', { createMode }))
}

export function fetchArchiveSession(sessionCode: string) {
  return apiRequest<ArchiveCreateSession>(http.get(`/api/archive-management/create/sessions/${sessionCode}`))
}

export async function uploadArchiveAttachment(params: {
  sessionCode: string
  attachmentRole: 'ELECTRONIC' | 'PAPER_SCAN'
  attachmentTypeCode?: string
  remark?: string
  file: File
}) {
  const formData = new FormData()
  formData.append('attachmentRole', params.attachmentRole)
  if (params.attachmentTypeCode) formData.append('attachmentTypeCode', params.attachmentTypeCode)
  if (params.remark) formData.append('remark', params.remark)
  formData.append('file', params.file)
  return apiRequest(http.post(`/api/archive-management/create/sessions/${params.sessionCode}/attachments`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })) as Promise<any>
}

export function updateArchiveAttachment(params: {
  sessionCode: string
  attachmentId: number
  attachmentTypeCode?: string
  remark?: string
  aiSummary?: string
}) {
  return apiRequest(http.put(`/api/archive-management/create/sessions/${params.sessionCode}/attachments/${params.attachmentId}`, {
    attachmentTypeCode: params.attachmentTypeCode,
    remark: params.remark,
    aiSummary: params.aiSummary
  })) as Promise<any>
}

export function createArchive(data: ArchiveCreateCommand) {
  return apiRequest<ArchiveRecordSummary>(http.post('/api/archive-management/create/archives', data))
}

export function queryArchives(data: ArchiveQueryCommand) {
  return apiRequest<ArchiveQueryResult>(http.post('/api/archive-management/create/query', data))
}

export function askArchiveQuestion(data: ArchiveAskCommand) {
  return apiRequest<ArchiveAskResult>(http.post('/api/archive-management/create/ask', data))
}

export function fetchArchiveAiModels() {
  return apiRequest<ArchiveAiModelSummary[]>(http.get('/api/archive-management/ai-models'))
}
