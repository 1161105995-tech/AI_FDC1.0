import http, { apiRequest } from '../http'
import type {
  CountryOption,
  DocumentOrganizationCityOption,
  DocumentOrganizationDetail,
  DocumentOrganizationSummary
} from '../../types'

export interface DocumentOrganizationQuery {
  keyword?: string
  documentOrganizationName?: string
  countryCode?: string
  cityCode?: string
  enabledFlag?: string
}

export interface DocumentOrganizationCreateCommand {
  documentOrganizationCode: string
  documentOrganizationName: string
  description: string
  countryCode: string
  cityCode?: string
  enabledFlag: string
}

export interface DocumentOrganizationUpdateCommand {
  documentOrganizationName: string
  description: string
  countryCode: string
  cityCode?: string
  enabledFlag: string
}

export function fetchDocumentOrganizations(params: DocumentOrganizationQuery) {
  return apiRequest<DocumentOrganizationSummary[]>(http.get('/api/base-data/document-organizations', { params }))
}

export function fetchDocumentOrganizationDetail(documentOrganizationCode: string) {
  return apiRequest<DocumentOrganizationDetail>(http.get(`/api/base-data/document-organizations/${documentOrganizationCode}`))
}

export function fetchDocumentOrganizationCountries() {
  return apiRequest<CountryOption[]>(http.get('/api/base-data/document-organizations/countries'))
}

export function fetchDocumentOrganizationCities(countryCode?: string) {
  return apiRequest<DocumentOrganizationCityOption[]>(http.get('/api/base-data/document-organizations/cities', { params: { countryCode } }))
}

export function createDocumentOrganization(data: DocumentOrganizationCreateCommand) {
  return apiRequest<DocumentOrganizationDetail>(http.post('/api/base-data/document-organizations', data))
}

export function updateDocumentOrganization(documentOrganizationCode: string, data: DocumentOrganizationUpdateCommand) {
  return apiRequest<DocumentOrganizationDetail>(http.put(`/api/base-data/document-organizations/${documentOrganizationCode}`, data))
}

export function deleteDocumentOrganization(documentOrganizationCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/document-organizations/${documentOrganizationCode}`))
}
