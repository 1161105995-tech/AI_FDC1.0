<template>
  <div class="query-page">
    <el-card shadow="never" class="query-card">
      <template #header>
        <div class="section-head">
          <div>
            <strong>档案查询</strong>
            <span>支持固定字段、扩展字段和全文检索</span>
          </div>
          <div class="section-actions">
            <el-button @click="goToAiSearch" type="info">AI+档案</el-button>
          </div>
        </div>
      </template>

      <div class="filter-section">
        <div class="section-actions">
          <el-button @click="filterForMyArchive" type="primary" plain>待我归档</el-button>
        </div>

        <div class="form-grid form-grid--4">
          <el-form-item label="关键字"><el-input v-model="query.keyword" clearable /></el-form-item>
          <el-form-item label="文档类型">
            <el-select v-model="query.documentTypeCode" clearable filterable @change="handleQueryTypeChange">
              <el-option v-for="item in options.documentTypes" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="公司/项目">
            <el-select v-model="query.companyProjectCode" clearable filterable>
              <el-option v-for="item in options.companyProjects" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="档案类型">
            <el-select v-model="query.archiveTypeCode" clearable>
              <el-option v-for="item in options.archiveTypes" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="载体类型">
            <el-select v-model="query.carrierTypeCode" clearable>
              <el-option v-for="item in options.carrierTypes" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="密级">
            <el-select v-model="query.securityLevelCode" clearable filterable>
              <el-option v-for="item in options.securityLevels" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="文档名称"><el-input v-model="query.documentName" clearable /></el-form-item>
          <el-form-item label="业务编码"><el-input v-model="query.businessCode" clearable /></el-form-item>
          <el-form-item label="责任人"><el-input v-model="query.dutyPerson" clearable /></el-form-item>
          <el-form-item label="归档地">
            <el-select v-model="query.archiveDestination" clearable filterable>
              <el-option v-for="item in options.archiveDestinations" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="系统来源"><el-input v-model="query.sourceSystem" clearable /></el-form-item>
          <el-form-item label="文档组织">
            <el-select v-model="query.documentOrganizationCode" clearable filterable placeholder="请选择或输入文档组织">
              <el-option v-for="item in options.documentOrganizations" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
        </div>

        <div v-if="queryFields.length" class="query-extra">
          <div class="query-extra__title">扩展字段</div>
          <div class="form-grid form-grid--4">
            <el-form-item v-for="field in queryFields" :key="field.fieldCode" :label="field.fieldName">
              <el-input v-model="queryExtFilters[field.fieldCode]" clearable />
            </el-form-item>
          </div>
        </div>

        <div class="action-buttons">
          <el-button @click="resetFilters">重置</el-button>
          <el-button type="primary" @click="runQuery">查询</el-button>
        </div>
      </div>

      <div class="table-section">
        <div class="table-header">
          <div class="table-actions">
            <el-dropdown @command="handleBatchAction">
              <el-button type="primary" plain>
                批量操作
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="transfer">批量移交</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button @click="showColumnSetting">自定义字段</el-button>
          </div>
        </div>

        <el-table :data="queryResult.records" border @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column v-for="column in visibleColumns" :key="column.prop" :prop="column.prop" :label="column.label" :width="column.width" :min-width="column.minWidth" show-overflow-tooltip>
            <template v-if="column.prop === 'archiveFilingCode' || column.prop === 'documentName'" #default="{ row }">
              <el-link type="primary" @click="viewArchiveDetail(row.archiveId)">{{ row[column.prop] }}</el-link>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container" v-if="queryResult.total > 0">
          <div class="pagination-info">
            共 {{ queryResult.total }} 条记录，每页显示
            <el-select v-model="query.pageSize" size="small" @change="runQuery" style="width: 100px; margin: 0 8px;">
              <el-option v-for="size in pageSizeOptions" :key="size" :label="size" :value="size" />
            </el-select>
            条
          </div>
          <el-pagination
            v-model:current-page="query.page"
            v-model:page-size="query.pageSize"
            :page-sizes="pageSizeOptions"
            layout="total, sizes, prev, pager, next, jumper"
            :total="queryResult.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 移交对话框 -->
    <el-dialog v-model="transferDialogVisible" title="批量移交" width="600px">
      <div class="transfer-dialog">
        <p>已选择 {{ selectedRecords.length }} 份档案</p>
        <el-form label-width="120px">
          <el-form-item label="签收人" required>
            <el-select v-model="transferForm.assigneeId" placeholder="请选择签收人">
              <el-option v-for="user in userOptions" :key="user.id" :label="user.name" :value="user.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="移交方式" required>
            <el-radio-group v-model="transferForm.transferMethod" @change="handleTransferMethodChange">
              <el-radio-button label="DIRECT">直接移交</el-radio-button>
              <el-radio-button label="MAIL">邮寄</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="transferForm.transferMethod === 'MAIL'" label="物流公司">
            <el-select v-model="transferForm.logisticsCompany" placeholder="请选择物流公司">
              <el-option v-for="company in logisticsCompanies" :key="company.value" :label="company.label" :value="company.value" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="transferForm.transferMethod === 'MAIL'" label="邮寄单号">
            <el-input v-model="transferForm.trackingNumber" placeholder="请输入邮寄单号" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="transferForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="transferDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTransfer" :loading="submitting">提交移交</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 字段设置对话框 -->
    <el-dialog v-model="columnSettingVisible" title="自定义字段" width="500px">
      <div class="column-setting">
        <el-tree
          v-model="selectedColumnKeys"
          :data="allColumns"
          show-checkbox
          node-key="prop"
          default-expand-all
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="columnSettingVisible = false">取消</el-button>
          <el-button type="primary" @click="saveColumnSetting">保存设置</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchArchiveCreateOptions, fetchEffectiveDocumentTypeExtFields, queryArchives, transferArchives, type ArchiveQueryCommand } from '../../api/modules/archiveManagement'
