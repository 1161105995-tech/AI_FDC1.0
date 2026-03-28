<template>
  <div class="page">
    <el-card shadow="never">
      <template #header>
        <div class="section-head">
          <div>
            <strong>成册整理</strong>
            <span>支持按业务编号、期间或人工勾选完成同册整理，并直接引导进入入库。</span>
          </div>
          <div class="section-actions">
            <el-button @click="loadOptions">刷新</el-button>
            <el-button type="primary" @click="handlePreview" :loading="previewLoading">预览分组</el-button>
          </div>
        </div>
      </template>

      <div class="toolbar">
        <el-radio-group v-model="bindMode">
          <el-radio-button v-for="mode in bindOptions.bindModes" :key="mode.code" :label="mode.code">
            {{ mode.name }}
          </el-radio-button>
        </el-radio-group>
      </div>

      <div class="filter-grid">
        <el-input v-model="filters.keyword" clearable placeholder="档案编号/题名/业务编号" />
        <el-select v-model="filters.documentTypeCode" clearable filterable placeholder="文档类型">
          <el-option v-for="item in createOptions.documentTypes" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
        <el-select v-model="filters.companyProjectCode" clearable filterable placeholder="公司/项目">
          <el-option v-for="item in createOptions.companyProjects" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </div>

      <el-alert
        v-if="bindMode === 'MANUAL'"
        type="info"
        :closable="false"
        show-icon
        title="人工勾选模式会按当前已选择档案生成一个或多个手工册。"
        class="tips"
      />

      <el-table :data="filteredCandidates" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="48" />
        <el-table-column prop="archiveCode" label="档案编号" width="180" />
        <el-table-column prop="documentName" label="档案题名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="documentTypeCode" label="文档类型" width="150" />
        <el-table-column prop="companyProjectCode" label="公司/项目" width="140" />
        <el-table-column prop="businessCode" label="业务编号" width="160" />
        <el-table-column label="期间" width="170">
          <template #default="{ row }">{{ row.beginPeriod || '-' }} ~ {{ row.endPeriod || '-' }}</template>
        </el-table-column>
        <el-table-column prop="archiveStatus" label="状态" width="110" />
      </el-table>
    </el-card>

    <el-card shadow="never" class="panel">
      <template #header>
        <div class="section-head">
          <div>
            <strong>成册预览</strong>
            <span>确认分组后即可正式生成册。</span>
          </div>
          <div class="section-actions">
            <el-input v-model="bindRemark" placeholder="成册说明，可选" style="width: 240px" />
            <el-button type="success" :disabled="!previewResult.groups.length" @click="handleCreate" :loading="submitLoading">确认成册</el-button>
          </div>
        </div>
      </template>

      <el-empty v-if="!previewResult.groups.length" description="先执行预览分组" />
      <div v-else class="preview-list">
        <el-card v-for="(group, index) in previewResult.groups" :key="`${group.bindRuleKey}-${index}`" shadow="never" class="volume-card">
          <div class="volume-head">
            <div>
              <strong>{{ group.volumeTitle }}</strong>
              <div class="meta">规则：{{ group.bindRuleKey || '手工成册' }} | 载体：{{ group.carrierTypeCode }}</div>
            </div>
            <el-tag type="info" effect="plain">{{ group.archiveCount }} 件</el-tag>
          </div>
          <el-table :data="group.items" size="small" border>
            <el-table-column prop="sortNo" label="序号" width="70" />
            <el-table-column prop="archiveCode" label="档案编号" width="180" />
            <el-table-column prop="documentName" label="档案题名" min-width="220" show-overflow-tooltip />
            <el-table-column prop="primaryFlag" label="主件" width="90" />
          </el-table>
        </el-card>
      </div>
    </el-card>

    <el-card v-if="createdBatch" shadow="never" class="panel success-panel">
      <template #header>
        <div class="section-head">
          <div>
            <strong>成册完成</strong>
            <span>已生成批次 {{ createdBatch.bindBatchCode }}，可立即进入入库。</span>
          </div>
          <div class="section-actions">
            <el-button @click="goQuery">返回查询</el-button>
            <el-button type="primary" @click="goStorage">继续入库</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="成册方式">{{ createdBatch.bindMode }}</el-descriptions-item>
        <el-descriptions-item label="册数">{{ createdBatch.volumeCount }}</el-descriptions-item>
        <el-descriptions-item label="档案数">{{ createdBatch.archiveCount }}</el-descriptions-item>
        <el-descriptions-item label="下一步">{{ createdBatch.nextAction }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  createBindBatch,
  fetchArchiveCreateOptions,
  fetchBindOptions,
  previewBind
} from '../../api/modules/archiveManagement'
import type {
  ArchiveCreateOptions,
  BindArchiveCandidate,
  BindBatch,
  BindOptions,
  BindPreviewResult
} from '../../types'

