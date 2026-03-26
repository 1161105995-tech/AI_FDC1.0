import http, { apiRequest } from '../http'
import type { ArchiveObject, ArchiveReceipt, CatalogTask } from '../../types'

export interface ArchiveQuery {
  keyword?: string
  archiveType?: string
  securityLevel?: string
  organizationName?: string
}

export interface ReceiptCreateCommand {
  sourceDept: string
  archiveTitle: string
  archiveType: string
  securityLevel: string
  submittedBy: string
}

export interface LocationAssignCommand {
  archiveObjectId: number
  locationCode: string
}

export function fetchArchiveObjects(params: ArchiveQuery) {
  return apiRequest<ArchiveObject[]>(http.get('/api/archive/objects', { params }))
}

export function fetchArchiveReceipts() {
  return apiRequest<ArchiveReceipt[]>(http.get('/api/archive/receipts'))
}

export function createArchiveReceipt(data: ReceiptCreateCommand) {
  return apiRequest<ArchiveReceipt>(http.post('/api/archive/receipts', data))
}

export function fetchCatalogTasks() {
  return apiRequest<CatalogTask[]>(http.get('/api/archive/catalog-tasks'))
}

export function assignArchiveLocation(data: LocationAssignCommand) {
  return apiRequest<boolean>(http.post('/api/archive/objects/assign-location', data))
}
