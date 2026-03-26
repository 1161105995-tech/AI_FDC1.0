<template>
  <div class="dict-page">
    <el-card shadow="never" class="dict-left">
      <template #header>
        <div class="panel-header">
          <div>
            <strong>字典分类</strong>
            <span>维护载体类型、附件类型、档案类型等正式主数据。</span>
          </div>
          <el-button type="primary" size="small" @click="openCategoryDialog()">新增分类</el-button>
        </div>
      </template>
      <el-table :data="categories" highlight-current-row @current-change="handleCategoryChange">
        <el-table-column prop="categoryName" label="分类名称" min-width="160" />
        <el-table-column prop="itemCount" label="项数" width="70" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button link type="primary" @click.stop="openCategoryDialog(row)">编辑</el-button>
            <el-button link type="danger" @click.stop="handleDeleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" class="dict-right">
      <template #header>
        <div class="panel-header">
          <div>
            <strong>{{ currentCategory?.categoryName || '字典项列表' }}</strong>
            <span>{{ currentCategory?.categoryCode || '请选择左侧分类' }}</span>
          </div>
          <el-button type="primary" size="small" :disabled="!currentCategory" @click="openItemDialog()">新增字典项</el-button>
        </div>
      </template>
      <el-table :data="items">
        <el-table-column prop="itemCode" label="编码" width="180" />
        <el-table-column prop="itemName" label="名称" min-width="180" />
        <el-table-column prop="itemValue" label="值" min-width="160" />
        <el-table-column prop="sortOrder" label="排序" width="90" />
        <el-table-column prop="enabledFlag" label="启用" width="80" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="openItemDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDeleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="categoryDialogVisible" :title="categoryDialogMode === 'create' ? '新增字典分类' : '编辑字典分类'" width="520px">
      <el-form :model="categoryForm" label-width="100px">
        <el-form-item label="分类编码" required><el-input v-model="categoryForm.categoryCode" :disabled="categoryDialogMode === 'edit'" /></el-form-item>
        <el-form-item label="分类名称" required><el-input v-model="categoryForm.categoryName" /></el-form-item>
        <el-form-item label="启用标志"><el-radio-group v-model="categoryForm.enabledFlag"><el-radio value="Y">Y</el-radio><el-radio value="N">N</el-radio></el-radio-group></el-form-item>
        <el-form-item label="说明"><el-input v-model="categoryForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="categoryDialogVisible = false">取消</el-button><el-button type="primary" @click="submitCategory">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="itemDialogVisible" :title="itemDialogMode === 'create' ? '新增字典项' : '编辑字典项'" width="520px">
      <el-form :model="itemForm" label-width="100px">
        <el-form-item label="项编码" required><el-input v-model="itemForm.itemCode" :disabled="itemDialogMode === 'edit'" /></el-form-item>
        <el-form-item label="项名称" required><el-input v-model="itemForm.itemName" /></el-form-item>
        <el-form-item label="项值"><el-input v-model="itemForm.itemValue" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="itemForm.sortOrder" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="启用标志"><el-radio-group v-model="itemForm.enabledFlag"><el-radio value="Y">Y</el-radio><el-radio value="N">N</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="itemDialogVisible = false">取消</el-button><el-button type="primary" @click="submitItem">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import {
  createDictionaryCategory,
  createDictionaryItem,
  deleteDictionaryCategory,
  deleteDictionaryItem,
  fetchDictionaryCategories,
  fetchDictionaryItems,
  updateDictionaryCategory,
  updateDictionaryItem
} from '../../api/modules/dictionary'
import type { DictionaryCategory, DictionaryItem } from '../../types'

const categories = ref<DictionaryCategory[]>([])
const items = ref<DictionaryItem[]>([])
const currentCategory = ref<DictionaryCategory>()
const categoryDialogVisible = ref(false)
const categoryDialogMode = ref<'create'|'edit'>('create')
const itemDialogVisible = ref(false)
const itemDialogMode = ref<'create'|'edit'>('create')

