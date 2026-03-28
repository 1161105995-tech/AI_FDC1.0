import http, { apiRequest } from '../http'

// 发起流程
interface StartProcessCommand {
  processDefinitionKey: string
  businessKey: string
  businessType?: string
  businessId?: number
  initiatorId: string
  initiatorName: string
  variables: Record<string, any>
}

// 完成任务
interface CompleteTaskCommand {
  taskId: string
  variables: Record<string, any>
}

// 驳回任务
interface RejectTaskCommand {
  taskId: string
  reason: string
}

// 转审任务
interface DelegateTaskCommand {
  taskId: string
  assignee: string
  reason: string
}

// 拆分流程
interface SplitProcessCommand {
  parentProcessInstanceId: string
  childProcessDefinitionKey: string
  childBusinessKeys: string[]
  businessType?: string
  businessId?: number
  initiatorId: string
  initiatorName: string
  variables: Record<string, any>
}

// 合并流程
interface MergeProcessesCommand {
  childProcessInstanceIds: string[]
  parentProcessDefinitionKey: string
  parentBusinessKey: string
  businessType?: string
  businessId?: number
  initiatorId: string
  initiatorName: string
  variables: Record<string, any>
}

export async function startProcess(command: StartProcessCommand) {
  return apiRequest(http.post('/api/workflow/processes', command))
}

export async function completeTask(command: CompleteTaskCommand) {
  return apiRequest(http.post(`/api/workflow/tasks/${command.taskId}/complete`, command))
}

export async function rejectTask(command: RejectTaskCommand) {
  return apiRequest(http.post(`/api/workflow/tasks/${command.taskId}/reject`, command))
}

export async function delegateTask(command: DelegateTaskCommand) {
  return apiRequest(http.post(`/api/workflow/tasks/${command.taskId}/delegate`, command))
}

export async function fetchMyTasks(userId: string) {
  return apiRequest(http.get('/api/workflow/tasks/my', { params: { userId } }))
}

export async function fetchMyProcesses(userId: string) {
  return apiRequest(http.get('/api/workflow/processes/my', { params: { userId } }))
}

export async function listProcesses(processDefinitionKey?: string) {
  return apiRequest(http.get('/api/workflow/processes', { params: { processDefinitionKey } }))
}

export async function fetchParticipatedProcesses(userId: string) {
  return apiRequest(http.get('/api/workflow/processes/participated', { params: { userId } }))
}

export async function splitProcess(command: SplitProcessCommand) {
  return apiRequest(http.post('/api/workflow/processes/split', command))
}

export async function mergeProcesses(command: MergeProcessesCommand) {
  return apiRequest(http.post('/api/workflow/processes/merge', command))
}

export async function getProcessInstance(processInstanceId: string) {
  return apiRequest(http.get(`/api/workflow/processes/${processInstanceId}`))
}

export async function getTask(taskId: string) {
  return apiRequest(http.get(`/api/workflow/tasks/${taskId}`))
}

export async function getProcessTasks(processInstanceId: string) {
  return apiRequest(http.get(`/api/workflow/processes/${processInstanceId}/tasks`))
}