import type { ArchiveCreateOptions, ArchiveQueryResult, DocumentTypeExtField } from '../../types'

const route = useRoute()
const router = useRouter()
const options = reactive<ArchiveCreateOptions>({
  companyProjects: [],
  documentTypes: [],
  archiveDestinations: [],
  documentOrganizations: [],
  securityLevels: [],
  carrierTypes: [],
  attachmentTypes: [],
  archiveTypes: [],
  aiModels: []
})

const query = reactive<ArchiveQueryCommand>({
  keyword: '',
  documentTypeCode: '',
  companyProjectCode: '',
  archiveTypeCode: '',
  carrierTypeCode: '',
  securityLevelCode: '',
  documentName: '',
  businessCode: '',
  dutyPerson: '',
  archiveDestination: '',
  sourceSystem: '',
  documentOrganizationCode: '',
  page: 1,
  pageSize: 20
})

const queryFields = ref<DocumentTypeExtField[]>([])
const queryExtFilters = reactive<Record<string, string>>({})
const queryResult = reactive<ArchiveQueryResult>({ records: [], queryFields: [], total: 0, page: 1, pageSize: 20 })
const pageSizeOptions = [20, 50, 100, 500, 1000]

// 批量操作相关
const selectedRecords = ref<any[]>([])
const transferDialogVisible = ref(false)
const submitting = ref(false)

// 移交表单
const transferForm = reactive({
  assigneeId: '',
  transferMethod: 'DIRECT',
  logisticsCompany: '',
  trackingNumber: '',
  remark: ''
})

// 选项数据
const userOptions = ref([
  { id: 1, name: '张三' },
  { id: 2, name: '李四' },
  { id: 3, name: '王五' }
])

const logisticsCompanies = ref([
  { value: 'SF', label: '顺丰速运' },
  { value: 'STO', label: '申通快递' },
  { value: 'YTO', label: '圆通速递' },
  { value: 'ZTO', label: '中通快递' },
  { value: 'YD', label: '韵达快递' }
])

// 自定义字段相关
const columnSettingVisible = ref(false)
const allColumns = ref([
  { label: '归档编码', prop: 'archiveFilingCode', width: '180' },
  { label: '文档名称', prop: 'documentName', minWidth: '220' },
  { label: '文档类型', prop: 'documentTypeName', width: '160' },
  { label: '公司/项目', prop: 'companyProjectName', width: '180' },
  { label: '载体类型', prop: 'carrierTypeCode', width: '120' },
  { label: '责任人', prop: 'dutyPerson', width: '120' },
  { label: '附件数', prop: 'attachmentCount', width: '90' },
  { label: 'AI摘要', prop: 'aiArchiveSummary', minWidth: '280' },
  { label: '业务编码', prop: 'businessCode', width: '150' },
  { label: '归档地', prop: 'archiveDestination', width: '150' },
  { label: '系统来源', prop: 'sourceSystem', width: '150' },
  { label: '文档组织', prop: 'documentOrganizationCode', width: '150' }
])

