<template>
  <div class="workflow-management-page">
    <el-card shadow="never" class="workspace-card">
      <template #header>
        <div class="section-head">
          <div>
            <strong>工作流管理</strong>
            <span>用于查看电子流待办、申请和参与记录，并直接进入移交详情完成审批。</span>
          </div>
          <div class="section-actions">
            <el-select v-model="selectedUserId" style="width: 220px" @change="loadData">
              <el-option
                v-for="user in users"
                :key="user.id"
                :label="`${user.name}（${user.role}）`"
                :value="user.id"
              />
            </el-select>
            <el-button type="primary" @click="openStartProcessDialog">发起流程</el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的待办" name="my-tasks">
          <el-table :data="myTasks" border class="task-table">
            <el-table-column label="任务名称" min-width="220">
              <template #default="{ row }">
                <el-button link type="primary" @click="openProcessDetailByTask(row)">{{ row.taskName }}</el-button>
              </template>
            </el-table-column>
            <el-table-column label="业务键" min-width="220">
              <template #default="{ row }">
                <el-button link @click="openProcessDetailByTask(row)">{{ row.businessKey || '-' }}</el-button>
              </template>
            </el-table-column>
            <el-table-column label="当前处理人" width="120">
              <template #default="{ row }">{{ resolveUserName(row.assignee) }}</template>
            </el-table-column>
            <el-table-column label="任务状态" width="120">
              <template #default="{ row }">
                <el-tag :type="taskStatusType(row.status)">{{ formatTaskStatus(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="displayUpdatedAt" label="最后更新时间" width="180" />
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="openCompleteDialog(row)">审批通过</el-button>
                <el-button type="danger" link @click="openRejectDialog(row)">驳回</el-button>
                <el-button link @click="openDelegateDialog(row)">转审</el-button>
                <el-button link @click="openProcessDetailByTask(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="myTasks.length === 0" description="当前没有待办任务" />
        </el-tab-pane>

        <el-tab-pane label="我的申请" name="my-processes">
          <el-table :data="myProcesses" border class="task-table">
            <el-table-column label="业务键" min-width="220">
              <template #default="{ row }">
                <el-button link type="primary" @click="viewProcess(row)">{{ row.businessKey }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="processDefinitionKey" label="流程定义" width="150" />
            <el-table-column label="流程状态" width="120">
              <template #default="{ row }">
                <el-tag :type="workflowStatusType(row.status)">{{ formatWorkflowStatus(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="initiatorName" label="发起人" width="120" />
            <el-table-column prop="displayUpdatedAt" label="最后更新时间" width="180" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="viewProcess(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="myProcesses.length === 0" description="当前没有申请记录" />
        </el-tab-pane>

        <el-tab-pane label="我的参与" name="participated-processes">
          <el-table :data="participatedProcesses" border class="task-table">
            <el-table-column label="业务键" min-width="220">
              <template #default="{ row }">
                <el-button link type="primary" @click="viewProcess(row)">{{ row.businessKey }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="processDefinitionKey" label="流程定义" width="150" />
            <el-table-column label="流程状态" width="120">
              <template #default="{ row }">
                <el-tag :type="workflowStatusType(row.status)">{{ formatWorkflowStatus(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="initiatorName" label="发起人" width="120" />
            <el-table-column prop="displayUpdatedAt" label="最后更新时间" width="180" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="viewProcess(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="participatedProcesses.length === 0" description="当前没有参与记录" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="startProcessVisible" title="发起流程" width="620px">
      <el-form label-width="120px">
        <el-form-item label="流程定义" required>
          <el-select v-model="startProcessForm.processDefinitionKey" placeholder="请选择流程定义">
            <el-option v-for="process in processDefinitions" :key="process.key" :label="process.name" :value="process.key" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务键" required>
          <el-input v-model="startProcessForm.businessKey" placeholder="例如 transfer_demo_001" />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-input v-model="startProcessForm.businessType" />
        </el-form-item>
        <el-form-item label="审批人" required>
          <el-select v-model="startProcessForm.assigneeId" placeholder="请选择审批人">
            <el-option
              v-for="user in approvalUsers"
              :key="user.id"
              :label="`${user.name}（${user.role}）`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startProcessVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStartProcess">确定发起</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="completeTaskVisible" title="审批通过" width="620px">
      <el-form label-width="120px">
        <el-form-item label="任务名称">
          <el-input :model-value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="completeTaskForm.comment" type="textarea" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeCompleteDialog">取消</el-button>
        <el-button type="primary" @click="submitCompleteTask">确认通过</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rejectTaskVisible" title="驳回任务" width="620px">
      <el-form label-width="120px">
        <el-form-item label="任务名称">
          <el-input :model-value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="驳回原因" required>
          <el-input v-model="rejectTaskForm.reason" type="textarea" :rows="4" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeRejectDialog">取消</el-button>
        <el-button type="danger" @click="submitRejectTask">确认驳回</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="delegateTaskVisible" title="转审任务" width="620px">
      <el-form label-width="120px">
        <el-form-item label="任务名称">
          <el-input :model-value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="转审人员" required>
          <el-select v-model="delegateTaskForm.assignee" placeholder="请选择转审人员">
            <el-option
              v-for="user in approvalUsers"
              :key="user.id"
              :label="`${user.name}（${user.role}）`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="转审意见" required>
          <el-input v-model="delegateTaskForm.reason" type="textarea" :rows="4" placeholder="请输入转审意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDelegateDialog">取消</el-button>
        <el-button type="primary" @click="submitDelegateTask">确认转审</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="processDetailVisible" title="电子流详情" width="1120px" top="4vh">
      <div v-if="currentProcess" class="detail-layout">
        <div class="detail-hero">
          <div>
            <div class="detail-title-row">
              <strong>{{ currentProcess.businessKey }}</strong>
              <el-tag :type="workflowStatusType(currentProcess.status)" effect="light">
                {{ formatWorkflowStatus(currentProcess.status) }}
              </el-tag>
            </div>
            <p>这里集中展示移交申请详情、流程记录和审批操作。</p>
          </div>
          <div class="detail-actions">
            <el-button v-if="currentActiveTask && canOperateCurrentTask" type="primary" @click="openCompleteDialog(currentActiveTask)">审批通过</el-button>
            <el-button v-if="currentActiveTask && canOperateCurrentTask" type="danger" plain @click="openRejectDialog(currentActiveTask)">驳回</el-button>
            <el-button v-if="currentActiveTask && canOperateCurrentTask" plain @click="openDelegateDialog(currentActiveTask)">转审</el-button>
          </div>
        </div>

        <div class="detail-grid">
          <div class="detail-main">
            <el-card shadow="never" class="detail-card">
              <template #header>
                <div class="card-head">
                  <strong>移交信息</strong>
                  <span>展示本次电子流携带的移交申请内容。</span>
                </div>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="流程实例ID">{{ currentProcess.processInstanceId }}</el-descriptions-item>
                <el-descriptions-item label="流程定义">{{ currentProcess.processDefinitionKey }}</el-descriptions-item>
                <el-descriptions-item label="发起人">{{ transferDetail.transferorName || currentProcess.initiatorName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="签收人">{{ transferDetail.assigneeName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="移交方式">{{ transferMethodLabel(transferDetail.transferMethod) }}</el-descriptions-item>
                <el-descriptions-item label="物流公司">{{ transferDetail.logisticsCompany || '-' }}</el-descriptions-item>
                <el-descriptions-item label="邮寄单号">{{ transferDetail.trackingNumber || '-' }}</el-descriptions-item>
                <el-descriptions-item label="申请时间">{{ currentProcess.displayStartTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="流程结束时间" :span="2">{{ currentProcess.displayEndTime || '未结束' }}</el-descriptions-item>
                <el-descriptions-item label="备注" :span="2">{{ transferDetail.remark || '无' }}</el-descriptions-item>
              </el-descriptions>
            </el-card>

            <el-card shadow="never" class="detail-card">
              <template #header>
                <div class="card-head">
                  <strong>移交文档</strong>
                  <span>文档类型和文档组织均展示名称，便于审批人直接核对。</span>
                </div>
              </template>
              <el-table :data="transferDetail.documents" border class="document-table">
                <el-table-column type="index" label="#" width="54" />
                <el-table-column prop="documentTypeName" label="文档类型" min-width="180" />
                <el-table-column prop="businessCode" label="业务编码" min-width="140" />
                <el-table-column prop="documentOrganizationName" label="文档组织" min-width="180" />
                <el-table-column label="扩展字段" min-width="300">
                  <template #default="{ row }">
                    <div v-if="row.extFieldEntries.length" class="ext-field-list">
                      <span v-for="field in row.extFieldEntries" :key="field.key" class="ext-chip">{{ field.key }}：{{ field.value || '-' }}</span>
                    </div>
                    <span v-else class="muted">无扩展字段</span>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!transferDetail.documents.length" description="当前流程没有携带移交文档明细" />
            </el-card>
          </div>

          <div class="detail-side">
            <el-card shadow="never" class="detail-card">
              <template #header>
                <div class="card-head">
                  <strong>当前处理</strong>
                  <span>便于审批人快速判断现在需要处理什么。</span>
                </div>
              </template>
              <div v-if="currentActiveTask" class="active-task">
                <div class="active-task__name">{{ currentActiveTask.taskName }}</div>
                <div class="active-task__meta">当前处理人：{{ resolveUserName(currentActiveTask.assignee) }}</div>
                <div class="active-task__meta">最后更新时间：{{ currentActiveTask.displayUpdatedAt || '-' }}</div>
                <div class="active-task__meta">状态：{{ formatTaskStatus(currentActiveTask.status) }}</div>
              </div>
              <el-empty v-else description="当前流程已结束，没有待处理任务" />
            </el-card>

            <el-card shadow="never" class="detail-card">
              <template #header>
                <div class="card-head">
                  <strong>流程记录</strong>
                  <span>按电子流最后更新时间展示任务轨迹。</span>
                </div>
              </template>
              <div v-if="processTasks.length" class="timeline-list">
                <div v-for="task in processTasks" :key="`${task.taskId}-${task.status}-${task.completeTime || ''}`" class="timeline-item">
                  <div class="timeline-dot" :class="`is-${taskStatusTone(task.status)}`"></div>
                  <div class="timeline-content">
                    <div class="timeline-top">
                      <strong>{{ task.taskName }}</strong>
                      <el-tag size="small" :type="taskStatusType(task.status)">{{ formatTaskStatus(task.status) }}</el-tag>
                    </div>
                    <div class="timeline-meta">处理人：{{ resolveUserName(task.assignee) }}</div>
                    <div class="timeline-meta">创建时间：{{ task.displayCreateTime || '-' }}</div>
                    <div class="timeline-meta">完成时间：{{ task.displayCompleteTime || '-' }}</div>
                    <div class="timeline-meta">最后更新时间：{{ task.displayUpdatedAt || '-' }}</div>
                  </div>
                </div>
              </div>
              <el-empty v-else description="当前流程暂无任务记录" />
            </el-card>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="processDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchArchiveCreateOptions } from '../../api/modules/archiveManagement'
import {
  completeTask,
  delegateTask,
  fetchMyProcesses,
  fetchMyTasks,
  fetchParticipatedProcesses,
  getProcessInstance,
  getProcessTasks,
  rejectTask,
  startProcess
} from '../../api/modules/workflow'
import type { ArchiveCreateOptions } from '../../types'

interface WorkflowUser {
  id: string
  name: string
  role: string
}

interface WorkflowTaskItem {
  taskId: string
  taskName: string
  processInstanceId: string
  businessKey?: string
  assignee?: string
  status?: string
  createTime?: string
  completeTime?: string
  displayCreateTime?: string
  displayCompleteTime?: string
  displayUpdatedAt?: string
  sortTime?: string
}

interface WorkflowProcessItem {
  processInstanceId: string
  processDefinitionKey: string
  businessKey: string
  status: string
  initiatorName: string
  startTime?: string
  endTime?: string
  lastUpdateDate?: string
  variables?: string
  displayStartTime?: string
  displayEndTime?: string
  displayUpdatedAt?: string
}

interface TransferDocumentView {
  documentTypeCode: string
  documentTypeName: string
  businessCode: string
  documentOrganizationCode: string
  documentOrganizationName: string
  extFieldEntries: Array<{ key: string; value: string }>
}

interface TransferDetailView {
  transferorName: string
  assigneeId: string
  assigneeName: string
  transferMethod: string
  logisticsCompany: string
  trackingNumber: string
  remark: string
  documents: TransferDocumentView[]
}

const activeTab = ref('my-tasks')
const myTasks = ref<WorkflowTaskItem[]>([])
const myProcesses = ref<WorkflowProcessItem[]>([])
const participatedProcesses = ref<WorkflowProcessItem[]>([])
const processTasks = ref<WorkflowTaskItem[]>([])
const currentTask = ref<WorkflowTaskItem | null>(null)
const currentProcess = ref<WorkflowProcessItem | null>(null)
const archiveOptions = ref<ArchiveCreateOptions | null>(null)

const selectedUserId = ref('1')
const users = ref<WorkflowUser[]>([
  { id: '1', name: '张三', role: '移交人' },
  { id: '2', name: '李四', role: '档案管理员' },
  { id: '3', name: '王五', role: '部门负责人' }
])
const processDefinitions = ref([{ key: 'documentTransfer', name: '文档移交流程' }])

const startProcessVisible = ref(false)
const completeTaskVisible = ref(false)
const rejectTaskVisible = ref(false)
const delegateTaskVisible = ref(false)
const processDetailVisible = ref(false)

const startProcessForm = ref({
  processDefinitionKey: 'documentTransfer',
  businessKey: '',
  businessType: 'TRANSFER',
  assigneeId: '2'
})

const completeTaskForm = ref({ comment: '' })
const rejectTaskForm = ref({ reason: '' })
const delegateTaskForm = ref({ assignee: '', reason: '' })

const currentUser = computed(() => users.value.find(user => user.id === selectedUserId.value) ?? users.value[0])
const approvalUsers = computed(() => users.value.filter(user => user.id !== selectedUserId.value))
const currentActiveTask = computed(() => processTasks.value.find(task => task.status === 'ACTIVE') ?? null)
const canOperateCurrentTask = computed(() => currentActiveTask.value?.assignee === selectedUserId.value)

const formatDateTime = (value?: string | null) => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const pad = (num: number) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

const sortByLatest = <T extends { sortTime?: string; lastUpdateDate?: string; endTime?: string; startTime?: string; createTime?: string }>(records: T[]) =>
  [...records].sort((a, b) => {
    const aTime = new Date(a.sortTime || a.lastUpdateDate || a.endTime || a.startTime || a.createTime || 0).getTime()
    const bTime = new Date(b.sortTime || b.lastUpdateDate || b.endTime || b.startTime || b.createTime || 0).getTime()
    return bTime - aTime
  })

const normalizeProcess = (process: WorkflowProcessItem): WorkflowProcessItem => ({
  ...process,
  displayStartTime: formatDateTime(process.startTime),
  displayEndTime: formatDateTime(process.endTime),
  displayUpdatedAt: formatDateTime(process.lastUpdateDate || process.endTime || process.startTime)
})

const normalizeTask = (task: WorkflowTaskItem, updatedAt?: string): WorkflowTaskItem => ({
  ...task,
  sortTime: updatedAt || task.completeTime || task.createTime,
  displayCreateTime: formatDateTime(task.createTime),
  displayCompleteTime: formatDateTime(task.completeTime),
  displayUpdatedAt: formatDateTime(updatedAt || task.completeTime || task.createTime)
})

const resolveOptionName = (key: 'documentTypes' | 'documentOrganizations', code?: string) => {
  if (!code) return '-'
  const options = archiveOptions.value?.[key] ?? []
  return options.find(option => option.code === code)?.name ?? code
}

const resolveUserName = (userId?: string) => {
  if (!userId) return '-'
  return users.value.find(user => user.id === String(userId))?.name ?? String(userId)
}

const workflowStatusType = (status?: string) => {
  switch (status) {
    case 'RUNNING':
      return 'warning'
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    default:
      return 'info'
  }
}

const formatWorkflowStatus = (status?: string) => {
  switch (status) {
    case 'RUNNING':
      return '处理中'
    case 'APPROVED':
      return '已通过'
    case 'REJECTED':
      return '已驳回'
    default:
      return status || '-'
  }
}

const taskStatusType = (status?: string) => {
  switch (status) {
    case 'ACTIVE':
      return 'warning'
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    case 'DELEGATED':
      return 'primary'
    default:
      return 'info'
  }
}

const taskStatusTone = (status?: string) => {
  switch (status) {
    case 'ACTIVE':
      return 'warning'
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    case 'DELEGATED':
      return 'primary'
    default:
      return 'info'
  }
}

const formatTaskStatus = (status?: string) => {
  switch (status) {
    case 'ACTIVE':
      return '待处理'
    case 'APPROVED':
      return '已通过'
    case 'REJECTED':
      return '已驳回'
    case 'DELEGATED':
      return '已转审'
    case 'COMPLETED':
      return '已完成'
    default:
      return status || '-'
  }
}

const transferMethodLabel = (value?: string) => {
  switch (value) {
    case 'DIRECT':
      return '直接移交'
    case 'MAIL':
      return '邮寄'
    default:
      return value || '-'
  }
}

const parseVariables = (variables?: string) => {
  if (!variables) return {}
  try {
    return JSON.parse(variables)
  } catch (error) {
    console.error('Failed to parse workflow variables:', error)
    return {}
  }
}

const transferDetail = computed<TransferDetailView>(() => {
  const emptyState: TransferDetailView = {
    transferorName: '',
    assigneeId: '',
    assigneeName: '',
    transferMethod: '',
    logisticsCompany: '',
    trackingNumber: '',
    remark: '',
    documents: []
  }

  if (!currentProcess.value?.variables) return emptyState

  const parsed = parseVariables(currentProcess.value.variables) as Record<string, any>
  const form = parsed.transferForm ?? {}
  const documents = Array.isArray(parsed.transferDocuments) ? parsed.transferDocuments : []
  const assigneeId = String(parsed.assigneeId ?? form.assigneeId ?? '')

  return {
    transferorName: parsed.initiatorName ?? currentProcess.value.initiatorName ?? '',
    assigneeId,
    assigneeName: resolveUserName(assigneeId),
    transferMethod: form.transferMethod ?? '',
    logisticsCompany: form.logisticsCompany ?? '',
    trackingNumber: form.trackingNumber ?? '',
    remark: form.remark ?? '',
    documents: documents.map((document: Record<string, unknown>) => {
      const documentTypeCode = String(document.documentTypeCode ?? '')
      const documentOrganizationCode = String(document.documentOrganizationCode ?? '')
      return {
        documentTypeCode,
        documentTypeName: resolveOptionName('documentTypes', documentTypeCode),
        businessCode: String(document.businessCode ?? ''),
        documentOrganizationCode,
        documentOrganizationName: resolveOptionName('documentOrganizations', documentOrganizationCode),
        extFieldEntries: Object.entries((document.extFields as Record<string, string>) ?? {}).map(([key, value]) => ({
          key,
          value: String(value ?? '')
        }))
      }
    })
  }
})

const loadData = async () => {
  try {
    const [options, tasks, processes, participated] = await Promise.all([
      fetchArchiveCreateOptions(),
      fetchMyTasks(selectedUserId.value),
      fetchMyProcesses(selectedUserId.value),
      fetchParticipatedProcesses(selectedUserId.value)
    ])

    archiveOptions.value = options

    const processIds = Array.from(new Set((tasks as WorkflowTaskItem[]).map(task => task.processInstanceId).filter(Boolean)))
    const processDetails = await Promise.all(
      processIds.map(async processInstanceId => {
        try {
          return await getProcessInstance(processInstanceId)
        } catch (error) {
          console.error(`Failed to load process ${processInstanceId}:`, error)
          return null
        }
      })
    )
    const processMap = new Map(processDetails.filter(Boolean).map(item => [item!.processInstanceId, item!]))

    myTasks.value = sortByLatest(
      (tasks as WorkflowTaskItem[]).map(task => normalizeTask(task, processMap.get(task.processInstanceId)?.lastUpdateDate))
    )
    myProcesses.value = sortByLatest((processes as WorkflowProcessItem[]).map(normalizeProcess))
    participatedProcesses.value = sortByLatest((participated as WorkflowProcessItem[]).map(normalizeProcess))
  } catch (error) {
    console.error('Failed to load workflow data:', error)
    ElMessage.error('加载工作流数据失败，请稍后重试')
  }
}

const openStartProcessDialog = () => {
  startProcessForm.value = {
    processDefinitionKey: 'documentTransfer',
    businessKey: `transfer_demo_${Date.now()}`,
    businessType: 'TRANSFER',
    assigneeId: approvalUsers.value[0]?.id ?? '2'
  }
  startProcessVisible.value = true
}

const submitStartProcess = async () => {
  if (!startProcessForm.value.businessKey.trim()) {
    ElMessage.warning('请输入业务键')
    return
  }
  if (!startProcessForm.value.assigneeId) {
    ElMessage.warning('请选择审批人')
    return
  }

  try {
    await startProcess({
      processDefinitionKey: startProcessForm.value.processDefinitionKey,
      businessKey: startProcessForm.value.businessKey.trim(),
      businessType: startProcessForm.value.businessType,
      initiatorId: currentUser.value.id,
      initiatorName: currentUser.value.name,
      variables: {
        assigneeId: startProcessForm.value.assigneeId,
        initiatorId: currentUser.value.id,
        initiatorName: currentUser.value.name,
        transferForm: {
          assigneeId: startProcessForm.value.assigneeId,
          transferMethod: 'DIRECT',
          remark: '从工作流管理页发起的测试流程'
        },
        transferDocuments: []
      }
    })
    ElMessage.success('流程已发起')
    startProcessVisible.value = false
    await loadData()
  } catch (error) {
    console.error('Failed to start process:', error)
    ElMessage.error('发起流程失败，请稍后重试')
  }
}

const openCompleteDialog = (task: WorkflowTaskItem) => {
  currentTask.value = task
  completeTaskForm.value.comment = ''
  completeTaskVisible.value = true
}

const closeCompleteDialog = () => {
  completeTaskVisible.value = false
  completeTaskForm.value.comment = ''
}

const submitCompleteTask = async () => {
  if (!currentTask.value) return
  try {
    await completeTask({
      taskId: currentTask.value.taskId,
      variables: {
        comment: completeTaskForm.value.comment.trim()
      }
    })
    ElMessage.success('审批通过成功')
    closeCompleteDialog()
    await loadData()
    if (processDetailVisible.value && currentProcess.value) {
      await viewProcess(currentProcess.value)
    }
  } catch (error) {
    console.error('Failed to complete task:', error)
    ElMessage.error('审批通过失败，请稍后重试')
  }
}

const openRejectDialog = (task: WorkflowTaskItem) => {
  currentTask.value = task
  rejectTaskForm.value.reason = ''
  rejectTaskVisible.value = true
}

const closeRejectDialog = () => {
  rejectTaskVisible.value = false
  rejectTaskForm.value.reason = ''
}

const submitRejectTask = async () => {
  if (!currentTask.value) return
  if (!rejectTaskForm.value.reason.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  try {
    await rejectTask({
      taskId: currentTask.value.taskId,
      reason: rejectTaskForm.value.reason.trim()
    })
    ElMessage.success('驳回成功')
    closeRejectDialog()
    await loadData()
    if (processDetailVisible.value && currentProcess.value) {
      await viewProcess(currentProcess.value)
    }
  } catch (error) {
    console.error('Failed to reject task:', error)
    ElMessage.error('驳回失败，请稍后重试')
  }
}

const openDelegateDialog = (task: WorkflowTaskItem) => {
  currentTask.value = task
  delegateTaskForm.value.assignee = approvalUsers.value.find(user => user.id !== task.assignee)?.id ?? ''
  delegateTaskForm.value.reason = ''
  delegateTaskVisible.value = true
}

const closeDelegateDialog = () => {
  delegateTaskVisible.value = false
  delegateTaskForm.value.assignee = ''
  delegateTaskForm.value.reason = ''
}

const submitDelegateTask = async () => {
  if (!currentTask.value) return
  if (!delegateTaskForm.value.assignee) {
    ElMessage.warning('请选择转审人员')
    return
  }
  if (!delegateTaskForm.value.reason.trim()) {
    ElMessage.warning('请输入转审意见')
    return
  }
  try {
    await delegateTask({
      taskId: currentTask.value.taskId,
      assignee: delegateTaskForm.value.assignee,
      reason: delegateTaskForm.value.reason.trim()
    })
    ElMessage.success('转审成功')
    closeDelegateDialog()
    await loadData()
    if (processDetailVisible.value && currentProcess.value) {
      await viewProcess(currentProcess.value)
    }
  } catch (error) {
    console.error('Failed to delegate task:', error)
    ElMessage.error('转审失败，请稍后重试')
  }
}

const decorateProcessTasks = (tasks: WorkflowTaskItem[], processStatus: string) =>
  sortByLatest(
    tasks.map(task => {
      let status = task.status || 'COMPLETED'
      if (status === 'COMPLETED') {
        if (processStatus === 'APPROVED') status = 'APPROVED'
        if (processStatus === 'REJECTED') status = 'REJECTED'
      }
      return normalizeTask({ ...task, status })
    })
  )

const viewProcess = async (process: WorkflowProcessItem) => {
  try {
    const [instance, tasks] = await Promise.all([
      getProcessInstance(process.processInstanceId),
      getProcessTasks(process.processInstanceId)
    ])
    currentProcess.value = normalizeProcess(instance as WorkflowProcessItem)
    processTasks.value = decorateProcessTasks(tasks as WorkflowTaskItem[], currentProcess.value.status)
    currentTask.value = currentActiveTask.value
    processDetailVisible.value = true
  } catch (error) {
    console.error('Failed to load process detail:', error)
    ElMessage.error('加载流程详情失败，请稍后重试')
  }
}

const openProcessDetailByTask = async (task: WorkflowTaskItem) => {
  currentTask.value = task
  await viewProcess({
    processInstanceId: task.processInstanceId,
    processDefinitionKey: '',
    businessKey: task.businessKey || '',
    status: 'RUNNING',
    initiatorName: ''
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.workflow-management-page {
  display: grid;
  gap: 16px;
}

.workspace-card,
.detail-card {
  border: 1px solid #e6eef8;
}

.section-head,
.detail-hero,
.detail-title-row,
.card-head,
.timeline-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.section-head {
  align-items: flex-start;
}

.section-head strong,
.card-head strong {
  display: block;
  font-size: 16px;
  color: #24324a;
}

.section-head span,
.card-head span,
.detail-hero p,
.timeline-meta,
.active-task__meta,
.muted {
  color: #7a879a;
  font-size: 12px;
}

.section-actions,
.detail-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.task-table,
.document-table {
  margin: 0;
}

.detail-layout,
.detail-main,
.detail-side,
.timeline-list {
  display: grid;
  gap: 16px;
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(320px, 1fr);
  gap: 16px;
}

.detail-hero {
  padding: 18px 20px;
  background: linear-gradient(135deg, #f4f8ff 0%, #f9fbff 100%);
  border: 1px solid #e6eef8;
  border-radius: 14px;
}

.detail-title-row strong,
.active-task__name {
  font-size: 18px;
  color: #1f2d3d;
}

.detail-hero p {
  margin: 8px 0 0;
}

.ext-field-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.ext-chip {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: #f3f7fd;
  color: #36506c;
  font-size: 12px;
}

.active-task {
  display: grid;
  gap: 8px;
}

.timeline-item {
  display: grid;
  grid-template-columns: 12px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-top: 6px;
  background: #d9e2ef;
}

.timeline-dot.is-warning {
  background: #e6a23c;
}

.timeline-dot.is-success {
  background: #67c23a;
}

.timeline-dot.is-danger {
  background: #f56c6c;
}

.timeline-dot.is-primary {
  background: #409eff;
}

.timeline-content {
  display: grid;
  gap: 6px;
  padding-bottom: 14px;
  border-bottom: 1px dashed #e7edf5;
}

@media (max-width: 1200px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
