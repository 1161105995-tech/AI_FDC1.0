import http, { apiRequest } from '../http'
import type { DashboardSummary } from '../../types'

export function fetchDashboardSummary() {
  return apiRequest<DashboardSummary>(http.get('/api/dashboard/summary'))
}
