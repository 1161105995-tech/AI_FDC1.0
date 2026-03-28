<template>
  <div class="transfer-page">
    <el-card shadow="never">
      <template #header>
        <div class="section-head">
          <div>
            <strong>文档移交管理</strong>
            <span>管理文档移交流程，支持基于待归档档案或直接创建移交记录</span>
          </div>
          <div class="section-actions">
            <el-button @click="goBack">返回列表</el-button>
          </div>
        </div>
      </template>

      <div class="mode-switch">
        <el-radio-group v-model="currentMode" @change="handleModeChange">
          <el-radio-button label="SCENARIO_A">基于待归档档案</el-radio-button>
          <el-radio-button label="SCENARIO_B">无待归档档案移交</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 场景B：无待归档档案移交 -->
      <div v-if="currentMode === 'SCENARIO_B'" class="scenario-b">
        <el-card shadow="never" class="sub-card">
          <template #header>
            <div class="sub-card__title">
              无待归档档案移交
              <el-button type="primary" size="small" @click="addTransferDocument" style="margin-left: 16px;">添加文档</el-button>
              <el-button type="success" size="small" @click="openBatchAddDialog" style="margin-left: 8px;">批量添加</el-button>
            </div>
          </template>
          <div class="direct-transfer">
            <el-table :data="transferDocuments" border class="transfer-table">
              <el-table-column type="index" label="序号" width="60" />
              <el-table-column prop="documentTypeCode" label="文档类型" width="180">
                <template #default="{ row }">
                  <CommonTreeSelect 
                    v-model="row.documentTypeCode" 
                    :data="documentTypeTree" 
                    :props="{ label: 'typeName', value: 'typeCode' }" 
                    placeholder="请选择文档类型" 
                    @change="handleDocumentTypeChange(row)"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="businessCode" label="业务编码" width="150">
                <template #default="{ row }">
                  <el-input v-model="row.businessCode" placeholder="请输入业务编码" />
                </template>
              </el-table-column>
              <el-table-column prop="documentOrganizationCode" label="文档组织" width="150">
                <template #default="{ row }">
                  <el-select v-model="row.documentOrganizationCode" placeholder="请选择文档组织">
                    <el-option v-for="item in organizationOptions" :key="item.code" :label="item.name" :value="item.code" />
                  </el-select>
                </template>
              </el-table-column>
              <template v-for="(field, key) in getExtFieldsColumns(transferDocuments[0])" :key="key">
                <el-table-column :prop="`extFields.${key}`" :label="field.label" min-width="120">
                  <template #default="{ row }">
                    <el-input v-model="row.extFields[key]" size="small" :placeholder="`请输入${field.label}`" />
                  </template>
                </el-table-column>
              </template>
              <el-table-column label="操作" width="100">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" @click="removeTransferDocument($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-if="transferDocuments.length === 0" description="请添加文档" />
          </div>
        </el-card>
      </div>

      <!-- 批量添加对话框 -->
      <el-dialog v-model="batchAddVisible" title="批量添加文档" width="800px">
        <div class="batch-add-dialog">
          <el-form label-width="120px">
            <el-form-item label="文档类型">
              <CommonTreeSelect 
                v-model="batchForm.documentTypeCode" 
                :data="documentTypeTree" 
                :props="{ label: 'typeName', value: 'typeCode' }" 
                placeholder="请选择文档类型" 
                @change="handleBatchDocumentTypeChange"
              />
            </el-form-item>
            <el-form-item label="批量输入">
              <el-input
                v-model="batchForm.batchInput"
                type="textarea"
                :rows="10"
                placeholder="请从Excel复制多行数据，每行包含业务编码和文档组织，用制表符分隔"
              />
              <div class="batch-hint">
                格式示例：业务编码\t文档组织编码<br>
                例如：BIZ001\tORG001
              </div>
            </el-form-item>
            <el-form-item v-if="batchForm.extFields && Object.keys(batchForm.extFields).length > 0" label="扩展字段">
              <el-form :inline="true" :model="batchForm.extFields">
                <el-form-item v-for="(value, key) in batchForm.extFields" :key="key" :label="key" label-width="80px">
                  <el-input v-model="batchForm.extFields[key]" placeholder="请输入" />
                </el-form-item>
              </el-form>
            </el-form-item>
          </el-form>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="batchAddVisible = false">取消</el-button>
            <el-button type="primary" @click="submitBatchAdd">确定添加</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 移交信息 -->
      <el-card shadow="never" class="sub-card">
        <template #header><div class="sub-card__title">移交信息</div></template>
        <el-form label-width="120px">
          <el-form-item label="移交人">
            <el-input :model-value="currentUser" disabled />
          </el-form-item>
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
      </el-card>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <el-button type="primary" size="large" @click="submitTransfer" :loading="submitting" :disabled="!canSubmit">
          提交移交申请
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import CommonTreeSelect from '../../components/CommonTreeSelect.vue'
import { fetchDocumentTypeTree } from '../../api/modules/documentType'
import { fetchArchiveCreateOptions, fetchEffectiveDocumentTypeExtFields } from '../../api/modules/archiveManagement'
import { startProcess } from '../../api/modules/workflow'
import type { ArchiveCreateOptions, DocumentTypeTreeNode, DocumentTypeExtField } from '../../types'

const router = useRouter()
const currentMode = ref('SCENARIO_B')
const loading = ref(false)
const submitting = ref(false)
const currentUserId = ref('1')
const currentUser = ref('当前用户')

// 场景A：待归档档案
const filters = reactive({
  documentTypeCode: '',
  documentOrganizationCode: ''
})

const documentTypeTree = ref<DocumentTypeTreeNode[]>([])
const organizationOptions = ref<any[]>([])
const archivedGroups = ref<any[]>([])
const selectedArchives = ref<any[]>([])

// 场景B：无待归档档案移交
const transferDocuments = ref<any[]>([])
const documentTypeOptions = ref<any[]>([])

// 批量添加
const batchAddVisible = ref(false)
const batchForm = reactive({
  documentTypeCode: '',
  batchInput: '',
  extFields: {}
})

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

// 计算属性
const canSubmit = computed(() => {
  if (!transferForm.assigneeId) return false
  if (transferDocuments.value.length === 0) return false
  if (transferForm.transferMethod === 'MAIL' && (!transferForm.logisticsCompany || !transferForm.trackingNumber)) return false
  return true
})

// 方法
const goBack = () => {
  router.push('/archive-management/transfer-query')
}

const handleModeChange = () => {
  if (currentMode.value === 'SCENARIO_A') {
    // 跳转到档案查询界面，并默认选择待我归档
    router.push('/archive-management/query?filter=my-archive')
  }
  selectedArchives.value = []
  transferDocuments.value = []
}

// 批量添加相关方法
const openBatchAddDialog = () => {
  // 重置批量添加表单
  batchForm.documentTypeCode = ''
  batchForm.batchInput = ''
  batchForm.extFields = {}
  batchAddVisible.value = true
}

const handleBatchDocumentTypeChange = async () => {
  if (!batchForm.documentTypeCode) {
    batchForm.extFields = {}
    return
  }
  // 从API获取扩展字段配置
  try {
    const extFields = await fetchEffectiveDocumentTypeExtFields(batchForm.documentTypeCode)
    // 将扩展字段转换为对象形式
    batchForm.extFields = extFields.reduce((acc, field) => {
      acc[field.fieldName] = ''
      return acc
    }, {} as Record<string, string>)
  } catch (error) {
    console.error('Failed to load ext fields:', error)
    batchForm.extFields = {}
  }
}

const submitBatchAdd = () => {
  if (!batchForm.documentTypeCode) {
    alert('请选择文档类型')
    return
  }
  if (!batchForm.batchInput) {
    alert('请输入批量数据')
    return
  }
  
  // 解析批量输入数据
  const lines = batchForm.batchInput.split('\n')
  const newDocuments = lines.map(line => {
    const [businessCode, documentOrganizationCode] = line.split('\t').map(item => item.trim())
    if (!businessCode) return null
    return {
      documentTypeCode: batchForm.documentTypeCode,
      businessCode,
      documentOrganizationCode: documentOrganizationCode || '',
      extFields: { ...batchForm.extFields }
    }
  }).filter(item => item !== null)
  
  if (newDocuments.length === 0) {
    alert('未解析到有效数据')
    return
  }
  
  // 添加到文档列表
  transferDocuments.value.push(...newDocuments)
  batchAddVisible.value = false
  alert(`成功添加 ${newDocuments.length} 份文档`)
}

const applyFilters = async () => {
  loading.value = true
  // 模拟数据
  setTimeout(() => {
    archivedGroups.value = [
      {
        key: 'type1-org1',
        title: '文档类型1 - 文档组织1',
        items: [
          { id: 1, documentName: '文档1', documentTypeName: '文档类型1', companyProjectName: '项目A', carrierTypeCode: 'HYBRID', businessCode: 'BUS001' },
          { id: 2, documentName: '文档2', documentTypeName: '文档类型1', companyProjectName: '项目A', carrierTypeCode: 'PAPER', businessCode: 'BUS002' }
        ]
      },
      {
        key: 'type2-org1',
        title: '文档类型2 - 文档组织1',
        items: [
          { id: 3, documentName: '文档3', documentTypeName: '文档类型2', companyProjectName: '项目B', carrierTypeCode: 'HYBRID', businessCode: 'BUS003' }
        ]
      }
    ]
    loading.value = false
  }, 1000)
}

const resetFilters = () => {
  filters.documentTypeCode = ''
  filters.documentOrganizationCode = ''
  archivedGroups.value = []
  selectedArchives.value = []
}

const handleSelectionChange = (selection: any[]) => {
  selectedArchives.value = selection
}

const addTransferDocument = () => {
  transferDocuments.value.push({
    documentTypeCode: '',
    businessCode: '',
    documentOrganizationCode: '',
    extFields: {}
  })
}

const removeTransferDocument = (index: number) => {
  transferDocuments.value.splice(index, 1)
}

const getExtFieldsColumns = (document: any) => {
  if (!document || !document.extFields) return {}
  return Object.entries(document.extFields).reduce((acc, [key, value]) => {
    acc[key] = { label: key }
    return acc
  }, {} as Record<string, { label: string }>)
}

const handleDocumentTypeChange = async (row: any) => {
  if (!row.documentTypeCode) {
    row.extFields = {}
    return
  }
  // 从API获取扩展字段配置
  try {
    const extFields = await fetchEffectiveDocumentTypeExtFields(row.documentTypeCode)
    // 将扩展字段转换为对象形式
    row.extFields = extFields.reduce((acc, field) => {
      acc[field.fieldName] = ''
      return acc
    }, {} as Record<string, string>)
  } catch (error) {
    console.error('Failed to load ext fields:', error)
    row.extFields = {}
  }
}

const handleTransferMethodChange = () => {
  if (transferForm.transferMethod !== 'MAIL') {
    transferForm.logisticsCompany = ''
    transferForm.trackingNumber = ''
  }
}

const submitTransfer = async () => {
  if (!canSubmit.value) return
  
  submitting.value = true
  try {
    // 构建工作流启动命令
    const command = {
      processDefinitionKey: 'documentTransfer',
      businessKey: `transfer_${Date.now()}`,
      businessType: 'TRANSFER',
      initiatorId: '1', // 实际应该从登录信息中获取
      initiatorName: currentUser.value,
      initiatorUserId: currentUserId.value,
      variables: {
        assigneeId: String(transferForm.assigneeId),
        assigneeName: String(transferForm.assigneeId),
        initiatorId: currentUserId.value,
        initiatorName: currentUser.value,
        transferForm: transferForm,
        transferDocuments: transferDocuments.value,
        transferMode: currentMode.value
      }
    }
    
    // 调用工作流API
    await startProcess(command)
    
    alert('移交申请已提交到电子流')
    submitting.value = false
    router.push('/archive-management/transfer-query')
  } catch (error) {
    console.error('提交移交申请失败:', error)
    alert('提交移交申请失败，请重试')
    submitting.value = false
  }
}

// 加载数据
const loadData = async () => {
  try {
    currentUser.value = '张三'
    const [tree, options] = await Promise.all([
      fetchDocumentTypeTree(),
      fetchArchiveCreateOptions()
    ])
    documentTypeTree.value = tree
    organizationOptions.value = options.documentOrganizations
    documentTypeOptions.value = tree.map(item => ({
      label: item.typeName,
      value: item.typeCode
    }))
    await applyFilters()
  } catch (error) {
    console.error('Failed to load data:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.transfer-page { display: grid; gap: 16px; }
.section-head { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; }
.section-head strong { display: block; font-size: 16px; color: #24324a; }
.section-head span { color: #7a879a; font-size: 12px; }
.section-actions { display: flex; gap: 8px; align-items: center; }
.mode-switch { margin-bottom: 16px; }
.sub-card { border: 1px solid #edf2f7; margin-bottom: 16px; }
.sub-card__title { font-weight: 600; color: #24324a; }
.filter-form { margin-bottom: 16px; }
.loading-container { padding: 20px 0; }
.archive-groups { display: grid; gap: 16px; }
.archive-group { border: 1px solid #e3edf8; border-radius: 8px; overflow: hidden; }
.group-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: #f8fafc; border-bottom: 1px solid #e3edf8; }
.group-header strong { color: #24324a; }
.group-header span { color: #7a879a; font-size: 12px; }
.group-table, .transfer-table { margin: 0; }
.direct-transfer { display: grid; gap: 16px; }
.ext-field { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.ext-field-label { font-size: 12px; color: #7a879a; min-width: 80px; }
.submit-section { display: flex; justify-content: center; margin-top: 24px; }
</style>