const defaultVisibleColumns = [
  'archiveFilingCode',
  'documentName',
  'documentTypeName',
  'companyProjectName',
  'carrierTypeCode',
  'dutyPerson',
  'attachmentCount',
  'aiArchiveSummary'
]

const selectedColumnKeys = ref([...defaultVisibleColumns])

const visibleColumns = computed(() => {
  return allColumns.value.filter(column => selectedColumnKeys.value.includes(column.prop))
})

const loadOptions = async () => {
  const result = await fetchArchiveCreateOptions()
  Object.assign(options, result)
  console.log('Document organizations:', options.documentOrganizations)
}

const handleQueryTypeChange = async (typeCode?: string) => {
  queryFields.value = typeCode ? await fetchEffectiveDocumentTypeExtFields(typeCode) : []
  queryFields.value = queryFields.value.filter(item => item.queryEnabledFlag === 'Y')
  Object.keys(queryExtFilters).forEach(key => delete queryExtFilters[key])
}

const runQuery = async () => {
  const result = await queryArchives({ ...query, extFilters: { ...queryExtFilters } })
  queryResult.records = result.records
  queryResult.queryFields = result.queryFields
  queryResult.total = result.total
  queryResult.page = result.page
  queryResult.pageSize = result.pageSize
  queryFields.value = result.queryFields
}

const resetFilters = async () => {
  Object.assign(query, {
    keyword: '',
    documentTypeCode: '',
    companyProjectCode: '',
    archiveTypeCode: '',
    carrierTypeCode: '',
    securityLevelCode: '',
    documentName: '',
    businessCode: '',
    dutyPerson: '',
    archiveDestination: '',
    sourceSystem: '',
    documentOrganizationCode: '',
    page: 1,
    pageSize: 20
  })
  Object.keys(queryExtFilters).forEach(key => delete queryExtFilters[key])
  queryFields.value = []
  queryResult.records = []
  queryResult.queryFields = []
  queryResult.total = 0
  queryResult.page = 1
  queryResult.pageSize = 20
  selectedRecords.value = []
}

const handleSizeChange = (size: number) => {
  query.pageSize = size
  query.page = 1
  runQuery()
}

const handleCurrentChange = (current: number) => {
  query.page = current
  runQuery()
}

const viewArchiveDetail = (archiveId: number) => {
  router.push(`/archive-management/detail/${archiveId}`)
}

// 快捷筛选
const filterForMyArchive = () => {
  // 模拟当前用户
  const currentUser = '当前用户'
  query.dutyPerson = currentUser
  // 假设状态字段为archiveStatus
  // query.archiveStatus = 'PENDING'
  runQuery()
}

// 批量操作
const handleSelectionChange = (selection: any[]) => {
  selectedRecords.value = selection
}

const handleBatchAction = (command: string) => {
  if (selectedRecords.value.length === 0) {
    ElMessage.warning('请先选择要操作的档案')
    return
  }
  
  if (command === 'transfer') {
    transferDialogVisible.value = true
  }
}

// 移交功能
const handleTransferMethodChange = () => {
  if (transferForm.transferMethod !== 'MAIL') {
    transferForm.logisticsCompany = ''
    transferForm.trackingNumber = ''
  }
}