const router = useRouter()
const bindMode = ref<'BUSINESS_CODE' | 'PERIOD' | 'MANUAL'>('BUSINESS_CODE')
const bindRemark = ref('')
const previewLoading = ref(false)
const submitLoading = ref(false)
const selectedRows = ref<BindArchiveCandidate[]>([])

const createOptions = ref<ArchiveCreateOptions>({
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
const bindOptions = ref<BindOptions>({ bindModes: [], candidates: [] })
const previewResult = ref<BindPreviewResult>({ bindMode: 'BUSINESS_CODE', groupCount: 0, archiveCount: 0, groups: [] })
const createdBatch = ref<BindBatch>()

const filters = reactive({
  keyword: '',
  documentTypeCode: '',
  companyProjectCode: ''
})

const filteredCandidates = computed(() => bindOptions.value.candidates.filter(item => {
  if (filters.keyword) {
    const keyword = filters.keyword.toLowerCase()
    const matched = [item.archiveCode, item.documentName, item.businessCode].some(value => String(value || '').toLowerCase().includes(keyword))
    if (!matched) return false
  }
  if (filters.documentTypeCode && item.documentTypeCode !== filters.documentTypeCode) return false
  if (filters.companyProjectCode && item.companyProjectCode !== filters.companyProjectCode) return false
  return true
}))

async function loadOptions() {
  const [createRes, bindRes] = await Promise.all([fetchArchiveCreateOptions(), fetchBindOptions()])
  createOptions.value = createRes
  bindOptions.value = bindRes
  if (bindRes.bindModes.length && !bindRes.bindModes.some(item => item.code === bindMode.value)) {
    bindMode.value = bindRes.bindModes[0].code as typeof bindMode.value
  }
}

function handleSelectionChange(rows: BindArchiveCandidate[]) {
  selectedRows.value = rows
}

async function handlePreview() {
  if (bindMode.value === 'MANUAL' && selectedRows.value.length === 0) {
    ElMessage.warning('人工成册请先勾选档案')
    return
  }
  previewLoading.value = true
  try {
    previewResult.value = await previewBind({
      bindMode: bindMode.value,
      keyword: filters.keyword || undefined,
      documentTypeCode: filters.documentTypeCode || undefined,
      companyProjectCode: filters.companyProjectCode || undefined,
      archiveIds: selectedRows.value.length ? selectedRows.value.map(item => item.archiveId) : undefined
    })
  } finally {
    previewLoading.value = false
  }
}

async function handleCreate() {
  submitLoading.value = true
  try {
    createdBatch.value = await createBindBatch({
      bindMode: bindMode.value,
      bindRemark: bindRemark.value || undefined,
      volumes: previewResult.value.groups.map(group => ({
        volumeTitle: group.volumeTitle,
        bindRuleKey: group.bindRuleKey,
        carrierTypeCode: group.carrierTypeCode,
        remark: group.remark,
        items: group.items.map(item => ({
          archiveId: item.archiveId,
          sortNo: item.sortNo,
          primaryFlag: item.primaryFlag,
          bindReason: item.bindReason
        }))
      }))
    })
    ElMessage.success('成册完成')
  } finally {
    submitLoading.value = false
  }
}

function goStorage() {
  if (!createdBatch.value) return
  router.push({
    path: '/archive-management/storage',
    query: { sourceBindBatchCode: createdBatch.value.bindBatchCode }
  })
}

function goQuery() {
  router.push('/archive-management/query')
}

onMounted(loadOptions)
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.section-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}
.section-head span {
  display: block;
  margin-top: 4px;
  color: #6b7280;
  font-size: 13px;
}
.section-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}
.toolbar,
.filter-grid,
.tips {
  margin-bottom: 16px;
}
.filter-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}
.panel {
  margin-top: 0;
}
.preview-list {
  display: grid;
  gap: 12px;
}
.volume-card {
  background: #fafafa;
}
.volume-head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 16px;
}
.meta {
  margin-top: 4px;
  color: #6b7280;
  font-size: 12px;
}
.success-panel {
  border-color: #b7eb8f;
}
</style>
