<template>
  <div class="app-page">
    <div class="page-summary-grid">
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">销毁记录</div>
        <div class="summary-card__value">{{ records.length }}</div>
        <div class="summary-card__meta">覆盖鉴定、审批、执行与留痕</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">待审批</div>
        <div class="summary-card__value">{{ pendingApprovals }}</div>
        <div class="summary-card__meta">纳入制度化审批和风险提示</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">执行中</div>
        <div class="summary-card__value">{{ processingRecords }}</div>
        <div class="summary-card__meta">待生成清册、执行登记和审计证据</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">保管期限类型</div>
        <div class="summary-card__value">{{ retentionKinds }}</div>
        <div class="summary-card__meta">支撑到期识别与批量鉴定</div>
      </el-card>
    </div>

    <el-card class="page-section-card" shadow="never">
      <template #header>新增销毁鉴定</template>
      <el-form :model="form" label-width="88px" class="page-form-grid">
        <el-form-item label="档号"><el-input v-model="form.archiveCode" /></el-form-item>
        <el-form-item label="档案题名"><el-input v-model="form.archiveTitle" /></el-form-item>
        <el-form-item label="保管期限"><el-input v-model="form.retentionPeriod" /></el-form-item>
        <el-form-item label="鉴定意见">
          <el-input v-model="form.appraisalConclusion" />
        </el-form-item>
      </el-form>
      <div class="form-actions">
        <el-button @click="resetForm">重置</el-button>
        <el-button type="primary" @click="submit">发起鉴定</el-button>
      </div>
    </el-card>

    <el-card class="page-section-card" shadow="never">
      <template #header>销毁记录</template>
      <el-table :data="records" stripe border>
        <el-table-column prop="disposalCode" label="销毁单号" min-width="140" />
        <el-table-column prop="archiveCode" label="档号" min-width="140" />
        <el-table-column prop="archiveTitle" label="题名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="retentionPeriod" label="保管期限" width="120" />
        <el-table-column prop="approvalStatus" label="审批状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.approvalStatus === 'APPROVED' ? 'success' : 'warning'" effect="light">
              {{ row.approvalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="disposalStatus" label="销毁状态" width="140">
          <template #default="{ row }">
            <el-tag :type="row.disposalStatus === 'COMPLETED' ? 'success' : 'info'" effect="light">
              {{ row.disposalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { createDisposalRecord, fetchDisposalRecords, type DisposalCreateCommand } from '../../api/modules/lifecycle'
import type { DisposalRecord } from '../../types'

const form = reactive<DisposalCreateCommand>({ archiveCode: '', archiveTitle: '', retentionPeriod: '10年', appraisalConclusion: '达到销毁条件，建议销毁' })
const records = ref<DisposalRecord[]>([])
const pendingApprovals = computed(() => records.value.filter(item => item.approvalStatus !== 'APPROVED').length)
const processingRecords = computed(() => records.value.filter(item => item.disposalStatus !== 'COMPLETED').length)
const retentionKinds = computed(() => new Set(records.value.map(item => item.retentionPeriod)).size)

const loadData = async () => {
  records.value = await fetchDisposalRecords()
}

const resetForm = () => {
  form.archiveCode = ''
  form.archiveTitle = ''
  form.retentionPeriod = '10年'
  form.appraisalConclusion = '达到销毁条件，建议销毁'
}

const submit = async () => {
  await createDisposalRecord(form)
  ElMessage.success('销毁鉴定已发起')
  resetForm()
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
