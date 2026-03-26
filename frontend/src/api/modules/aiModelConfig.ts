import http, { apiRequest } from '../http'
import type { AiModelConfig, AiModelConnectionTestResult } from '../../types'

export function fetchAiModelConfigs(keyword?: string) {
  return apiRequest<AiModelConfig[]>(http.get('/api/governance/ai-model-configs', { params: { keyword } }))
}

export function fetchAiModelConfigDetail(modelConfigId: number) {
  return apiRequest<AiModelConfig>(http.get(`/api/governance/ai-model-configs/${modelConfigId}`))
}

export function createAiModelConfig(data: AiModelConfig) {
  return apiRequest<AiModelConfig>(http.post('/api/governance/ai-model-configs', data))
}

export function updateAiModelConfig(modelConfigId: number, data: AiModelConfig) {
  return apiRequest<AiModelConfig>(http.put(`/api/governance/ai-model-configs/${modelConfigId}`, data))
}

export function testAiModelConnection(data: AiModelConfig) {
  return apiRequest<AiModelConnectionTestResult>(http.post('/api/governance/ai-model-configs/test-connection', data))
}

export function testSavedAiModelConnection(modelConfigId: number) {
  return apiRequest<AiModelConnectionTestResult>(http.post(`/api/governance/ai-model-configs/${modelConfigId}/test-connection`))
}

export function activateAiModelConfig(modelConfigId: number) {
  return apiRequest<AiModelConfig>(http.post(`/api/governance/ai-model-configs/${modelConfigId}/activate`))
}

export function deactivateAiModelConfig(modelConfigId: number) {
  return apiRequest<AiModelConfig>(http.post(`/api/governance/ai-model-configs/${modelConfigId}/deactivate`))
}
