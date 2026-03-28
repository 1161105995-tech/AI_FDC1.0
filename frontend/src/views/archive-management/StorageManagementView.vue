<template>
  <div class="page">
    <el-card shadow="never">
      <template #header>
        <div class="section-head">
          <div>
            <strong>入库上架</strong>
            <span>支持承接成册批次继续入库，也支持独立查询待入库册和档案。</span>
          </div>
          <div class="section-actions">
            <el-button @click="loadInitialData">刷新</el-button>
            <el-button type="primary" @click="handleSearch">查询待入库对象</el-button>
          </div>
        </div>
      </template>

      <el-alert
        v-if="queryForm.sourceBindBatchCode"
        type="success"
        :closable="false"
        show-icon
        :title="`当前承接成册批次：${queryForm.sourceBindBatchCode}`"
        class="tips"
      />

      <div class="filter-grid">
        <el-input v-model="queryForm.keyword" clearable placeholder="册号/档案号/题名" />
        <el-input v-model="queryForm.sourceBindBatchCode" clearable placeholder="来源成册批次，可选" />
        <el-select v-model="storageForm.warehouseCode" clearable filterable placeholder="目标库房">
          <el-option v-for="item in storageOptions.warehouses" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
        <el-select v-model="storageForm.locationCode" clearable filterable placeholder="目标库位">
          <el-option v-for="item in filteredLocations" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
        <el-input v-model="storageForm.remark" placeholder="入库说明，可选" />
      </div>
    </el-card>

    <el-card shadow="never">
      <template #header><strong>待入库册</strong></template>
      <el-table :data="queryResult.volumes" border @selection-change="handleVolumeSelection">
        <el-table-column type="selection" width="48" />
        <el-table-column prop="bindVolumeCode" label="册号" width="180" />
        <el-table-column prop="volumeTitle" label="册题名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="carrierTypeCode" label="载体" width="120" />
        <el-table-column prop="archiveCount" label="档案数" width="90" />
        <el-table-column prop="bindStatus" label="状态" width="110" />
      </el-table>
    </el-card>

    <el-card shadow="never">
      <template #header><strong>待入库单档</strong></template>
      <el-table :data="queryResult.archives" border @selection-change="handleArchiveSelection">
        <el-table-column type="selection" width="48" />
        <el-table-column prop="archiveCode" label="档案编号" width="180" />
        <el-table-column prop="documentName" label="档案题名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="documentTypeCode" label="文档类型" width="150" />
        <el-table-column prop="archiveStatus" label="状态" width="110" />
      </el-table>
      <div class="submit-bar">
        <el-button type="success" :loading="submitLoading" @click="handleSubmit">提交入库</el-button>
      </div>
    </el-card>

    <el-card v-if="latestBatch" shadow="never">
      <template #header><strong>最新入库结果</strong></template>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="批次号">{{ latestBatch.storageBatchCode }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ latestBatch.sourceType }}</el-descriptions-item>
        <el-descriptions-item label="库房">{{ latestBatch.warehouseCode }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ latestBatch.storageStatus }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="latestBatch.items" border class="result-table">
        <el-table-column prop="itemType" label="对象类型" width="110" />
        <el-table-column prop="bindVolumeCode" label="册号" width="180" />
        <el-table-column prop="archiveCode" label="档案号" width="180" />
        <el-table-column prop="locationCode" label="库位" width="180" />
        <el-table-column prop="resultStatus" label="结果" width="120" />
        <el-table-column prop="storedAt" label="入库时间" min-width="180" />
      </el-table>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="section-head">
          <div>
            <strong>入库流水</strong>
            <span>支持按批次号、册号、档案号、库房和库位检索。</span>
          </div>
          <div class="section-actions">
            <el-button @click="handleLedgerSearch">查询流水</el-button>
          </div>
        </div>
      </template>

      <div class="ledger-grid">
        <el-input v-model="ledgerQuery.storageBatchCode" clearable placeholder="入库批次号" />
        <el-input v-model="ledgerQuery.bindVolumeCode" clearable placeholder="册号" />
        <el-input v-model="ledgerQuery.archiveCode" clearable placeholder="档案号" />
        <el-select v-model="ledgerQuery.warehouseCode" clearable filterable placeholder="库房">
          <el-option v-for="item in storageOptions.warehouses" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
        <el-select v-model="ledgerQuery.locationCode" clearable filterable placeholder="库位">
          <el-option v-for="item in filteredLocations" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
        <el-select v-model="ledgerQuery.resultStatus" clearable placeholder="结果">
          <el-option label="SUCCESS" value="SUCCESS" />
          <el-option label="FAILED" value="FAILED" />
        </el-select>
      </div>

      <el-table :data="ledgerRows" border>
        <el-table-column prop="ledgerCode" label="流水号" width="180" />
        <el-table-column prop="storageBatchCode" label="入库批次" width="180" />
        <el-table-column prop="bindVolumeCode" label="册号" width="180" />
        <el-table-column prop="archiveCode" label="档案号" width="180" />
        <el-table-column prop="warehouseCode" label="库房" width="120" />
        <el-table-column prop="locationCode" label="库位" width="160" />
        <el-table-column prop="resultStatus" label="结果" width="100" />
        <el-table-column prop="operationTime" label="操作时间" min-width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import {
  createStorageBatch,
  fetchStorageOptions,
  queryStorage,
  queryStorageLedger
} from '../../api/modules/archiveManagement'
import type {
  BindArchiveCandidate,
  BindVolume,
  StorageBatch,
  StorageLedger,
  StorageOptions,
  StorageQueryResult
} from '../../types'

