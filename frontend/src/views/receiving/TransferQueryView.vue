<template>
  <div class="transfer-query-page">
    <el-card shadow="never">
      <template #header>
        <div class="section-head">
          <div>
            <strong>移交记录查询</strong>
            <span>展示真实电子流移交记录，支持按条件筛选和查看详情。</span>
          </div>
          <div class="section-actions">
            <el-button type="primary" @click="goToCreate">发起移交</el-button>
          </div>
        </div>
      </template>

      <div class="search-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="移交编号">
            <el-input v-model="searchForm.transferId" placeholder="请输入移交编号" clearable />
          </el-form-item>
          <el-form-item label="移交方式">
            <el-select v-model="searchForm.transferMethod" placeholder="请选择移交方式" clearable>
              <el-option label="直接移交" value="DIRECT" />
              <el-option label="邮寄" value="MAIL" />
            </el-select>
          </el-form-item>
          <el-form-item label="业务编码">
            <el-input v-model="searchForm.businessCode" placeholder="请输入业务编码" clearable />
          </el-form-item>
          <el-form-item label="移交人">
            <el-select v-model="searchForm.transferorId" filterable placeholder="请选择移交人" clearable>
              <el-option v-for="user in userOptions" :key="user.id" :label="user.name" :value="user.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="签收人">
            <el-select v-model="searchForm.assigneeId" filterable placeholder="请选择签收人" clearable>
              <el-option v-for="user in userOptions" :key="user.id" :label="user.name" :value="user.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="处理中" value="RUNNING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已驳回" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              value-format="YYYY-MM-DD"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="search">查询</el-button>
            <el-button @click="reset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="8" animated />
      </div>

      <div v-else class="transfer-list">
        <el-table :data="pagedRecords" border class="transfer-table">
          <el-table-column label="移交编号" min-width="200">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewDetail(row.processInstanceId)">{{ row.id }}</el-button>
            </template>
          </el-table-column>
          <el-table-column prop="transferorName" label="移交人" width="120" />
          <el-table-column prop="assigneeName" label="签收人" width="120" />
          <el-table-column label="移交方式" width="120">
            <template #default="{ row }">
              <el-tag :type="row.transferMethod === 'DIRECT' ? 'primary' : 'success'" effect="light">
                {{ row.transferMethod === 'DIRECT' ? '直接移交' : '邮寄' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="light">
                {{ getStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="documentCount" label="文档数量" width="100" />
          <el-table-column prop="displayUpdatedAt" label="最后更新时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="viewDetail(row.processInstanceId)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="filteredRecords.length"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>

      <el-dialog v-model="detailDialogVisible" title="移交详情" width="980px" :show-close="true">
        <div v-if="currentTransferDetail" class="transfer-detail">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="移交编号">{{ currentTransferDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="流程实例ID">{{ currentTransferDetail.processInstanceId }}</el-descriptions-item>
            <el-descriptions-item label="移交人">{{ currentTransferDetail.transferorName }}</el-descriptions-item>
            <el-descriptions-item label="签收人">{{ currentTransferDetail.assigneeName }}</el-descriptions-item>
            <el-descriptions-item label="移交方式">
              <el-tag :type="currentTransferDetail.transferMethod === 'DIRECT' ? 'primary' : 'success'" effect="light">
                {{ currentTransferDetail.transferMethod === 'DIRECT' ? '直接移交' : '邮寄' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="流程状态">
              <el-tag :type="getStatusType(currentTransferDetail.status)" effect="light">
                {{ getStatusLabel(currentTransferDetail.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentTransferDetail.displayCreateTime }}</el-descriptions-item>
            <el-descriptions-item label="最后更新时间">{{ currentTransferDetail.displayUpdatedAt }}</el-descriptions-item>
            <el-descriptions-item v-if="currentTransferDetail.transferMethod === 'MAIL'" label="物流公司">
              {{ currentTransferDetail.logisticsCompany || '无' }}
            </el-descriptions-item>
            <el-descriptions-item v-if="currentTransferDetail.transferMethod === 'MAIL'" label="邮寄单号">
              {{ currentTransferDetail.trackingNumber || '无' }}
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ currentTransferDetail.remark || '无' }}</el-descriptions-item>
          </el-descriptions>

          <div class="documents-section">
            <h3>移交文档</h3>
            <el-table :data="currentTransferDetail.documents" border class="documents-table">
              <el-table-column type="index" label="#" width="54" />
              <el-table-column prop="documentTypeName" label="文档类型" min-width="180" />
              <el-table-column prop="businessCode" label="业务编码" min-width="140" />
              <el-table-column prop="documentOrganizationName" label="文档组织" min-width="180" />
              <el-table-column label="扩展字段" min-width="280">
                <template #default="{ row }">
                  <div v-if="row.extFieldEntries.length" class="ext-field-list">
                    <span v-for="field in row.extFieldEntries" :key="field.key" class="ext-chip">{{ field.key }}：{{ field.value || '-' }}</span>
                  </div>
                  <span v-else>无扩展字段</span>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="documents-section">
            <h3>电子流审批记录</h3>
            <div v-if="processTasks.length" class="timeline-list">
              <div v-for="task in processTasks" :key="`${task.taskId}-${task.status}-${task.completeTime || ''}`" class="timeline-item">
                <div class="timeline-dot" :class="`is-${getTaskTone(task.status)}`"></div>
                <div class="timeline-content">
                  <div class="timeline-top">
                    <strong>{{ task.taskName }}</strong>
                    <el-tag size="small" :type="getTaskType(task.status)">{{ getTaskLabel(task.status) }}</el-tag>
                  </div>
                  <div class="timeline-meta">处理人：{{ resolveUserName(task.assignee) }}</div>
                  <div class="timeline-meta">创建时间：{{ formatDateTime(task.createTime) || '-' }}</div>
                  <div class="timeline-meta">完成时间：{{ formatDateTime(task.completeTime) || '-' }}</div>
                </div>
              </div>
            </div>
            <el-empty v-else description="当前流程暂无审批记录" />
          </div>
        </div>
        <template #footer>
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchArchiveCreateOptions } from '../../api/modules/archiveManagement'
import { getProcessInstance, getProcessTasks, listProcesses } from '../../api/modules/workflow'
import type { ArchiveCreateOptions } from '../../types'

interface TransferDocumentView {
  documentTypeName: string
  businessCode: string
  documentOrganizationName: string
  extFieldEntries: Array<{ key: string; value: string }>
}

interface TransferRecordView {
  id: string
  processInstanceId: string
  transferorId: string
  transferorName: string
  assigneeId: string
  assigneeName: string
  transferMethod: string
  logisticsCompany: string
  trackingNumber: string
  status: string
  remark: string
  businessCodes: string[]
  documentCount: number
  createTime?: string
  updatedAt?: string
  displayCreateTime: string
  displayUpdatedAt: string
  documents: TransferDocumentView[]
}

interface ProcessTaskView {
  taskId: string
  taskName: string
  assignee?: string
  status?: string
  createTime?: string
  completeTime?: string
}

const router = useRouter()
const loading = ref(false)
const archiveOptions = ref<ArchiveCreateOptions | null>(null)
const currentUserId = ref('2')
const allRecords = ref<TransferRecordView[]>([])
const filteredRecords = ref<TransferRecordView[]>([])
const detailDialogVisible = ref(false)
const currentTransferDetail = ref<TransferRecordView | null>(null)
const processTasks = ref<ProcessTaskView[]>([])

const userOptions = ref([
  { id: '1', name: '张三' },
  { id: '2', name: '李四' },
  { id: '3', name: '王五' }
])

const searchForm = reactive({
  transferId: '',
  transferMethod: '',
  businessCode: '',
  transferorId: '',
  assigneeId: '',
  status: '',
  dateRange: [] as string[]
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 20
})

const pagedRecords = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize
  return filteredRecords.value.slice(start, start + pagination.pageSize)
})

const formatDateTime = (value?: string | null) => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const pad = (num: number) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

const resolveOptionName = (key: 'documentTypes' | 'documentOrganizations', code?: string) => {
  if (!code) return '-'
  const options = archiveOptions.value?.[key] ?? []
  return options.find(option => option.code === code)?.name ?? code
}

const resolveUserName = (userId?: string) => {
  if (!userId) return '-'
  return userOptions.value.find(user => user.id === String(userId))?.name ?? String(userId)
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

const buildTransferRecord = (process: any): TransferRecordView => {
  const parsed = parseVariables(process.variables) as Record<string, any>
  const form = parsed.transferForm ?? {}
  const documents = Array.isArray(parsed.transferDocuments) ? parsed.transferDocuments : []
  const transferorId = String(parsed.initiatorId ?? process.initiatorId ?? '')
  const assigneeId = String(parsed.assigneeId ?? form.assigneeId ?? '')

  return {
    id: process.businessKey,
    processInstanceId: process.processInstanceId,
    transferorId,
    transferorName: parsed.initiatorName ?? process.initiatorName ?? resolveUserName(transferorId),
    assigneeId,
    assigneeName: resolveUserName(assigneeId),
    transferMethod: form.transferMethod ?? 'DIRECT',
    logisticsCompany: form.logisticsCompany ?? '',
    trackingNumber: form.trackingNumber ?? '',
    status: process.status,
    remark: form.remark ?? '',
    businessCodes: documents.map((item: Record<string, unknown>) => String(item.businessCode ?? '')).filter(Boolean),
    documentCount: documents.length,
    createTime: process.startTime,
    updatedAt: process.lastUpdateDate || process.endTime || process.startTime,
    displayCreateTime: formatDateTime(process.startTime),
    displayUpdatedAt: formatDateTime(process.lastUpdateDate || process.endTime || process.startTime),
    documents: documents.map((document: Record<string, unknown>) => {
      const documentTypeCode = String(document.documentTypeCode ?? '')
      const documentOrganizationCode = String(document.documentOrganizationCode ?? '')
      return {
        documentTypeName: resolveOptionName('documentTypes', documentTypeCode),
        businessCode: String(document.businessCode ?? ''),
        documentOrganizationName: resolveOptionName('documentOrganizations', documentOrganizationCode),
        extFieldEntries: Object.entries((document.extFields as Record<string, string>) ?? {}).map(([key, value]) => ({
          key,
          value: String(value ?? '')
        }))
      }
    })
  }
}

const decorateProcessTasks = (tasks: ProcessTaskView[], processStatus?: string) =>
  [...tasks]
    .map(task => {
      let status = task.status || 'COMPLETED'
      if (status === 'COMPLETED') {
        if (processStatus === 'APPROVED') status = 'APPROVED'
        if (processStatus === 'REJECTED') status = 'REJECTED'
      }
      return { ...task, status }
    })
    .sort((a, b) => {
      const aTime = new Date(a.completeTime || a.createTime || 0).getTime()
      const bTime = new Date(b.completeTime || b.createTime || 0).getTime()
      return bTime - aTime
    })

const applyFilters = () => {
  const [beginDate, endDate] = searchForm.dateRange
  const beginTime = beginDate ? new Date(`${beginDate} 00:00:00`).getTime() : null
  const endTime = endDate ? new Date(`${endDate} 23:59:59`).getTime() : null

  filteredRecords.value = allRecords.value.filter(record => {
    const recordTime = new Date(record.updatedAt || record.createTime || 0).getTime()
    if (searchForm.transferId && !record.id.toLowerCase().includes(searchForm.transferId.toLowerCase())) return false
    if (searchForm.transferMethod && record.transferMethod !== searchForm.transferMethod) return false
    if (searchForm.businessCode && !record.businessCodes.some(code => code.toLowerCase().includes(searchForm.businessCode.toLowerCase()))) return false
    if (searchForm.transferorId && record.transferorId !== searchForm.transferorId) return false
    if (searchForm.assigneeId && record.assigneeId !== searchForm.assigneeId) return false
    if (searchForm.status && record.status !== searchForm.status) return false
    if (beginTime !== null && recordTime < beginTime) return false
    if (endTime !== null && recordTime > endTime) return false
    return true
  })

  filteredRecords.value.sort((a, b) => new Date(b.updatedAt || b.createTime || 0).getTime() - new Date(a.updatedAt || a.createTime || 0).getTime())
  pagination.currentPage = 1
}

const loadData = async () => {
  loading.value = true
  try {
    const [options, processes] = await Promise.all([
      fetchArchiveCreateOptions(),
      listProcesses('documentTransfer')
    ])
    archiveOptions.value = options
    allRecords.value = (processes as any[]).map(buildTransferRecord).sort((a, b) => {
      return new Date(b.updatedAt || b.createTime || 0).getTime() - new Date(a.updatedAt || a.createTime || 0).getTime()
    })
    applyFilters()
  } catch (error) {
    console.error('Failed to load transfer records:', error)
    ElMessage.error('加载移交记录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const goToCreate = () => {
  router.push('/archive-management/transfer')
}

const search = () => {
  applyFilters()
}

const reset = () => {
  searchForm.transferId = ''
  searchForm.transferMethod = ''
  searchForm.businessCode = ''
  searchForm.transferorId = ''
  searchForm.assigneeId = ''
  searchForm.status = ''
  searchForm.dateRange = []
  applyFilters()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
}

const handleCurrentChange = (current: number) => {
  pagination.currentPage = current
}

const viewDetail = async (processInstanceId: string) => {
  try {
    const [process, tasks] = await Promise.all([
      getProcessInstance(processInstanceId),
      getProcessTasks(processInstanceId)
    ])
    currentTransferDetail.value = buildTransferRecord(process)
    processTasks.value = decorateProcessTasks(tasks as ProcessTaskView[], currentTransferDetail.value.status)
    detailDialogVisible.value = true
  } catch (error) {
    console.error('Failed to load transfer detail:', error)
    ElMessage.error('加载移交详情失败，请稍后重试')
  }
}

const getStatusType = (status?: string) => {
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

const getStatusLabel = (status?: string) => {
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

const getTaskType = (status?: string) => {
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

const getTaskTone = (status?: string) => {
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

const getTaskLabel = (status?: string) => {
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.transfer-query-page {
  display: grid;
  gap: 16px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.section-head strong {
  display: block;
  font-size: 16px;
  color: #24324a;
}

.section-head span {
  color: #7a879a;
  font-size: 12px;
}

.section-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.search-form,
.transfer-list,
.transfer-detail,
.documents-section {
  display: grid;
  gap: 16px;
}

.timeline-list {
  display: grid;
  gap: 14px;
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

.timeline-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.timeline-meta {
  color: #7a879a;
  font-size: 12px;
}

.loading-container {
  padding: 20px 0;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.documents-section h3 {
  margin: 0;
  font-size: 16px;
  color: #24324a;
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
</style>
