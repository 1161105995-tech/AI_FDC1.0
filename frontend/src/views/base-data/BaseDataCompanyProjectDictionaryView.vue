<template>
  <div class="dictionary-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="国家字典" name="country">
        <el-card shadow="never">
          <template #header>
            <div class="section-header">
              <span>国家字典</span>
              <el-button type="primary" @click="openCountryDialog()">新增国家</el-button>
            </div>
          </template>
          <el-table :data="countries" border empty-text="暂无国家字典数据">
            <el-table-column prop="countryCode" label="国家编码" min-width="140" />
            <el-table-column prop="countryName" label="国家名称" min-width="180" />
            <el-table-column prop="sortOrder" label="排序" width="90" align="center" />
            <el-table-column label="启用标识" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.enabledFlag === 'Y' ? 'success' : 'info'">{{ row.enabledFlag === 'Y' ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="openCountryDialog(row)">编辑</el-button>
                <el-popconfirm title="确认软删除该国家字典吗？" @confirm="removeCountry(row.id)">
                  <template #reference>
                    <el-button link type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="组织类别字典" name="org-category">
        <el-card shadow="never">
          <template #header>
            <div class="section-header">
              <span>组织类别字典</span>
              <el-button type="primary" @click="openOrgDialog()">新增组织类别</el-button>
            </div>
          </template>
          <el-table :data="orgCategories" border empty-text="暂无组织类别字典数据">
            <el-table-column prop="categoryCode" label="组织类别编码" min-width="180" />
            <el-table-column prop="categoryName" label="组织类别名称" min-width="180" />
            <el-table-column prop="sortOrder" label="排序" width="90" align="center" />
            <el-table-column label="启用标识" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.enabledFlag === 'Y' ? 'success' : 'info'">{{ row.enabledFlag === 'Y' ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="openOrgDialog(row)">编辑</el-button>
                <el-popconfirm title="确认软删除该组织类别字典吗？" @confirm="removeOrgCategory(row.id)">
                  <template #reference>
                    <el-button link type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="countryDialog.visible" :title="countryDialog.mode === 'create' ? '新增国家字典' : '编辑国家字典'" width="520px">
      <el-form ref="countryFormRef" :model="countryForm" :rules="countryRules" label-width="110px">
        <el-form-item label="国家编码" prop="countryCode">
          <el-input v-model.trim="countryForm.countryCode" :disabled="countryDialog.mode === 'edit'" maxlength="32" />
        </el-form-item>
        <el-form-item label="国家名称" prop="countryName">
          <el-input v-model.trim="countryForm.countryName" maxlength="128" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="countryForm.sortOrder" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="启用标识" prop="enabledFlag">
          <el-segmented v-model="countryForm.enabledFlag" :options="flagOptions" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="countryDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitCountry">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="orgDialog.visible" :title="orgDialog.mode === 'create' ? '新增组织类别字典' : '编辑组织类别字典'" width="520px">
      <el-form ref="orgFormRef" :model="orgForm" :rules="orgRules" label-width="110px">
        <el-form-item label="类别编码" prop="categoryCode">
          <el-input v-model.trim="orgForm.categoryCode" :disabled="orgDialog.mode === 'edit'" maxlength="64" />
        </el-form-item>
        <el-form-item label="类别名称" prop="categoryName">
          <el-input v-model.trim="orgForm.categoryName" maxlength="128" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="orgForm.sortOrder" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="启用标识" prop="enabledFlag">
          <el-segmented v-model="orgForm.enabledFlag" :options="flagOptions" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orgDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitOrgCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import {
  createCountryDictionary,
  createOrgCategoryDictionary,
  deleteCountryDictionary,
  deleteOrgCategoryDictionary,
  fetchCountryDictionaries,
  fetchOrgCategoryDictionaries,
  updateCountryDictionary,
  updateOrgCategoryDictionary,
  type CountryDictionaryCommand,
  type OrgCategoryDictionaryCommand
} from '../../api/modules/companyProject'
import type { CountryDictionaryItem, OrgCategoryDictionaryItem } from '../../types'

const activeTab = ref('country')
const countries = ref<CountryDictionaryItem[]>([])
const orgCategories = ref<OrgCategoryDictionaryItem[]>([])
const countryFormRef = ref<FormInstance>()
const orgFormRef = ref<FormInstance>()
const flagOptions = [
  { label: '启用', value: 'Y' },
  { label: '停用', value: 'N' }
]

