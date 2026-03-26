import http, { apiRequest } from '../http'
import type { DictionaryCategory, DictionaryItem } from '../../types'

export interface DictionaryCategoryCommand {
  categoryCode: string
  categoryName: string
  description?: string
  enabledFlag: 'Y' | 'N'
}

export interface DictionaryCategoryUpdateCommand {
  categoryName: string
  description?: string
  enabledFlag: 'Y' | 'N'
}

export interface DictionaryItemCommand {
  itemCode: string
  itemName: string
  itemValue?: string
  sortOrder: number
  enabledFlag: 'Y' | 'N'
}

export interface DictionaryItemUpdateCommand {
  itemName: string
  itemValue?: string
  sortOrder: number
  enabledFlag: 'Y' | 'N'
}

export function fetchDictionaryCategories() {
  return apiRequest<DictionaryCategory[]>(http.get('/api/base-data/dictionaries/categories'))
}

export function createDictionaryCategory(data: DictionaryCategoryCommand) {
  return apiRequest<DictionaryCategory>(http.post('/api/base-data/dictionaries/categories', data))
}

export function updateDictionaryCategory(categoryCode: string, data: DictionaryCategoryUpdateCommand) {
  return apiRequest<DictionaryCategory>(http.put(`/api/base-data/dictionaries/categories/${categoryCode}`, data))
}

export function deleteDictionaryCategory(categoryCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/dictionaries/categories/${categoryCode}`))
}

export function fetchDictionaryItems(categoryCode: string) {
  return apiRequest<DictionaryItem[]>(http.get(`/api/base-data/dictionaries/categories/${categoryCode}/items`))
}

export function createDictionaryItem(categoryCode: string, data: DictionaryItemCommand) {
  return apiRequest<DictionaryItem>(http.post(`/api/base-data/dictionaries/categories/${categoryCode}/items`, data))
}

export function updateDictionaryItem(categoryCode: string, itemCode: string, data: DictionaryItemUpdateCommand) {
  return apiRequest<DictionaryItem>(http.put(`/api/base-data/dictionaries/categories/${categoryCode}/items/${itemCode}`, data))
}

export function deleteDictionaryItem(categoryCode: string, itemCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/dictionaries/categories/${categoryCode}/items/${itemCode}`))
}
