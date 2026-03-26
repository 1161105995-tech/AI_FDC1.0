import http, { apiRequest } from '../http'
import type {
  AuditRecord,
  CompanyProjectDetail,
  CompanyProjectOrgCategory,
  CompanyProjectSummary,
  CountryDictionaryItem,
  CountryOption,
  OrgCategoryDictionaryItem
} from '../../types'

export interface CompanyProjectLineCommand {
  orgCategory: string
  organizationCode: string
  organizationName: string
  validFlag: string
}

export interface CompanyProjectCreateCommand {
  companyProjectCode: string
  companyProjectName: string
  countryCode: string
  managementArea: string
  enabledFlag: string
  lines: CompanyProjectLineCommand[]
}

export interface CompanyProjectUpdateCommand {
  companyProjectName: string
  countryCode: string
  managementArea: string
  enabledFlag: string
  lines: CompanyProjectLineCommand[]
}

export interface CompanyProjectQuery {
  keyword?: string
  countryCode?: string
  managementArea?: string
  enabledFlag?: string
}

export interface CountryDictionaryCommand {
  countryCode: string
  countryName: string
  sortOrder: number
  enabledFlag: string
}

export interface OrgCategoryDictionaryCommand {
  categoryCode: string
  categoryName: string
  sortOrder: number
  enabledFlag: string
}

export function fetchCompanyProjects(params: CompanyProjectQuery) {
  return apiRequest<CompanyProjectSummary[]>(http.get('/api/base-data/company-projects', { params }))
}

export function fetchCompanyProjectDetail(companyProjectCode: string) {
  return apiRequest<CompanyProjectDetail>(http.get(`/api/base-data/company-projects/${companyProjectCode}`))
}

export function fetchCompanyProjectCountries() {
  return apiRequest<CountryOption[]>(http.get('/api/base-data/company-projects/countries'))
}

export function fetchCountryDictionaries() {
  return apiRequest<CountryDictionaryItem[]>(http.get('/api/base-data/company-projects/dictionary/countries'))
}

export function createCountryDictionary(data: CountryDictionaryCommand) {
  return apiRequest<CountryDictionaryItem>(http.post('/api/base-data/company-projects/dictionary/countries', data))
}

export function updateCountryDictionary(id: number, data: CountryDictionaryCommand) {
  return apiRequest<CountryDictionaryItem>(http.put(`/api/base-data/company-projects/dictionary/countries/${id}`, data))
}

export function deleteCountryDictionary(id: number) {
  return apiRequest<void>(http.delete(`/api/base-data/company-projects/dictionary/countries/${id}`))
}

export function fetchCompanyProjectOrgCategories() {
  return apiRequest<CompanyProjectOrgCategory[]>(http.get('/api/base-data/company-projects/org-categories'))
}

export function fetchOrgCategoryDictionaries() {
  return apiRequest<OrgCategoryDictionaryItem[]>(http.get('/api/base-data/company-projects/dictionary/org-categories'))
}

export function createOrgCategoryDictionary(data: OrgCategoryDictionaryCommand) {
  return apiRequest<OrgCategoryDictionaryItem>(http.post('/api/base-data/company-projects/dictionary/org-categories', data))
}

export function updateOrgCategoryDictionary(id: number, data: OrgCategoryDictionaryCommand) {
  return apiRequest<OrgCategoryDictionaryItem>(http.put(`/api/base-data/company-projects/dictionary/org-categories/${id}`, data))
}

export function deleteOrgCategoryDictionary(id: number) {
  return apiRequest<void>(http.delete(`/api/base-data/company-projects/dictionary/org-categories/${id}`))
}

export function createCompanyProject(data: CompanyProjectCreateCommand) {
  return apiRequest<CompanyProjectDetail>(http.post('/api/base-data/company-projects', data))
}

export function updateCompanyProject(companyProjectCode: string, data: CompanyProjectUpdateCommand) {
  return apiRequest<CompanyProjectDetail>(http.put(`/api/base-data/company-projects/${companyProjectCode}`, data))
}

export function deleteCompanyProject(companyProjectCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/company-projects/${companyProjectCode}`))
}

export function fetchModuleAudits(moduleCode: string) {
  return apiRequest<AuditRecord[]>(http.get(`/api/common/audits/modules/${moduleCode}`))
}