const countryDialog = reactive({ visible: false, mode: 'create' as 'create' | 'edit', id: 0 })
const orgDialog = reactive({ visible: false, mode: 'create' as 'create' | 'edit', id: 0 })

const countryForm = reactive<CountryDictionaryCommand>({ countryCode: '', countryName: '', sortOrder: 1, enabledFlag: 'Y' })
const orgForm = reactive<OrgCategoryDictionaryCommand>({ categoryCode: '', categoryName: '', sortOrder: 1, enabledFlag: 'Y' })

const countryRules: FormRules<CountryDictionaryCommand> = {
  countryCode: [{ required: true, message: '请输入国家编码', trigger: 'blur' }],
  countryName: [{ required: true, message: '请输入国家名称', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序', trigger: 'change' }],
  enabledFlag: [{ required: true, message: '请选择启用标识', trigger: 'change' }]
}
const orgRules: FormRules<OrgCategoryDictionaryCommand> = {
  categoryCode: [{ required: true, message: '请输入组织类别编码', trigger: 'blur' }],
  categoryName: [{ required: true, message: '请输入组织类别名称', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序', trigger: 'change' }],
  enabledFlag: [{ required: true, message: '请选择启用标识', trigger: 'change' }]
}

const loadAll = async () => {
  countries.value = await fetchCountryDictionaries()
  orgCategories.value = await fetchOrgCategoryDictionaries()
}

const openCountryDialog = (row?: CountryDictionaryItem) => {
  countryDialog.visible = true
  countryDialog.mode = row ? 'edit' : 'create'
  countryDialog.id = row?.id ?? 0
  countryForm.countryCode = row?.countryCode ?? ''
  countryForm.countryName = row?.countryName ?? ''
  countryForm.sortOrder = row?.sortOrder ?? 1
  countryForm.enabledFlag = row?.enabledFlag ?? 'Y'
}

const openOrgDialog = (row?: OrgCategoryDictionaryItem) => {
  orgDialog.visible = true
  orgDialog.mode = row ? 'edit' : 'create'
  orgDialog.id = row?.id ?? 0
  orgForm.categoryCode = row?.categoryCode ?? ''
  orgForm.categoryName = row?.categoryName ?? ''
  orgForm.sortOrder = row?.sortOrder ?? 1
  orgForm.enabledFlag = row?.enabledFlag ?? 'Y'
}

const submitCountry = async () => {
  try {
    const valid = await countryFormRef.value?.validate().catch(() => false)
    if (valid === false) return
    if (countryDialog.mode === 'create') {
      await createCountryDictionary(countryForm)
      ElMessage.success('国家字典创建成功')
    } else {
      await updateCountryDictionary(countryDialog.id, countryForm)
      ElMessage.success('国家字典更新成功')
    }
    countryDialog.visible = false
    await loadAll()
  } catch (error: any) {
    ElMessage.error(error?.message || '国家字典保存失败')
  }
}

const submitOrgCategory = async () => {
  try {
    const valid = await orgFormRef.value?.validate().catch(() => false)
    if (valid === false) return
    if (orgDialog.mode === 'create') {
      await createOrgCategoryDictionary(orgForm)
      ElMessage.success('组织类别字典创建成功')
    } else {
      await updateOrgCategoryDictionary(orgDialog.id, orgForm)
      ElMessage.success('组织类别字典更新成功')
    }
    orgDialog.visible = false
    await loadAll()
  } catch (error: any) {
    ElMessage.error(error?.message || '组织类别字典保存失败')
  }
}

const removeCountry = async (id: number) => {
  try {
    await deleteCountryDictionary(id)
    ElMessage.success('国家字典已软删除')
    await loadAll()
  } catch (error: any) {
    ElMessage.error(error?.message || '国家字典删除失败')
  }
}

const removeOrgCategory = async (id: number) => {
  try {
    await deleteOrgCategoryDictionary(id)
    ElMessage.success('组织类别字典已软删除')
    await loadAll()
  } catch (error: any) {
    ElMessage.error(error?.message || '组织类别字典删除失败')
  }
}

onMounted(loadAll)
</script>

<style scoped>
.dictionary-page { display: grid; gap: 16px; }
.section-header { display: flex; justify-content: space-between; align-items: center; }
</style>