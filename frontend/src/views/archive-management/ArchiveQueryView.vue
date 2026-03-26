<template>
  <div class="query-page">
    <el-card shadow="never">
      <template #header>
        <div class="section-head">
          <div>
            <strong>档案查询</strong>
            <span>支持固定字段、扩展字段、全文检索和知识问答。</span>
          </div>
          <div class="section-actions">
            <el-button @click="resetFilters">重置</el-button>
            <el-button type="primary" @click="runQuery">查询</el-button>
          </div>
        </div>
      </template>

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
          <el-select v-model="query.documentOrganizationCode" clearable filterable>
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

      <div class="qa-panel">
        <el-input v-model="question" placeholder="例如：近三个月的合同类档案有哪些？" clearable />
        <el-button @click="askQuestion">知识问答</el-button>
      </div>

      <el-alert v-if="qaAnswer" :title="qaAnswer" type="success" :closable="false" style="margin-bottom: 16px" />

      <el-table :data="queryResult.records" border>
        <el-table-column prop="archiveFilingCode" label="归档编码" width="180" />
        <el-table-column prop="documentName" label="文档名称" min-width="220" />
        <el-table-column prop="documentTypeName" label="文档类型" width="160" />
        <el-table-column prop="companyProjectName" label="公司/项目" width="180" />
        <el-table-column prop="carrierTypeCode" label="载体类型" width="120" />
        <el-table-column prop="dutyPerson" label="责任人" width="120" />
        <el-table-column prop="attachmentCount" label="附件数" width="90" />
        <el-table-column prop="aiArchiveSummary" label="AI摘要" min-width="280" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { askArchiveQuestion, fetchArchiveCreateOptions, fetchEffectiveDocumentTypeExtFields, queryArchives, type ArchiveQueryCommand } from '../../api/modules/archiveManagement'
import type { ArchiveCreateOptions, ArchiveQueryResult, DocumentTypeExtField } from '../../types'

const route = useRoute()
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
  documentOrganizationCode: ''
})

const queryFields = ref<DocumentTypeExtField[]>([])
const queryExtFilters = reactive<Record<string, string>>({})
const question = ref('')
const qaAnswer = ref('')
const queryResult = reactive<ArchiveQueryResult>({ records: [], queryFields: [] })

const loadOptions = async () => {
  const result = await fetchArchiveCreateOptions()
  Object.assign(options, result)
}

const handleQueryTypeChange = async (typeCode?: string) => {
  queryFields.value = typeCode ? await fetchEffectiveDocumentTypeExtFields(typeCode) : []
  queryFields.value = queryFields.value.filter(item => item.queryEnabledFlag === 'Y')
  Object.keys(queryExtFilters).forEach(key => delete queryExtFilters[key])
}

const runQuery = async () => {
  qaAnswer.value = ''
  const result = await queryArchives({ ...query, extFilters: { ...queryExtFilters } })
  queryResult.records = result.records
  queryResult.queryFields = result.queryFields
  queryFields.value = result.queryFields
}

const askQuestion = async () => {
  if (!question.value.trim()) {
    ElMessage.warning('请输入问题内容')
    return
  }
  const result = await askArchiveQuestion({
    question: question.value.trim(),
    documentTypeCode: query.documentTypeCode || undefined,
    companyProjectCode: query.companyProjectCode || undefined
  })
  qaAnswer.value = result.answer
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
    documentOrganizationCode: ''
  })
  Object.keys(queryExtFilters).forEach(key => delete queryExtFilters[key])
  queryFields.value = []
  question.value = ''
  qaAnswer.value = ''
  queryResult.records = []
  queryResult.queryFields = []
}

onMounted(async () => {
  const routeKeyword = typeof route.query.q === 'string' ? route.query.q.trim() : ''
  if (routeKeyword) {
    query.keyword = routeKeyword
    if (route.query.mode === 'ai') {
      question.value = routeKeyword
    }
  }
  await loadOptions()
  await runQuery()
})
</script>

<style scoped>
.query-page { display: grid; gap: 16px; }
.section-head { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; }
.section-head strong { display: block; font-size: 16px; color: #24324a; }
.section-head span { color: #7a879a; font-size: 12px; }
.section-actions { display: flex; gap: 8px; }
.form-grid { display: grid; gap: 12px 16px; }
.form-grid--4 { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.query-extra { margin: 8px 0 20px; padding-top: 8px; border-top: 1px dashed #e4e7ed; }
.query-extra__title { margin-bottom: 12px; font-size: 13px; font-weight: 600; color: #4b587c; }
.qa-panel { display: grid; grid-template-columns: 1fr auto; gap: 12px; margin-bottom: 16px; }
@media (max-width: 1280px) { .form-grid--4 { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 860px) { .form-grid--4 { grid-template-columns: 1fr; } .section-head { flex-direction: column; } .qa-panel { grid-template-columns: 1fr; } }
</style>
