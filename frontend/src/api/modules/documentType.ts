import http, { apiRequest } from '../http'
import type { AuditRecord, DocumentTypePermissionPreview, DocumentTypeTreeNode } from '../../types'

export interface DocumentTypeCreateCommand {
  typeCode: string
  typeName: string
  description?: string
  enabledFlag: string
  parentCode?: string
}

export interface DocumentTypeUpdateCommand {
  typeName: string
  description?: string
  enabledFlag: string
  parentCode?: string
}

export function fetchDocumentTypeTree() {
  return apiRequest<DocumentTypeTreeNode[]>(http.get('/api/base-data/document-types/tree'))
}

export function fetchDocumentTypeDetail(typeCode: string) {
  return apiRequest<DocumentTypeTreeNode>(http.get(`/api/base-data/document-types/${typeCode}`))
}

export function createDocumentType(data: DocumentTypeCreateCommand) {
  return apiRequest<DocumentTypeTreeNode>(http.post('/api/base-data/document-types', data))
}

export function updateDocumentType(typeCode: string, data: DocumentTypeUpdateCommand) {
  return apiRequest<DocumentTypeTreeNode>(http.put(`/api/base-data/document-types/${typeCode}`, data))
}

export function deleteDocumentType(typeCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/document-types/${typeCode}`))
}

export function fetchDocumentTypePermissionPreview() {
  return apiRequest<DocumentTypePermissionPreview>(http.get('/api/base-data/document-types/permissions/preview'))
}

export function fetchModuleAudits(moduleCode: string) {
  return apiRequest<AuditRecord[]>(http.get(`/api/common/audits/modules/${moduleCode}`))
}
