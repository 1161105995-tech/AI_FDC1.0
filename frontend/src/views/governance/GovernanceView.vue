<template>
  <div class="governance-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="治理概览" name="overview">
        <div class="app-page">
          <div class="page-summary-grid">
            <el-card class="summary-card" shadow="never"><div class="summary-card__label">工作流定义</div><div class="summary-card__value">{{ workflows.length }}</div><div class="summary-card__meta">归档、借阅、盘点、销毁等流程治理</div></el-card>
            <el-card class="summary-card" shadow="never"><div class="summary-card__label">规则数量</div><div class="summary-card__value">{{ rules.length }}</div><div class="summary-card__meta">分类、期限、密级、质检等规则沉淀</div></el-card>
            <el-card class="summary-card" shadow="never"><div class="summary-card__label">AI 能力</div><div class="summary-card__value">{{ capabilities.length }}</div><div class="summary-card__meta">OCR、抽取、检索、问答、脱敏</div></el-card>
            <el-card class="summary-card" shadow="never"><div class="summary-card__label">安全策略</div><div class="summary-card__value">{{ policies.length }}</div><div class="summary-card__meta">权限、加密、水印、审计与风控策略</div></el-card>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="AI模型配置" name="model-config">
        <div class="model-page">
          <div class="toolbar">
            <el-input v-model="keyword" placeholder="搜索编码、名称或模型标识" clearable style="max-width: 320px" @keyup.enter="loadModels" />
            <div class="toolbar-actions">
              <el-button @click="loadModels">刷新</el-button>
              <el-button type="primary" @click="startCreate">新增模型</el-button>
            </div>
          </div>
          <el-table :data="models" border>
            <el-table-column prop="modelCode" label="模型编码" width="160" />
            <el-table-column prop="modelName" label="模型名称" width="180" />
            <el-table-column prop="modelType" label="模型类型" width="120" />
            <el-table-column prop="apiUrl" label="接口地址" min-width="200" show-overflow-tooltip />
            <el-table-column prop="modelIdentifier" label="模型标识" min-width="160" />
            <el-table-column prop="enabledFlag" label="状态" width="100"><template #default="{ row }"><el-tag :type="row.enabledFlag === 'Y' ? 'success' : 'info'">{{ row.enabledFlag === 'Y' ? '生效中' : '未生效' }}</el-tag></template></el-table-column>
            <el-table-column label="操作" width="260" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="startEdit(row.modelConfigId!)">编辑</el-button>
                <el-button link @click="testSaved(row.modelConfigId!)">测试</el-button>
                <el-button v-if="row.enabledFlag !== 'Y'" link type="success" @click="activate(row.modelConfigId!)">生效</el-button>
                <el-button v-else link type="warning" @click="deactivate(row.modelConfigId!)">失效</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-alert v-if="message" :title="message" type="success" :closable="false" style="margin-top: 12px" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑模型' : '新增模型'" width="760px">
      <el-form :model="form" label-width="120px">
        <div class="form-grid">
          <el-form-item label="模型编码" required><el-input v-model="form.modelCode" :disabled="!!editingId" /></el-form-item>
          <el-form-item label="模型名称" required><el-input v-model="form.modelName" /></el-form-item>
          <el-form-item label="模型类型" required><el-select v-model="form.modelType"><el-option label="CHAT" value="CHAT" /><el-option label="EMBEDDING" value="EMBEDDING" /><el-option label="RERANK" value="RERANK" /></el-select></el-form-item>
          <el-form-item label="接口地址" required><el-input v-model="form.apiUrl" /></el-form-item>
          <el-form-item label="API Key" required><el-input v-model="form.apiKey" show-password /></el-form-item>
          <el-form-item label="模型标识" required><el-input v-model="form.modelIdentifier" /></el-form-item>
          <el-form-item label="向量维度" required><el-input-number v-model="form.embeddingDimension" :min="1" style="width:100%" /></el-form-item>
          <el-form-item label="超时时间(秒)" required><el-input-number v-model="form.timeoutSeconds" :min="1" style="width:100%" /></el-form-item>
          <el-form-item label="Top K" required><el-input-number v-model="form.topK" :min="1" style="width:100%" /></el-form-item>
          <el-form-item label="相似度阈值"><el-input-number v-model="form.scoreThreshold" :min="0" :max="1" :step="0.01" style="width:100%" /></el-form-item>
          <el-form-item label="正式结果条数" required><el-input-number v-model="form.officialResultCount" :min="1" style="width:100%" /></el-form-item>
          <el-form-item label="正式结果阈值" required><el-input-number v-model="form.officialScoreThreshold" :min="0" :max="1" :step="0.01" style="width:100%" /></el-form-item>
          <el-form-item label="相关结果条数" required><el-input-number v-model="form.relatedResultCount" :min="1" style="width:100%" /></el-form-item>
          <el-form-item label="相关结果阈值" required><el-input-number v-model="form.relatedScoreThreshold" :min="0" :max="1" :step="0.01" style="width:100%" /></el-form-item>
          <el-form-item label="启用标志"><el-radio-group v-model="form.enabledFlag"><el-radio value="Y">Y</el-radio><el-radio value="N">N</el-radio></el-radio-group></el-form-item>
        </div>
        <el-form-item label="Prompt模板"><el-input v-model="form.promptTemplate" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <el-alert v-if="testMessage" :title="testMessage" :type="testStatus === 'SUCCESS' ? 'success' : 'error'" :closable="false" style="margin-top: 8px" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button @click="testCurrent">测试连通性</el-button>
        <el-button type="primary" @click="saveModel">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { fetchAiCapabilities, fetchRules, fetchSecurityPolicies, fetchWorkflowDefinitions } from '../../api/modules/governance'
