import http, { apiRequest } from '../http'
import type {
  Warehouse,
  WarehouseArea,
  WarehouseLocation,
  WarehouseManagementResponse,
  WarehouseRack,
  WarehouseSummary,
  WarehouseVisualization
} from '../../types'

export interface WarehouseCreateCommand {
  warehouseCode: string
  warehouseName: string
  warehouseType: string
  managerName: string
  contactPhone?: string
  address?: string
  areaSize?: number
  photoUrl?: string
  status?: string
  description?: string
}

export interface WarehouseUpdateCommand extends WarehouseCreateCommand {}

export interface WarehouseCopyCommand {
  targetWarehouseCode: string
  targetWarehouseName: string
  managerName?: string
  contactPhone?: string
  address?: string
  areaSize?: number
  photoUrl?: string
  status?: string
  description?: string
}

export interface WarehouseAreaCreateCommand {
  warehouseCode: string
  areaCode: string
  areaName: string
  startX?: number
  startY?: number
  width?: number
  height?: number
  status?: string
}

export interface WarehouseAreaUpdateCommand {
  areaName: string
  startX?: number
  startY?: number
  width?: number
  height?: number
  status?: string
}

export interface WarehouseAreaCopyCommand {
  targetAreaCode: string
  targetAreaName: string
}

export interface WarehouseRackCreateCommand {
  warehouseCode: string
  areaCode: string
  rackCode: string
  rackName: string
  layerCount: number
  slotCount: number
  startX?: number
  startY?: number
}

export interface WarehouseRackUpdateCommand {
  rackName: string
  status?: string
  startX?: number
  startY?: number
}

export interface WarehouseRackCopyCommand {
  targetRackCode: string
  targetRackName: string
  targetAreaCode?: string
}

export interface WarehouseLocationCreateCommand {
  warehouseCode: string
  areaCode: string
  shelfCode: string
  layerCode: string
  locationCode: string
  locationName: string
  capacity: number
  x?: number
  y?: number
  width?: number
  height?: number
}

export interface WarehouseLocationUpdateCommand {
  locationName: string
  status: string
  capacity: number
  x?: number
  y?: number
  width?: number
  height?: number
}

export function fetchWarehouses() {
  return apiRequest<WarehouseSummary[]>(http.get('/api/base-data/warehouses'))
}

export function createWarehouse(data: WarehouseCreateCommand) {
  return apiRequest<Warehouse>(http.post('/api/base-data/warehouses', data))
}

export function updateWarehouse(warehouseCode: string, data: WarehouseUpdateCommand) {
  return apiRequest<Warehouse>(http.put(`/api/base-data/warehouses/${warehouseCode}`, data))
}

export function copyWarehouse(warehouseCode: string, data: WarehouseCopyCommand) {
  return apiRequest<Warehouse>(http.post(`/api/base-data/warehouses/${warehouseCode}/copy`, data))
}

export function deleteWarehouse(warehouseCode: string) {
  return apiRequest<boolean>(http.delete(`/api/base-data/warehouses/${warehouseCode}`))
}

export function createWarehouseArea(data: WarehouseAreaCreateCommand) {
  return apiRequest<WarehouseArea>(http.post('/api/base-data/warehouses/areas', data))
}

export function updateWarehouseArea(warehouseCode: string, areaCode: string, data: WarehouseAreaUpdateCommand) {
  return apiRequest<WarehouseArea>(http.put(`/api/base-data/warehouses/${warehouseCode}/areas/${areaCode}`, data))
}

export function copyWarehouseArea(warehouseCode: string, areaCode: string, data: WarehouseAreaCopyCommand) {
  return apiRequest<WarehouseArea>(http.post(`/api/base-data/warehouses/${warehouseCode}/areas/${areaCode}/copy`, data))
}

export function deleteWarehouseArea(warehouseCode: string, areaCode: string) {
  return apiRequest<boolean>(http.delete(`/api/base-data/warehouses/${warehouseCode}/areas/${areaCode}`))
}

export function createWarehouseRack(data: WarehouseRackCreateCommand) {
  return apiRequest<WarehouseRack>(http.post('/api/base-data/warehouses/racks', data))
}

export function updateWarehouseRack(warehouseCode: string, rackCode: string, data: WarehouseRackUpdateCommand) {
  return apiRequest<WarehouseRack>(http.put(`/api/base-data/warehouses/${warehouseCode}/racks/${rackCode}`, data))
}

export function copyWarehouseRack(warehouseCode: string, rackCode: string, data: WarehouseRackCopyCommand) {
  return apiRequest<WarehouseRack>(http.post(`/api/base-data/warehouses/${warehouseCode}/racks/${rackCode}/copy`, data))
}

export function deleteWarehouseRack(warehouseCode: string, rackCode: string) {
  return apiRequest<boolean>(http.delete(`/api/base-data/warehouses/${warehouseCode}/racks/${rackCode}`))
}

export function createWarehouseLocation(data: WarehouseLocationCreateCommand) {
  return apiRequest<WarehouseLocation>(http.post('/api/base-data/warehouses/locations', data))
}

export function updateWarehouseLocation(locationCode: string, data: WarehouseLocationUpdateCommand) {
  return apiRequest<WarehouseLocation>(http.put(`/api/base-data/warehouses/locations/${locationCode}`, data))
}

export function deleteWarehouseLocation(locationCode: string) {
  return apiRequest<boolean>(http.delete(`/api/base-data/warehouses/locations/${locationCode}`))
}

export function fetchWarehouseManagement(warehouseCode: string) {
  return apiRequest<WarehouseManagementResponse>(http.get(`/api/base-data/warehouses/${warehouseCode}/management`))
}

export function fetchWarehouseVisualization(warehouseCode: string) {
  return apiRequest<WarehouseVisualization>(http.get(`/api/base-data/warehouses/${warehouseCode}/visualization`))
}