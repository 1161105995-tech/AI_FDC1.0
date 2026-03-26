<template>
  <div class="document-type-page">
    <div class="page-title">文档类型管理</div>

    <div class="document-type-grid">
      <el-card shadow="never" class="tree-panel">
        <template #header>
          <div class="panel-header">
            <div>
              <strong>文档类型树</strong>
              <span>支持最多 5 层配置</span>
            </div>
            <el-button type="primary" size="small" @click="openCreateDialog()">新建</el-button>
          </div>
        </template>

        <el-tree
          v-if="treeData.length"
          :data="treeData"
          node-key="typeCode"
          default-expand-all
          highlight-current
          :expand-on-click-node="false"
          :indent="18"
          :props="treeProps"
          @node-click="handleNodeSelect"
        >
          <template #default="{ data }">
            <div class="tree-node">
              <div class="tree-node__content">
                <strong>{{ data.typeName }}</strong>
                <span>{{ data.typeCode }}</span>
              </div>
              <el-tag size="small" :type="data.enabledFlag === 'Y' ? 'success' : 'info'">
                {{ data.enabledFlag === 'Y' ? '启用' : '停用' }}
              </el-tag>
            </div>
          </template>
        </el-tree>
        <el-empty v-else description="暂无文档类型数据" />
      </el-card>

      <div class="content-panel">
        <el-card shadow="never" class="detail-panel">
          <template #header>
            <div class="panel-header">
              <div>
                <strong>节点详情</strong>
                <span>查看、编辑与软删除</span>
              </div>
              <div class="panel-actions">
                <el-button size="small" :disabled="!selectedNode" @click="openCreateDialog(selectedNode?.typeCode)">新增下级</el-button>
                <el-button size="small" :disabled="!selectedNode" @click="openEditDialog">编辑</el-button>
                <el-button size="small" type="danger" plain :disabled="!selectedNode" @click="handleDelete">删除</el-button>
              </div>
            </div>
          </template>

          <el-descriptions v-if="selectedNode" :column="2" border>
            <el-descriptions-item label="编码">{{ selectedNode.typeCode }}</el-descriptions-item>
            <el-descriptions-item label="名称">{{ selectedNode.typeName }}</el-descriptions-item>
            <el-descriptions-item label="上级编码">{{ selectedNode.parentCode || 'ROOT' }}</el-descriptions-item>
            <el-descriptions-item label="层级">第 {{ selectedNode.levelNum }} 层</el-descriptions-item>
            <el-descriptions-item label="启用标志">{{ selectedNode.enabledFlag }}</el-descriptions-item>
            <el-descriptions-item label="软删除标识">{{ selectedNode.deleteFlag }}</el-descriptions-item>
            <el-descriptions-item label="创建人">{{ selectedNode.createdBy }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatTime(selectedNode.creationDate) }}</el-descriptions-item>
            <el-descriptions-item label="最后更新人">{{ selectedNode.lastUpdatedBy }}</el-descriptions-item>
            <el-descriptions-item label="最后更新时间">{{ formatTime(selectedNode.lastUpdateDate) }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ selectedNode.description || '无' }}</el-descriptions-item>
          </el-descriptions>
          <el-empty v-else description="请选择左侧节点查看详情" />
        </el-card>

        <el-card shadow="never" class="ext-panel">
          <template #header>
            <div class="panel-header">
              <div>
                <strong>扩展字段配置</strong>
                <span>所有层级都可定义，创建档案时会自动继承上级有效字段。</span>
              </div>
              <div class="panel-actions">
                <el-button size="small" :disabled="!selectedNode" @click="loadExtFields">刷新</el-button>
                <el-button size="small" type="primary" :disabled="!selectedNode" @click="openFieldDialog()">新增字段</el-button>
              </div>
            </div>
          </template>
          <el-table :data="extFields" size="small">
            <el-table-column prop="fieldCode" label="字段编码" width="220" />
            <el-table-column prop="fieldName" label="字段名称" width="150" />
            <el-table-column prop="fieldType" label="字段类型" width="100" />
            <el-table-column prop="dictCategoryCode" label="字典对象" width="140" />
            <el-table-column prop="requiredFlag" label="必填" width="80" />
            <el-table-column prop="enabledFlag" label="生效" width="80" />
            <el-table-column prop="queryEnabledFlag" label="查询项" width="90" />
            <el-table-column prop="formSortOrder" label="表单顺序" width="90" />
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openFieldDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteField(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

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
            <el-table-column prop="businessKey" label="业务主键" width="160" />
            <el-table-column prop="operationSummary" label="摘要" min-width="180" />
            <el-table-column prop="operatorName" label="操作人" width="120" />
            <el-table-column label="操作时间" width="180">
              <template #default="{ row }">{{ formatTime(row.operationTime) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新建文档类型' : '编辑文档类型'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="上级编码">
          <CommonTreeSelect v-model="form.parentCode" :data="parentTreeOptions" placeholder="ROOT 根节点" label-key="typeName" value-key="typeCode" children-key="children" />
        </el-form-item>
        <el-form-item label="编码" required>
          <el-input v-model="form.typeCode" :disabled="dialogMode === 'edit'" />
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="form.typeName" />
        </el-form-item>
        <el-form-item label="启用标志">
          <el-radio-group v-model="form.enabledFlag">
            <el-radio value="Y">Y</el-radio>
            <el-radio value="N">N</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="fieldDialogVisible" :title="fieldDialogMode === 'create' ? '新增扩展字段' : '编辑扩展字段'" width="560px">
      <el-form :model="fieldForm" label-width="110px">
        <el-form-item label="字段名称" required><el-input v-model="fieldForm.fieldName" /></el-form-item>
        <el-form-item label="字段类型" required>
          <el-radio-group v-model="fieldForm.fieldType">
            <el-radio value="TEXT">文本</el-radio>
            <el-radio value="DICT">数据字典</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="字典对象" v-if="fieldForm.fieldType === 'DICT'" required><el-input v-model="fieldForm.dictCategoryCode" placeholder="例如：ARCHIVE_ATTACHMENT_TYPE" /></el-form-item>
        <div class="field-grid">
          <el-form-item label="是否必填"><el-switch v-model="fieldForm.requiredFlag" active-value="Y" inactive-value="N" /></el-form-item>
          <el-form-item label="是否生效"><el-switch v-model="fieldForm.enabledFlag" active-value="Y" inactive-value="N" /></el-form-item>
          <el-form-item label="查询条件"><el-switch v-model="fieldForm.queryEnabledFlag" active-value="Y" inactive-value="N" /></el-form-item>
          <el-form-item label="表单顺序"><el-input-number v-model="fieldForm.formSortOrder" :min="1" style="width: 100%" /></el-form-item>
          <el-form-item label="查询顺序"><el-input-number v-model="fieldForm.querySortOrder" :min="1" style="width: 100%" /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="fieldDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFieldForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { fetchDictionaryCategories } from '../../api/modules/dictionary'
import CommonTreeSelect from '../../components/CommonTreeSelect.vue'
import {
  createDocumentType,
  deleteDocumentType,
  fetchDocumentTypeTree,
  fetchModuleAudits,
  updateDocumentType,
  type DocumentTypeCreateCommand,
  type DocumentTypeUpdateCommand
} from '../../api/modules/documentType'
import {
  createDocumentTypeExtField,
  deleteDocumentTypeExtField,
  fetchDocumentTypeExtFields,
  updateDocumentTypeExtField,
  type DocumentTypeExtFieldCreateCommand
} from '../../api/modules/archiveManagement'
import type { AuditRecord, DictionaryCategory, DocumentTypeExtField, DocumentTypeTreeNode } from '../../types'

const treeData = ref<DocumentTypeTreeNode[]>([])
const selectedNode = ref<DocumentTypeTreeNode>()
const auditRecords = ref<AuditRecord[]>([])
const extFields = ref<DocumentTypeExtField[]>([])
const dictionaryCategories = ref<DictionaryCategory[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const fieldDialogVisible = ref(false)
const fieldDialogMode = ref<'create' | 'edit'>('create')
const treeProps = { label: 'typeName', children: 'children' }
const selectedFieldCode = ref('')

const form = reactive<DocumentTypeCreateCommand & DocumentTypeUpdateCommand>({ typeCode: '', typeName: '', description: '', enabledFlag: 'Y', parentCode: '' })
const fieldForm = reactive<DocumentTypeExtFieldCreateCommand>({ fieldName: '', fieldType: 'TEXT', dictCategoryCode: '', requiredFlag: 'N', enabledFlag: 'Y', formSortOrder: 1, queryEnabledFlag: 'N', querySortOrder: 1 })

const normalizeTree = (nodes: DocumentTypeTreeNode[]): DocumentTypeTreeNode[] => (Array.isArray(nodes) ? nodes : []).map(node => ({ ...node, children: normalizeTree(Array.isArray(node.children) ? node.children : []) }))
const flattenNodes = (nodes: DocumentTypeTreeNode[]): DocumentTypeTreeNode[] => nodes.flatMap(node => [node, ...flattenNodes(node.children || [])])
const isDescendant = (candidate: DocumentTypeTreeNode, targetCode?: string) => !!targetCode && !!candidate.ancestorPath && candidate.ancestorPath.split('/').includes(targetCode)
const filterTreeNodes = (nodes: DocumentTypeTreeNode[], predicate: (node: DocumentTypeTreeNode) => boolean): DocumentTypeTreeNode[] => nodes.map(node => { const children = filterTreeNodes(node.children || [], predicate); return predicate(node) ? { ...node, children } : null }).filter((node): node is DocumentTypeTreeNode => Boolean(node))
const parentTreeOptions = computed(() => dialogMode.value === 'create' ? filterTreeNodes(treeData.value, node => node.levelNum < 5) : filterTreeNodes(treeData.value, node => node.typeCode !== selectedNode.value?.typeCode && !isDescendant(node, selectedNode.value?.typeCode)))

const loadTree = async () => {
  treeData.value = normalizeTree(await fetchDocumentTypeTree())
  if (!selectedNode.value && treeData.value.length > 0) selectedNode.value = treeData.value[0]
  else if (selectedNode.value) selectedNode.value = flattenNodes(treeData.value).find(item => item.typeCode === selectedNode.value?.typeCode) ?? treeData.value[0]
  if (selectedNode.value) await loadExtFields()
}
const loadAudits = async () => { auditRecords.value = await fetchModuleAudits('DOCUMENT_TYPE') }
const loadDictionaryCategories = async () => { dictionaryCategories.value = await fetchDictionaryCategories() }
const loadExtFields = async () => { if (!selectedNode.value) return; extFields.value = await fetchDocumentTypeExtFields(selectedNode.value.typeCode) }
const handleNodeSelect = async (node: DocumentTypeTreeNode) => { selectedNode.value = node; await loadExtFields() }
const resetForm = () => { form.typeCode = ''; form.typeName = ''; form.description = ''; form.enabledFlag = 'Y'; form.parentCode = '' }
const openCreateDialog = (parentCode?: string) => { dialogMode.value = 'create'; resetForm(); form.parentCode = parentCode || ''; dialogVisible.value = true }
const openEditDialog = () => { if (!selectedNode.value) return ElMessage.warning('请先选择一个文档类型节点'); dialogMode.value = 'edit'; form.typeCode = selectedNode.value.typeCode; form.typeName = selectedNode.value.typeName; form.description = selectedNode.value.description || ''; form.enabledFlag = selectedNode.value.enabledFlag; form.parentCode = selectedNode.value.parentCode || ''; dialogVisible.value = true }
const submitForm = async () => {
  const trimmedTypeCode = form.typeCode.trim(); const trimmedTypeName = form.typeName.trim();
  if (!trimmedTypeName) return ElMessage.warning('请输入文档类型名称')
  if (dialogMode.value === 'create' && !trimmedTypeCode) return ElMessage.warning('请输入文档类型编码')
  try {
    if (dialogMode.value === 'create') selectedNode.value = await createDocumentType({ typeCode: trimmedTypeCode, typeName: trimmedTypeName, description: form.description.trim(), enabledFlag: form.enabledFlag, parentCode: form.parentCode || undefined })
    else if (selectedNode.value) selectedNode.value = await updateDocumentType(selectedNode.value.typeCode, { typeName: trimmedTypeName, description: form.description.trim(), enabledFlag: form.enabledFlag, parentCode: form.parentCode || undefined })
    dialogVisible.value = false
    await Promise.all([loadTree(), loadAudits()])
    ElMessage.success('保存成功')
  } catch (error: any) { ElMessage.error(error?.message || '保存失败') }
}
const handleDelete = async () => {
  if (!selectedNode.value) return ElMessage.warning('请先选择一个文档类型节点')
  try { await ElMessageBox.confirm(`确认删除文档类型 ${selectedNode.value.typeName} 吗？`, '提示', { type: 'warning' }); await deleteDocumentType(selectedNode.value.typeCode); selectedNode.value = undefined; extFields.value = []; await Promise.all([loadTree(), loadAudits()]); ElMessage.success('删除成功') } catch (error: any) { if (error !== 'cancel') ElMessage.error(error?.message || '删除失败') }
}
const resetFieldForm = () => { fieldForm.fieldName = ''; fieldForm.fieldType = 'TEXT'; fieldForm.dictCategoryCode = ''; fieldForm.requiredFlag = 'N'; fieldForm.enabledFlag = 'Y'; fieldForm.formSortOrder = 1; fieldForm.queryEnabledFlag = 'N'; fieldForm.querySortOrder = 1; selectedFieldCode.value = '' }
const openFieldDialog = (field?: DocumentTypeExtField) => { if (!selectedNode.value) return ElMessage.warning('请先选择一个文档类型节点'); fieldDialogMode.value = field ? 'edit' : 'create'; resetFieldForm(); if (field) { selectedFieldCode.value = field.fieldCode; fieldForm.fieldName = field.fieldName; fieldForm.fieldType = field.fieldType; fieldForm.dictCategoryCode = field.dictCategoryCode || ''; fieldForm.requiredFlag = field.requiredFlag; fieldForm.enabledFlag = field.enabledFlag; fieldForm.formSortOrder = field.formSortOrder; fieldForm.queryEnabledFlag = field.queryEnabledFlag; fieldForm.querySortOrder = field.querySortOrder } fieldDialogVisible.value = true }
const submitFieldForm = async () => {
  if (!selectedNode.value) return
  if (!fieldForm.fieldName.trim()) return ElMessage.warning('请输入字段名称')
  if (fieldForm.fieldType === 'DICT' && !fieldForm.dictCategoryCode?.trim()) return ElMessage.warning('请输入数据字典对象')
  try {
    if (fieldDialogMode.value === 'create') await createDocumentTypeExtField(selectedNode.value.typeCode, { ...fieldForm, fieldName: fieldForm.fieldName.trim(), dictCategoryCode: fieldForm.dictCategoryCode?.trim() || undefined })
    else await updateDocumentTypeExtField(selectedNode.value.typeCode, selectedFieldCode.value, { ...fieldForm, fieldName: fieldForm.fieldName.trim(), dictCategoryCode: fieldForm.dictCategoryCode?.trim() || undefined })
    fieldDialogVisible.value = false
    await loadExtFields()
    ElMessage.success('扩展字段保存成功')
  } catch (error: any) { ElMessage.error(error?.message || '扩展字段保存失败') }
}
const handleDeleteField = async (field: DocumentTypeExtField) => {
  if (!selectedNode.value) return
  try { await ElMessageBox.confirm(`确认删除扩展字段 ${field.fieldName} 吗？`, '提示', { type: 'warning' }); await deleteDocumentTypeExtField(selectedNode.value.typeCode, field.fieldCode); await loadExtFields(); ElMessage.success('扩展字段已删除') } catch (error: any) { if (error !== 'cancel') ElMessage.error(error?.message || '删除失败') }
}
const formatTime = (value?: string) => value ? value.replace('T', ' ').slice(0, 19) : '-'

onMounted(async () => { await Promise.all([loadTree(), loadAudits(), loadDictionaryCategories()]) })
</script>

<style scoped>
.document-type-page { display: grid; gap: 16px; }
.page-title { font-size: 24px; font-weight: 700; color: #20314d; }
.document-type-grid { display: grid; grid-template-columns: 340px 1fr; gap: 16px; align-items: start; }
.tree-panel, .detail-panel, .audit-panel, .ext-panel { border: none; }
.content-panel { display: grid; gap: 16px; }
.panel-header { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.panel-header.simple { align-items: flex-start; }
.panel-header strong { display: block; color: #24324a; font-size: 16px; }
.panel-header span { color: #7a879a; font-size: 12px; }
.panel-actions { display: flex; gap: 8px; }
.tree-node { width: 100%; min-height: 42px; display: flex; justify-content: space-between; align-items: flex-start; gap: 8px; padding: 4px 0; box-sizing: border-box; }
.tree-node__content { min-width: 0; display: flex; flex-direction: column; gap: 2px; }
.tree-node__content strong { color: #24324a; line-height: 1.35; word-break: break-all; }
.tree-node__content span { color: #7a879a; font-size: 12px; line-height: 1.2; word-break: break-all; }
:deep(.el-tree-node__content) { min-height: 46px; align-items: flex-start; }
:deep(.el-tree-node__label) { width: 100%; }
.field-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 8px 16px; }
@media (max-width: 1200px) { .document-type-grid { grid-template-columns: 1fr; } .field-grid { grid-template-columns: 1fr; } }
</style>