const submitTransfer = async () => {
  if (!transferForm.assigneeId) {
    ElMessage.warning('请选择签收人')
    return
  }
  
  if (transferForm.transferMethod === 'MAIL' && (!transferForm.logisticsCompany || !transferForm.trackingNumber)) {
    ElMessage.warning('请填写物流公司和邮寄单号')
    return
  }
  
  const assignee = userOptions.value.find(user => String(user.id) === String(transferForm.assigneeId))
  submitting.value = true
  try {
    const response = await transferArchives({
      archiveIds: selectedRecords.value.map(record => Number(record.archiveId)).filter(id => Number.isFinite(id)),
      assigneeId: String(transferForm.assigneeId),
      assigneeName: assignee?.name,
      transferMethod: transferForm.transferMethod as 'DIRECT' | 'MAIL',
      logisticsCompany: transferForm.transferMethod === 'MAIL' ? transferForm.logisticsCompany : '',
      trackingNumber: transferForm.transferMethod === 'MAIL' ? transferForm.trackingNumber : '',
      remark: transferForm.remark,
      initiatorId: '1',
      initiatorName: '张三'
    })
    ElMessage.success(`移交申请已提交，已发起 ${response.archiveCount} 份档案的电子流`)
    transferDialogVisible.value = false
    transferForm.assigneeId = ''
    transferForm.transferMethod = 'DIRECT'
    transferForm.logisticsCompany = ''
    transferForm.trackingNumber = ''
    transferForm.remark = ''
    selectedRecords.value = []
    await runQuery()
  } finally {
    submitting.value = false
  }
  return
  submitting.value = true
  // 模拟提交
  setTimeout(() => {
    ElMessage.success('移交申请已提交')
    transferDialogVisible.value = false
    submitting.value = false
    // 重置表单
    transferForm.assigneeId = ''
    transferForm.transferMethod = 'DIRECT'
    transferForm.logisticsCompany = ''
    transferForm.trackingNumber = ''
    transferForm.remark = ''
    // 刷新列表
    runQuery()
  }, 1000)
}

// 自定义字段
const showColumnSetting = () => {
  columnSettingVisible.value = true
}

const saveColumnSetting = () => {
  if (selectedColumnKeys.value.length === 0) {
    ElMessage.warning('请至少选择一个字段')
    return
  }
  columnSettingVisible.value = false
  ElMessage.success('字段设置已保存')
}

// AI+档案跳转
const goToAiSearch = () => {
  router.push('/archive-management/ai-search')
}

onMounted(async () => {
  const routeKeyword = typeof route.query.q === 'string' ? route.query.q.trim() : ''
  if (routeKeyword) {
    query.keyword = routeKeyword
  }
  await loadOptions()
  await runQuery()
})
</script>

<style scoped>
.query-page { display: grid; gap: 16px; }
.query-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  overflow: hidden;
}
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f2f5;
}
.section-head strong {
  display: block;
  font-size: 18px;
  color: #24324a;
  font-weight: 600;
}
.section-head span {
  color: #7a879a;
  font-size: 13px;
  margin-top: 4px;
  display: block;
}
.section-actions {
  display: flex;
  gap: 8px;
}
.filter-section {
  padding: 20px 0;
}
.quick-filters {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}
.form-grid {
  display: grid;
  gap: 16px 20px;
}
.form-grid--4 {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}
.query-extra {
  margin: 20px 0;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}
.query-extra__title {
  margin-bottom: 16px;
  font-size: 14px;
  font-weight: 600;
  color: #4b587c;
}
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f0f2f5;
}
.action-buttons .el-button {
  min-width: 120px;
  padding: 10px 20px;
  font-size: 14px;
}
.table-section {
  margin-top: 20px;
}
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f2f5;
}
.table-actions {
  display: flex;
  gap: 12px;
}
.table-actions .el-button {
  font-size: 13px;
  padding: 8px 16px;
}
.el-table {
  border-radius: 6px;
  overflow: hidden;
}
.el-table th {
  background-color: #f7f8fa !important;
  font-weight: 600;
  color: #24324a;
  font-size: 13px;
}
.el-table td {
  font-size: 13px;
  color: #303133;
}
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}
.pagination-info {
  font-size: 13px;
  color: #606266;
}
.transfer-dialog p {
  margin-bottom: 16px;
  color: #606266;
  font-size: 13px;
}
.column-setting .el-tree {
  max-height: 400px;
  overflow-y: auto;
}
@media (max-width: 1280px) {
  .form-grid--4 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
@media (max-width: 1024px) {
  .form-grid--4 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .table-actions {
    width: 100%;
    justify-content: space-between;
  }
}
@media (max-width: 768px) {
  .form-grid--4 {
    grid-template-columns: 1fr;
  }
  .section-head {
    flex-direction: column;
  }
  .pagination-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .action-buttons {
    flex-direction: column;
  }
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
