import http, { apiRequest } from '../http'
import type { BorrowRecord, DisposalRecord, InventoryTask } from '../../types'

export interface BorrowCreateCommand {
  archiveCode: string
  archiveTitle: string
  borrower: string
  borrowType: string
  expectedReturnDate: string
}

export interface InventoryCreateCommand {
  warehouseCode: string
  inventoryScope: string
  owner: string
  dueDate: string
}

export interface DisposalCreateCommand {
  archiveCode: string
  archiveTitle: string
  retentionPeriod: string
  appraisalConclusion: string
}

export function fetchBorrowRecords() {
  return apiRequest<BorrowRecord[]>(http.get('/api/archive/borrow-records'))
}

export function createBorrowRecord(data: BorrowCreateCommand) {
  return apiRequest<BorrowRecord>(http.post('/api/archive/borrow-records', data))
}

export function fetchInventoryTasks() {
  return apiRequest<InventoryTask[]>(http.get('/api/archive/inventory-tasks'))
}

export function createInventoryTask(data: InventoryCreateCommand) {
  return apiRequest<InventoryTask>(http.post('/api/archive/inventory-tasks', data))
}

export function fetchDisposalRecords() {
  return apiRequest<DisposalRecord[]>(http.get('/api/archive/disposal-records'))
}

export function createDisposalRecord(data: DisposalCreateCommand) {
  return apiRequest<DisposalRecord>(http.post('/api/archive/disposal-records', data))
}