import {
  activateAiModelConfig,
  createAiModelConfig,
  deactivateAiModelConfig,
  fetchAiModelConfigDetail,
  fetchAiModelConfigs,
  testAiModelConnection,
  testSavedAiModelConnection,
  updateAiModelConfig
} from '../../api/modules/aiModelConfig'
import type { AiCapability, AiModelConfig, RuleDefinition, SecurityPolicy, WorkflowDefinition } from '../../types'

const activeTab = ref('overview')
const keyword = ref('')
const workflows = ref<WorkflowDefinition[]>([])
const rules = ref<RuleDefinition[]>([])
const capabilities = ref<AiCapability[]>([])
const policies = ref<SecurityPolicy[]>([])
const models = ref<AiModelConfig[]>([])
const message = ref('')
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const testMessage = ref('')
const testStatus = ref('')

const form = reactive<AiModelConfig>({
  modelCode: '', modelName: '', modelType: 'CHAT', apiUrl: 'http://localhost:11434', apiKey: 'local-key', modelIdentifier: 'qwen3:8b', promptTemplate: '', embeddingDimension: 256, timeoutSeconds: 120, topK: 20, scoreThreshold: 0.5, officialResultCount: 10, officialScoreThreshold: 0.72, relatedResultCount: 20, relatedScoreThreshold: 0.45, enabledFlag: 'Y', remark: ''
})

const resetForm = () => {
  editingId.value = null
  Object.assign(form, { modelCode: '', modelName: '', modelType: 'CHAT', apiUrl: 'http://localhost:11434', apiKey: 'local-key', modelIdentifier: 'qwen3:8b', promptTemplate: '', embeddingDimension: 256, timeoutSeconds: 120, topK: 20, scoreThreshold: 0.5, officialResultCount: 10, officialScoreThreshold: 0.72, relatedResultCount: 20, relatedScoreThreshold: 0.45, enabledFlag: 'Y', remark: '' })
  testMessage.value = ''
  testStatus.value = ''
}

const loadModels = async () => { models.value = await fetchAiModelConfigs(keyword.value || undefined) }
const startCreate = () => { resetForm(); dialogVisible.value = true; activeTab.value = 'model-config' }
const startEdit = async (id: number) => { const detail = await fetchAiModelConfigDetail(id); editingId.value = id; Object.assign(form, detail); dialogVisible.value = true; activeTab.value = 'model-config' }
const saveModel = async () => {
  try {
    if (editingId.value) await updateAiModelConfig(editingId.value, form)
    else await createAiModelConfig(form)
    dialogVisible.value = false
    await loadModels()
    message.value = '模型配置已保存。'
    ElMessage.success(message.value)
  } catch (error: any) { ElMessage.error(error?.message || '保存失败') }
}
const activate = async (id: number) => { await activateAiModelConfig(id); await loadModels(); message.value = '模型已生效。' }
const deactivate = async (id: number) => { await deactivateAiModelConfig(id); await loadModels(); message.value = '模型已失效。' }
const testSaved = async (id: number) => { const result = await testSavedAiModelConnection(id); message.value = result.message }
const testCurrent = async () => { const result = await testAiModelConnection(form); testStatus.value = result.status; testMessage.value = result.message }

onMounted(async () => {
  const [workflowData, ruleData, capabilityData, policyData] = await Promise.all([fetchWorkflowDefinitions(), fetchRules(), fetchAiCapabilities(), fetchSecurityPolicies()])
  workflows.value = workflowData
  rules.value = ruleData
  capabilities.value = capabilityData
  policies.value = policyData
  await loadModels()
})
</script>

<style scoped>
.governance-page { display: grid; gap: 16px; }
.model-page { display: grid; gap: 16px; }
.toolbar { display: flex; justify-content: space-between; gap: 12px; align-items: center; }
.toolbar-actions { display: flex; gap: 8px; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap: 8px 16px; }
@media (max-width: 960px) { .toolbar { flex-direction: column; align-items: stretch; } .form-grid { grid-template-columns: 1fr; } }
</style>