const categoryForm = reactive({ categoryCode: '', categoryName: '', description: '', enabledFlag: 'Y' as 'Y'|'N' })
const itemForm = reactive({ itemCode: '', itemName: '', itemValue: '', sortOrder: 1, enabledFlag: 'Y' as 'Y'|'N' })

const loadCategories = async () => {
  categories.value = await fetchDictionaryCategories()
  if (!currentCategory.value && categories.value.length) {
    currentCategory.value = categories.value[0]
    await loadItems()
  }
}
const loadItems = async () => {
  if (!currentCategory.value) { items.value = []; return }
  items.value = await fetchDictionaryItems(currentCategory.value.categoryCode)
}
const handleCategoryChange = async (row?: DictionaryCategory) => { currentCategory.value = row; await loadItems() }
const openCategoryDialog = (row?: DictionaryCategory) => { categoryDialogMode.value = row ? 'edit' : 'create'; categoryForm.categoryCode = row?.categoryCode || ''; categoryForm.categoryName = row?.categoryName || ''; categoryForm.description = row?.description || ''; categoryForm.enabledFlag = row?.enabledFlag || 'Y'; categoryDialogVisible.value = true }
const submitCategory = async () => {
  try {
    if (categoryDialogMode.value === 'create') await createDictionaryCategory({ ...categoryForm })
    else await updateDictionaryCategory(categoryForm.categoryCode, { categoryName: categoryForm.categoryName, description: categoryForm.description, enabledFlag: categoryForm.enabledFlag })
    categoryDialogVisible.value = false
    await loadCategories()
    ElMessage.success('字典分类已保存')
  } catch (error: any) { ElMessage.error(error?.message || '保存失败') }
}
const handleDeleteCategory = async (row: DictionaryCategory) => {
  try { await ElMessageBox.confirm(`确认删除分类 ${row.categoryName} 吗？`, '提示', { type: 'warning' }); await deleteDictionaryCategory(row.categoryCode); if (currentCategory.value?.categoryCode === row.categoryCode) currentCategory.value = undefined; await loadCategories(); await loadItems(); ElMessage.success('分类已删除') } catch (error: any) { if (error !== 'cancel') ElMessage.error(error?.message || '删除失败') }
}
const openItemDialog = (row?: DictionaryItem) => { itemDialogMode.value = row ? 'edit' : 'create'; itemForm.itemCode = row?.itemCode || ''; itemForm.itemName = row?.itemName || ''; itemForm.itemValue = row?.itemValue || ''; itemForm.sortOrder = row?.sortOrder || 1; itemForm.enabledFlag = row?.enabledFlag || 'Y'; itemDialogVisible.value = true }
const submitItem = async () => {
  if (!currentCategory.value) return
  try {
    if (itemDialogMode.value === 'create') await createDictionaryItem(currentCategory.value.categoryCode, { ...itemForm })
    else await updateDictionaryItem(currentCategory.value.categoryCode, itemForm.itemCode, { itemName: itemForm.itemName, itemValue: itemForm.itemValue, sortOrder: itemForm.sortOrder, enabledFlag: itemForm.enabledFlag })
    itemDialogVisible.value = false
    await loadItems()
    await loadCategories()
    ElMessage.success('字典项已保存')
  } catch (error: any) { ElMessage.error(error?.message || '保存失败') }
}
const handleDeleteItem = async (row: DictionaryItem) => {
  if (!currentCategory.value) return
  try { await ElMessageBox.confirm(`确认删除字典项 ${row.itemName} 吗？`, '提示', { type: 'warning' }); await deleteDictionaryItem(currentCategory.value.categoryCode, row.itemCode); await loadItems(); await loadCategories(); ElMessage.success('字典项已删除') } catch (error: any) { if (error !== 'cancel') ElMessage.error(error?.message || '删除失败') }
}

onMounted(async () => { await loadCategories() })
</script>

<style scoped>
.dict-page { display: grid; grid-template-columns: 420px 1fr; gap: 16px; }
.panel-header { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.panel-header strong { display: block; color: #24324a; font-size: 16px; }
.panel-header span { color: #7a879a; font-size: 12px; }
@media (max-width: 1200px) { .dict-page { grid-template-columns: 1fr; } }
</style>
