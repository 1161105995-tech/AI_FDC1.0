import http, { apiRequest } from '../http'
import type { AiCapability, RuleDefinition, SecurityPolicy, WorkflowDefinition } from '../../types'

export function fetchWorkflowDefinitions() {
  return apiRequest<WorkflowDefinition[]>(http.get('/api/workflows/definitions'))
}

export function fetchRules() {
  return apiRequest<RuleDefinition[]>(http.get('/api/rules'))
}

export function fetchAiCapabilities() {
  return apiRequest<AiCapability[]>(http.get('/api/ai/capabilities'))
}

export function fetchSecurityPolicies() {
  return apiRequest<SecurityPolicy[]>(http.get('/api/security/policies'))
}
