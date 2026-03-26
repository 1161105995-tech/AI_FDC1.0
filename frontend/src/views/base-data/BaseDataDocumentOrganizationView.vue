<template>
  <div class="document-organization-page">
    <el-card shadow="never">
      <div class="toolbar">
        <el-form :inline="true" :model="query" class="query-form">
          <el-form-item label="文档组织编码">
            <el-input v-model.trim="query.keyword" placeholder="请输入编码" clearable />
          </el-form-item>
          <el-form-item label="文档组织名称">
            <el-input v-model.trim="query.documentOrganizationName" placeholder="请输入名称" clearable />
          </el-form-item>
          <el-form-item label="国家">
            <el-select v-model="query.countryCode" placeholder="全部" clearable style="width: 160px" @change="handleQueryCountryChange">
              <el-option v-for="item in countries" :key="item.countryCode" :label="item.countryName" :value="item.countryCode" />
            </el-select>
          </el-form-item>
          <el-form-item label="城市">
            <el-select v-model="query.cityCode" placeholder="全部" clearable style="width: 160px">
              <el-option v-for="item in queryCities" :key="item.cityCode" :label="item.cityName" :value="item.cityCode" />
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
          <el-button type="success" @click="startCreate">新建文档组织</el-button>
        </div>
      </div>

      <el-alert
        v-if="editor.visible"
        :title="editor.mode === 'create' ? '在当前页直接新建文档组织' : editor.mode === 'edit' ? `编辑文档组织：${form.documentOrganizationCode}` : `查看文档组织：${form.documentOrganizationCode}`"
        type="info"
        :closable="false"
        class="editor-tip"
      />

      <div v-if="editor.visible" class="editor-panel">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="editor-form">
          <el-row :gutter="16">
            <el-col :md="8" :xs="24">
              <el-form-item label="文档组织编码" prop="documentOrganizationCode" required>
                <el-input v-model.trim="form.documentOrganizationCode" :disabled="editor.mode !== 'create' || isReadonly" maxlength="64" placeholder="请输入文档组织编码" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="文档组织名称" prop="documentOrganizationName" required>
                <el-input v-model.trim="form.documentOrganizationName" :disabled="isReadonly" maxlength="128" placeholder="请输入文档组织名称" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="国家" prop="countryCode" required>
                <el-select v-model="form.countryCode" :disabled="isReadonly" placeholder="请选择国家" style="width: 100%" @change="handleFormCountryChange">
                  <el-option v-for="item in countries" :key="item.countryCode" :label="item.countryName" :value="item.countryCode" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="城市" prop="cityCode">
                <el-select v-model="form.cityCode" :disabled="isReadonly" placeholder="请选择城市" clearable style="width: 100%">
                  <el-option v-for="item in formCities" :key="item.cityCode" :label="item.cityName" :value="item.cityCode" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="启用标识" prop="enabledFlag" required>
                <el-segmented v-model="form.enabledFlag" :options="flagOptions" :disabled="isReadonly" />
              </el-form-item>
            </el-col>
            <el-col :md="24" :xs="24">
              <el-form-item label="描述信息" prop="description" required>
                <el-input v-model.trim="form.description" :disabled="isReadonly" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="请输入描述信息" />
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
          <el-button v-if="editor.mode === 'view'" type="primary" plain @click="startEdit(form.documentOrganizationCode)">进入编辑</el-button>
          <el-button v-else type="primary" @click="submit">保存</el-button>
        </div>
      </div>

      <el-table :data="displayedItems" border empty-text="暂无文档组织数据，请调整筛选条件或新建数据">
        <el-table-column prop="documentOrganizationCode" label="文档组织编码" min-width="160" />
        <el-table-column prop="documentOrganizationName" label="文档组织名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="description" label="描述信息" min-width="220" show-overflow-tooltip />
        <el-table-column label="国家" min-width="140">
          <template #default="{ row }">{{ formatCountry(row.countryCode) }}</template>
        </el-table-column>
        <el-table-column label="城市" min-width="160">
          <template #default="{ row }">{{ formatCity(row.cityCode) }}</template>
        </el-table-column>
        <el-table-column label="启用标识" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.enabledFlag === 'Y' ? 'success' : 'info'">{{ row.enabledFlag === 'Y' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdateDate" label="最后更新时间" min-width="180" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="startView(row.documentOrganizationCode)">查看</el-button>
            <el-button link type="primary" @click="startEdit(row.documentOrganizationCode)">编辑</el-button>
            <el-popconfirm title="确认软删除该文档组织吗？" @confirm="removeItem(row.documentOrganizationCode)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import {
  createDocumentOrganization,
  deleteDocumentOrganization,
  fetchDocumentOrganizationCities,
  fetchDocumentOrganizationCountries,
  fetchDocumentOrganizationDetail,
  fetchDocumentOrganizations,
  updateDocumentOrganization,
  type DocumentOrganizationCreateCommand
} from '../../api/modules/documentOrganization'
import type { CountryOption, DocumentOrganizationCityOption, DocumentOrganizationDetail, DocumentOrganizationSummary } from '../../types'

