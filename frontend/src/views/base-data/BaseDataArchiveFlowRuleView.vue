<template>
  <div class="archive-flow-page">
    <el-card shadow="never">
      <div class="toolbar">
        <el-form :inline="true" :model="query" class="query-form">
          <el-form-item label="关键字">
            <el-input v-model.trim="query.keyword" placeholder="公司/项目、文档类型或文档组织" clearable />
          </el-form-item>
          <el-form-item label="公司/项目">
            <el-select v-model="query.companyProjectCode" placeholder="全部" clearable style="width: 220px">
              <el-option v-for="item in companyProjectOptions" :key="item.code" :label="`${item.code} - ${item.name}`" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="文档组织">
            <el-select v-model="query.documentOrganizationCode" placeholder="全部" clearable style="width: 220px" filterable>
              <el-option v-for="item in documentOrganizationOptions" :key="item.code" :label="`${item.code} - ${item.name}`" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="启用标识">
            <el-select v-model="query.enabledFlag" placeholder="全部" clearable style="width: 120px">
              <el-option label="启用" value="Y" />
              <el-option label="停用" value="N" />
            </el-select>
          </el-form-item>
        </el-form>
        <div class="toolbar-actions">
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button type="success" @click="startCreate">新建归档流向规则</el-button>
        </div>
      </div>

      <el-alert
        v-if="editor.visible"
        :title="editor.mode === 'create' ? '在当前页面新建归档流向规则' : editor.mode === 'edit' ? `编辑：${form.companyProjectCode}` : `查看：${form.companyProjectCode}`"
        type="info"
        :closable="false"
        class="editor-tip"
      />

      <div v-if="editor.visible" class="editor-panel">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="160px" class="editor-form">
          <el-row :gutter="16">
            <el-col :md="8" :xs="24">
              <el-form-item label="公司/项目" prop="companyProjectCode" required>
                <el-select v-model="form.companyProjectCode" :disabled="editor.mode !== 'create' || isReadonly" placeholder="请选择公司/项目" filterable style="width: 100%">
                  <el-option v-for="item in companyProjectOptions" :key="item.code" :label="`${item.code} - ${item.name}`" :value="item.code" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="文档类型" prop="documentTypeCode" required>
                <TreeCodeSelector v-model="form.documentTypeCode" :data="documentTypeTree" placeholder="请选择文档类型" label-key="typeName" value-key="typeCode" :disabled="isReadonly" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="归档地" prop="archiveDestination">
                <el-select v-model="form.archiveDestination" :disabled="isReadonly" placeholder="请选择城市" clearable filterable style="width: 100%">
                  <el-option v-for="item in cityOptions" :key="item.code" :label="item.name" :value="item.code" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="文档组织" prop="documentOrganizationCode" required>
                <el-select v-model="form.documentOrganizationCode" :disabled="isReadonly" placeholder="请选择文档组织" filterable style="width: 100%">
                  <el-option v-for="item in documentOrganizationOptions" :key="item.code" :label="`${item.code} - ${item.name}`" :value="item.code" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="保存期限（年）" prop="retentionPeriodYears" required>
                <el-input v-model="retentionPeriodInput" :disabled="isReadonly" placeholder="仅支持阿拉伯数字" maxlength="4" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="密级" prop="securityLevelCode" required>
                <el-select v-model="form.securityLevelCode" :disabled="isReadonly" placeholder="请选择密级" style="width: 100%">
                  <el-option v-for="item in securityLevels" :key="item.securityLevelCode" :label="item.securityLevelName" :value="item.securityLevelCode" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="是否对外展示" prop="externalDisplayFlag" required>
                <el-segmented v-model="form.externalDisplayFlag" :options="yesNoOptions" :disabled="isReadonly" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="启用标识" prop="enabledFlag" required>
                <el-segmented v-model="form.enabledFlag" :options="enabledOptions" :disabled="isReadonly" />
              </el-form-item>
            </el-col>
            <el-col :md="24" :xs="24">
              <el-form-item label="自定义规则" prop="customRule">
                <el-input v-model.trim="form.customRule" :disabled="isReadonly" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="可选，填写默认规则说明" />
              </el-form-item>
            </el-col>
            <el-col v-if="editor.mode !== 'create'" :md="8" :xs="24">
              <el-form-item label="最后更新时间">
                <el-input :model-value="editor.lastUpdateDate || '-'" disabled />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div class="editor-actions">
          <el-button @click="cancelEditor">取消</el-button>
          <el-button v-if="editor.mode === 'view'" type="primary" plain @click="startEdit(form.companyProjectCode)">进入编辑</el-button>
          <el-button v-else type="primary" @click="submit">保存</el-button>
        </div>
      </div>

      <el-table :data="displayedItems" border empty-text="暂无归档流向规则">
        <el-table-column prop="companyProjectCode" label="公司/项目编码" min-width="180" />
        <el-table-column prop="companyProjectName" label="公司/项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="documentTypeName" label="文档类型" min-width="180" show-overflow-tooltip />
        <el-table-column prop="archiveDestinationName" label="归档地" min-width="140" show-overflow-tooltip />
        <el-table-column prop="documentOrganizationName" label="文档组织" min-width="180" show-overflow-tooltip />
        <el-table-column prop="retentionPeriodYears" label="保存期限（年）" width="130" align="center" />
        <el-table-column prop="securityLevelName" label="密级" width="130" align="center" />
        <el-table-column label="对外展示" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.externalDisplayFlag === 'Y' ? 'success' : 'info'">{{ row.externalDisplayFlag === 'Y' ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="启用" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.enabledFlag === 'Y' ? 'success' : 'info'">{{ row.enabledFlag === 'Y' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdateDate" label="最后更新时间" min-width="180" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="startView(row.companyProjectCode)">查看</el-button>
            <el-button link type="primary" @click="startEdit(row.companyProjectCode)">编辑</el-button>
            <el-popconfirm title="确认软删除该规则吗？" @confirm="removeItem(row.companyProjectCode)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-card shadow="never" class="audit-panel">
        <template #header>
          <div class="panel-header simple">
            <div>
              <strong>审计记录</strong>
              <span>通过公共审计模块记录新增、修改、删除</span>
            </div>
          </div>
        </template>
        <el-table :data="auditRecords" size="small">
          <el-table-column prop="operationType" label="操作类型" width="120" />
          <el-table-column prop="businessKey" label="业务主键" width="200" />
          <el-table-column prop="operationSummary" label="摘要" min-width="180" />
          <el-table-column prop="operatorName" label="操作人" width="120" />
          <el-table-column label="操作时间" width="180">
            <template #default="{ row }">{{ formatTime(row.operationTime) }}</template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import TreeCodeSelector from '../../components/TreeCodeSelector.vue'
import {
  createArchiveFlowRule,
  deleteArchiveFlowRule,
  fetchArchiveFlowCityOptions,
  fetchArchiveFlowCompanyProjectOptions,
  fetchArchiveFlowDocumentOrganizationOptions,
  fetchArchiveFlowRuleDetail,
  fetchArchiveFlowRules,
  fetchArchiveFlowSecurityLevels,
  fetchModuleAudits,
  updateArchiveFlowRule,
  type ArchiveFlowRuleCreateCommand
} from '../../api/modules/archiveFlow'
import { fetchDocumentTypeTree } from '../../api/modules/documentType'
import type {
  ArchiveFlowRuleDetail,
  ArchiveFlowRuleOption,
  ArchiveFlowRuleSummary,
  AuditRecord,
  DocumentTypeTreeNode,
  SecurityLevelOption
} from '../../types'

const formRef = ref<FormInstance>()
const items = ref<ArchiveFlowRuleSummary[]>([])
const auditRecords = ref<AuditRecord[]>([])
const companyProjectOptions = ref<ArchiveFlowRuleOption[]>([])
const documentOrganizationOptions = ref<ArchiveFlowRuleOption[]>([])
const cityOptions = ref<ArchiveFlowRuleOption[]>([])
const securityLevels = ref<SecurityLevelOption[]>([])
const documentTypeTree = ref<DocumentTypeTreeNode[]>([])

const enabledOptions = [
  { label: '启用', value: 'Y' },
  { label: '停用', value: 'N' }
]
const yesNoOptions = [
  { label: '是', value: 'Y' },
  { label: '否', value: 'N' }
]

const query = reactive({
  keyword: '',
  companyProjectCode: '',
  documentTypeCode: '',
  documentOrganizationCode: '',
  enabledFlag: ''
})

const editor = reactive({
  visible: false,
  mode: 'create' as 'create' | 'edit' | 'view',
  lastUpdateDate: ''
})

const form = reactive<ArchiveFlowRuleCreateCommand>({
  companyProjectCode: '',
  documentTypeCode: '',
  customRule: '',
  archiveDestination: '',
  documentOrganizationCode: '',
  retentionPeriodYears: 0,
  securityLevelCode: '',
  externalDisplayFlag: 'N',
  enabledFlag: 'Y'
})

const retentionPeriodInput = ref('0')

watch(retentionPeriodInput, (value) => {
  const clean = (value || '').replace(/\D/g, '')
  if (clean !== value) {
    retentionPeriodInput.value = clean
    return
  }
  form.retentionPeriodYears = clean ? Number(clean) : 0
})

const rules: FormRules<ArchiveFlowRuleCreateCommand> = {
  companyProjectCode: [{ required: true, message: '请选择公司/项目', trigger: 'change' }],
  documentTypeCode: [{ required: true, message: '请选择文档类型', trigger: 'change' }],
  customRule: [{ max: 500, message: '最大长度500', trigger: 'blur' }],
  archiveDestination: [{ max: 64, message: '最大长度64', trigger: 'change' }],
  documentOrganizationCode: [{ required: true, message: '请选择文档组织', trigger: 'change' }],
  retentionPeriodYears: [{
    validator: (_rule, value, callback) => {
      if (!Number.isInteger(value) || value < 0) {
        callback(new Error('请输入大于等于0的阿拉伯数字'))
        return
      }
      callback()
    },
    trigger: 'blur'
  }],
  securityLevelCode: [{ required: true, message: '请选择密级', trigger: 'change' }],
  externalDisplayFlag: [{ required: true, message: '请选择是否对外展示', trigger: 'change' }],
  enabledFlag: [{ required: true, message: '请选择启用标识', trigger: 'change' }]
}

const isReadonly = computed(() => editor.mode === 'view')

const displayedItems = computed(() => {
  if (editor.visible && editor.mode === 'edit' && form.companyProjectCode) {
    return items.value.filter(item => item.companyProjectCode !== form.companyProjectCode)
  }
  return items.value
})

const resetFormState = () => {
  form.companyProjectCode = ''
  form.documentTypeCode = ''
  form.customRule = ''
  form.archiveDestination = ''
  form.documentOrganizationCode = ''
  form.retentionPeriodYears = 0
  form.securityLevelCode = securityLevels.value[0]?.securityLevelCode || ''
  form.externalDisplayFlag = 'N'
  form.enabledFlag = 'Y'
  retentionPeriodInput.value = '0'
  editor.lastUpdateDate = ''
}

const fillForm = (detail: ArchiveFlowRuleDetail) => {
  form.companyProjectCode = detail.companyProjectCode
  form.documentTypeCode = detail.documentTypeCode
  form.customRule = detail.customRule || ''
  form.archiveDestination = detail.archiveDestination || ''
  form.documentOrganizationCode = detail.documentOrganizationCode
  form.retentionPeriodYears = detail.retentionPeriodYears
  form.securityLevelCode = detail.securityLevelCode
  form.externalDisplayFlag = detail.externalDisplayFlag
  form.enabledFlag = detail.enabledFlag
  retentionPeriodInput.value = String(detail.retentionPeriodYears)
  editor.lastUpdateDate = detail.lastUpdateDate
}

const loadList = async () => {
  items.value = await fetchArchiveFlowRules({
    keyword: query.keyword?.trim() || undefined,
    companyProjectCode: query.companyProjectCode || undefined,
    documentTypeCode: query.documentTypeCode || undefined,
    documentOrganizationCode: query.documentOrganizationCode || undefined,
    enabledFlag: query.enabledFlag || undefined
  })
}

const loadMeta = async () => {
  const [companyProjects, organizations, cities, levels, typeTree] = await Promise.all([
    fetchArchiveFlowCompanyProjectOptions(),
    fetchArchiveFlowDocumentOrganizationOptions(),
    fetchArchiveFlowCityOptions(),
    fetchArchiveFlowSecurityLevels(),
    fetchDocumentTypeTree()
  ])
  companyProjectOptions.value = companyProjects
  documentOrganizationOptions.value = organizations
  cityOptions.value = cities
  securityLevels.value = levels
  documentTypeTree.value = typeTree
  resetFormState()
}

const loadAudits = async () => {
  auditRecords.value = await fetchModuleAudits('ARCHIVE_FLOW_RULE')
}

const resetQuery = async () => {
  query.keyword = ''
  query.companyProjectCode = ''
  query.documentTypeCode = ''
  query.documentOrganizationCode = ''
  query.enabledFlag = ''
  await loadList()
}

const startCreate = () => {
  editor.visible = true
  editor.mode = 'create'
  resetFormState()
}

const openDetailInEditor = async (companyProjectCode: string, mode: 'edit' | 'view') => {
  const detail = await fetchArchiveFlowRuleDetail(companyProjectCode)
  editor.visible = true
  editor.mode = mode
  fillForm(detail)
}

const startEdit = async (companyProjectCode: string) => {
  await openDetailInEditor(companyProjectCode, 'edit')
}

const startView = async (companyProjectCode: string) => {
  await openDetailInEditor(companyProjectCode, 'view')
}

const cancelEditor = () => {
  editor.visible = false
  resetFormState()
  formRef.value?.clearValidate()
}

const submit = async () => {
  try {
    const valid = await formRef.value?.validate().catch(() => false)
    if (valid === false) return

    if (editor.mode === 'create') {
      await createArchiveFlowRule({
        companyProjectCode: form.companyProjectCode,
        documentTypeCode: form.documentTypeCode,
        customRule: form.customRule?.trim() || undefined,
        archiveDestination: form.archiveDestination || undefined,
        documentOrganizationCode: form.documentOrganizationCode,
        retentionPeriodYears: form.retentionPeriodYears,
        securityLevelCode: form.securityLevelCode,
        externalDisplayFlag: form.externalDisplayFlag,
        enabledFlag: form.enabledFlag
      })
      ElMessage.success('归档流向规则创建成功')
    } else {
      await updateArchiveFlowRule(form.companyProjectCode, {
        documentTypeCode: form.documentTypeCode,
        customRule: form.customRule?.trim() || undefined,
        archiveDestination: form.archiveDestination || undefined,
        documentOrganizationCode: form.documentOrganizationCode,
        retentionPeriodYears: form.retentionPeriodYears,
        securityLevelCode: form.securityLevelCode,
        externalDisplayFlag: form.externalDisplayFlag,
        enabledFlag: form.enabledFlag
      })
      ElMessage.success('归档流向规则更新成功')
    }

    cancelEditor()
    await Promise.all([loadList(), loadAudits()])
  } catch (error: any) {
    ElMessage.error(error?.message || '保存失败')
  }
}

const removeItem = async (companyProjectCode: string) => {
  try {
    await deleteArchiveFlowRule(companyProjectCode)
    ElMessage.success('归档流向规则已软删除')
    if (form.companyProjectCode === companyProjectCode) {
      cancelEditor()
    }
    await Promise.all([loadList(), loadAudits()])
  } catch (error: any) {
    ElMessage.error(error?.message || '删除失败')
  }
}

const formatTime = (value?: string) => value ? value.replace('T', ' ').slice(0, 19) : '-'

onMounted(async () => {
  try {
    await loadMeta()
  } catch (error: any) {
    ElMessage.error(error?.message || '基础字典加载失败')
  }

  try {
    await loadList()
  } catch (error: any) {
    ElMessage.error(error?.message || '列表加载失败')
  }

  try {
    await loadAudits()
  } catch (error: any) {
    ElMessage.error(error?.message || '审计记录加载失败')
  }
})
</script>

<style scoped>
.archive-flow-page { display: grid; gap: 16px; }
.toolbar { display: flex; justify-content: space-between; gap: 16px; margin-bottom: 16px; flex-wrap: wrap; }
.query-form { flex: 1; }
.toolbar-actions { display: flex; gap: 12px; align-items: flex-start; }
.editor-tip { margin-bottom: 16px; }
.editor-panel {
  margin-bottom: 16px;
  padding: 16px;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  background: var(--el-fill-color-light);
}
.editor-form { margin-bottom: 12px; }
.editor-actions { display: flex; justify-content: flex-end; gap: 12px; }
.audit-panel { margin-top: 16px; }
.panel-header.simple { display: flex; align-items: flex-start; }
.panel-header strong { display: block; color: #24324a; font-size: 16px; }
.panel-header span { color: #7a879a; font-size: 12px; }
</style>