const route = useRoute()

const storageOptions = ref<StorageOptions>({ sourceTypes: [], warehouses: [], locations: [] })
const queryResult = ref<StorageQueryResult>({ volumes: [], archives: [] })
const latestBatch = ref<StorageBatch>()
const ledgerRows = ref<StorageLedger[]>([])
const selectedVolumes = ref<BindVolume[]>([])
const selectedArchives = ref<BindArchiveCandidate[]>([])
const submitLoading = ref(false)

const queryForm = reactive({
  sourceBindBatchCode: '',
  keyword: ''
})

const storageForm = reactive({
  warehouseCode: '',
  locationCode: '',
  remark: ''
})

const ledgerQuery = reactive({
  storageBatchCode: '',
  bindVolumeCode: '',
  archiveCode: '',
  warehouseCode: '',
  locationCode: '',
  resultStatus: ''
})

const filteredLocations = computed(() => {
  if (!storageForm.warehouseCode && !ledgerQuery.warehouseCode) return storageOptions.value.locations
  const warehouseCode = storageForm.warehouseCode || ledgerQuery.warehouseCode
  return storageOptions.value.locations.filter(item => item.code.startsWith(`${warehouseCode}-`) || item.code.startsWith(warehouseCode))
})

async function loadInitialData() {
  storageOptions.value = await fetchStorageOptions()
  queryForm.sourceBindBatchCode = String(route.query.sourceBindBatchCode || queryForm.sourceBindBatchCode || '')
  await handleSearch()
  await handleLedgerSearch()
}

async function handleSearch() {
  queryResult.value = await queryStorage({
    sourceBindBatchCode: queryForm.sourceBindBatchCode || undefined,
    keyword: queryForm.keyword || undefined
  })
}

function handleVolumeSelection(rows: BindVolume[]) {
  selectedVolumes.value = rows
}

function handleArchiveSelection(rows: BindArchiveCandidate[]) {
  selectedArchives.value = rows
}

async function handleSubmit() {
  if (!storageForm.warehouseCode || !storageForm.locationCode) {
    ElMessage.warning('请选择目标库房和库位')
    return
  }
  if (selectedVolumes.value.length === 0 && selectedArchives.value.length === 0) {
    ElMessage.warning('请至少选择一个册或档案')
    return
  }
  submitLoading.value = true
  try {
    latestBatch.value = await createStorageBatch({
      sourceType: queryForm.sourceBindBatchCode ? 'BIND_GUIDED' : 'DIRECT',
      sourceBindBatchCode: queryForm.sourceBindBatchCode || undefined,
      warehouseCode: storageForm.warehouseCode,
      remark: storageForm.remark || undefined,
      items: [
        ...selectedVolumes.value.map(item => ({
          itemType: 'VOLUME' as const,
          volumeId: item.volumeId,
          locationCode: storageForm.locationCode
        })),
        ...selectedArchives.value.map(item => ({
          itemType: 'ARCHIVE' as const,
          archiveId: item.archiveId,
          locationCode: storageForm.locationCode
        }))
      ]
    })
    ledgerQuery.storageBatchCode = latestBatch.value.storageBatchCode
    await Promise.all([handleSearch(), handleLedgerSearch()])
    ElMessage.success('入库完成')
  } finally {
    submitLoading.value = false
  }
}

async function handleLedgerSearch() {
  ledgerRows.value = await queryStorageLedger({
    storageBatchCode: ledgerQuery.storageBatchCode || undefined,
    bindVolumeCode: ledgerQuery.bindVolumeCode || undefined,
    archiveCode: ledgerQuery.archiveCode || undefined,
    warehouseCode: ledgerQuery.warehouseCode || undefined,
    locationCode: ledgerQuery.locationCode || undefined,
    resultStatus: ledgerQuery.resultStatus || undefined
  })
}

onMounted(loadInitialData)
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
.filter-grid,
.ledger-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}
.tips {
  margin-bottom: 16px;
}
.submit-bar {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.result-table {
  margin-top: 16px;
}
</style>