const formRef = ref<FormInstance>()
const items = ref<DocumentOrganizationSummary[]>([])
const countries = ref<CountryOption[]>([])
const allCities = ref<DocumentOrganizationCityOption[]>([])
const queryCities = ref<DocumentOrganizationCityOption[]>([])
const formCities = ref<DocumentOrganizationCityOption[]>([])

const flagOptions = [
  { label: '启用', value: 'Y' },
  { label: '停用', value: 'N' }
]

const query = reactive({
  keyword: '',
  documentOrganizationName: '',
  countryCode: '',
  cityCode: '',
  enabledFlag: ''
})

const editor = reactive({
  visible: false,
  mode: 'create' as 'create' | 'edit' | 'view',
  lastUpdateDate: ''
})

const form = reactive<DocumentOrganizationCreateCommand>({
  documentOrganizationCode: '',
  documentOrganizationName: '',
  description: '',
  countryCode: '',
  cityCode: '',
  enabledFlag: 'Y'
})

const rules: FormRules<DocumentOrganizationCreateCommand> = {
  documentOrganizationCode: [
    { required: true, message: '请输入文档组织编码', trigger: 'blur' },
    { max: 64, message: '长度不能超过64位', trigger: 'blur' }
  ],
  documentOrganizationName: [
    { required: true, message: '请输入文档组织名称', trigger: 'blur' },
    { max: 128, message: '长度不能超过128位', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述信息', trigger: 'blur' },
    { max: 500, message: '长度不能超过500位', trigger: 'blur' }
  ],
  countryCode: [{ required: true, message: '请选择国家', trigger: 'change' }],
  cityCode: [{ max: 64, message: '长度不能超过64位', trigger: 'change' }],
  enabledFlag: [{ required: true, message: '请选择启用标识', trigger: 'change' }]
}

const isReadonly = computed(() => editor.mode === 'view')

const displayedItems = computed(() => {
  if (editor.visible && editor.mode === 'edit' && form.documentOrganizationCode) {
    return items.value.filter(item => item.documentOrganizationCode !== form.documentOrganizationCode)
  }
  return items.value
})

const resetFormState = () => {
  form.documentOrganizationCode = ''
  form.documentOrganizationName = ''
  form.description = ''
  form.countryCode = countries.value[0]?.countryCode ?? ''
  form.cityCode = ''
  form.enabledFlag = 'Y'
  editor.lastUpdateDate = ''
  formCities.value = form.countryCode ? allCities.value.filter((item) => item.countryCode === form.countryCode) : allCities.value
}

const fillForm = (detail: DocumentOrganizationDetail) => {
  form.documentOrganizationCode = detail.documentOrganizationCode
  form.documentOrganizationName = detail.documentOrganizationName
  form.description = detail.description
  form.countryCode = detail.countryCode
  form.cityCode = detail.cityCode ?? ''
  form.enabledFlag = detail.enabledFlag
  editor.lastUpdateDate = detail.lastUpdateDate
  formCities.value = form.countryCode ? allCities.value.filter((item) => item.countryCode === form.countryCode) : allCities.value
}

const loadList = async () => {
  items.value = await fetchDocumentOrganizations({
    keyword: query.keyword?.trim() || undefined,
    documentOrganizationName: query.documentOrganizationName?.trim() || undefined,
    countryCode: query.countryCode || undefined,
    cityCode: query.cityCode || undefined,
    enabledFlag: query.enabledFlag || undefined
  })
}

const loadMeta = async () => {
  countries.value = await fetchDocumentOrganizationCountries()
  allCities.value = await fetchDocumentOrganizationCities()
  queryCities.value = query.countryCode ? allCities.value.filter((item) => item.countryCode === query.countryCode) : allCities.value
  resetFormState()
}

const handleQueryCountryChange = () => {
  query.cityCode = ''
  queryCities.value = query.countryCode ? allCities.value.filter((item) => item.countryCode === query.countryCode) : allCities.value
}

const handleFormCountryChange = () => {
  form.cityCode = ''
  formCities.value = form.countryCode ? allCities.value.filter((item) => item.countryCode === form.countryCode) : allCities.value
}

const resetQuery = async () => {
  query.keyword = ''
  query.documentOrganizationName = ''
  query.countryCode = ''
  query.cityCode = ''
  query.enabledFlag = ''
  queryCities.value = allCities.value
  await loadList()
}

const startCreate = () => {
  editor.visible = true
  editor.mode = 'create'
  resetFormState()
}

const openDetailInEditor = async (documentOrganizationCode: string, mode: 'edit' | 'view') => {
  const detail = await fetchDocumentOrganizationDetail(documentOrganizationCode)
  editor.visible = true
  editor.mode = mode
  fillForm(detail)
}

const startEdit = async (documentOrganizationCode: string) => {
  await openDetailInEditor(documentOrganizationCode, 'edit')
}

const startView = async (documentOrganizationCode: string) => {
  await openDetailInEditor(documentOrganizationCode, 'view')
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
      await createDocumentOrganization({
        documentOrganizationCode: form.documentOrganizationCode,
        documentOrganizationName: form.documentOrganizationName,
        description: form.description,
        countryCode: form.countryCode,
        cityCode: form.cityCode || undefined,
        enabledFlag: form.enabledFlag
      })
      ElMessage.success('文档组织创建成功')
    } else {
      await updateDocumentOrganization(form.documentOrganizationCode, {
        documentOrganizationName: form.documentOrganizationName,
        description: form.description,
        countryCode: form.countryCode,
        cityCode: form.cityCode || undefined,
        enabledFlag: form.enabledFlag
      })
      ElMessage.success('文档组织更新成功')
    }
    cancelEditor()
    await loadList()
  } catch (error: any) {
    ElMessage.error(error?.message || '保存失败')
  }
}

const removeItem = async (documentOrganizationCode: string) => {
  try {
    await deleteDocumentOrganization(documentOrganizationCode)
    ElMessage.success('文档组织已软删除')
    if (form.documentOrganizationCode === documentOrganizationCode) {
      cancelEditor()
    }
    await loadList()
  } catch (error: any) {
    ElMessage.error(error?.message || '删除失败')
  }
}

const formatCountry = (countryCode?: string) => countries.value.find((item) => item.countryCode === countryCode)?.countryName ?? countryCode ?? '-'
const formatCity = (cityCode?: string) => allCities.value.find((item) => item.cityCode === cityCode)?.cityName ?? cityCode ?? '-'

onMounted(async () => {
  await loadMeta()
  await loadList()
})
</script>

<style scoped>
.document-organization-page { display: grid; gap: 16px; }
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
</style>
