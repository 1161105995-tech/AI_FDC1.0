<template>
  <div class="app-page">
    <div class="page-summary-grid">
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">借阅记录</div>
        <div class="summary-card__value">{{ records.length }}</div>
        <div class="summary-card__meta">实体与电子档案统一借阅管理</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">待审批</div>
        <div class="summary-card__value">{{ pendingApprovals }}</div>
        <div class="summary-card__meta">按档案类型、密级与人员身份审批</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">借出中</div>
        <div class="summary-card__value">{{ borrowingCount }}</div>
        <div class="summary-card__meta">跟踪借出与归还状态</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">即将到期</div>
        <div class="summary-card__value">{{ dueSoonCount }}</div>
        <div class="summary-card__meta">支撑超期提醒与催还</div>
      </el-card>
    </div>

    <el-card class="page-section-card" shadow="never">
      <template #header>新增借阅申请</template>
      <el-form :model="form" label-width="88px" class="page-form-grid">
        <el-form-item label="档号"><el-input v-model="form.archiveCode" /></el-form-item>
        <el-form-item label="档案题名"><el-input v-model="form.archiveTitle" /></el-form-item>
        <el-form-item label="借阅人"><el-input v-model="form.borrower" /></el-form-item>
        <el-form-item label="借阅类型">
          <el-select v-model="form.borrowType" style="width: 100%">
            <el-option label="实体借阅" value="PHYSICAL" />
            <el-option label="电子授权" value="DIGITAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="归还日期">
          <el-date-picker v-model="form.expectedReturnDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div class="form-actions">
        <el-button @click="resetForm">重置</el-button>
        <el-button type="primary" @click="submit">提交申请</el-button>
      </div>
    </el-card>

    <el-card class="page-section-card" shadow="never">
      <template #header>借阅记录</template>
      <el-table :data="records" stripe border>
        <el-table-column prop="borrowCode" label="借阅单号" min-width="140" />
        <el-table-column prop="archiveCode" label="档号" min-width="140" />
        <el-table-column prop="archiveTitle" label="题名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="borrower" label="借阅人" width="120" />
        <el-table-column prop="borrowType" label="借阅类型" width="120" />
        <el-table-column prop="approvalStatus" label="审批状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.approvalStatus === 'APPROVED' ? 'success' : 'warning'" effect="light">
              {{ row.approvalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="borrowStatus" label="借阅状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.borrowStatus === 'BORROWED' ? 'primary' : 'info'" effect="light">
              {{ row.borrowStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedReturnDate" label="预期归还" width="140" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { createBorrowRecord, fetchBorrowRecords, type BorrowCreateCommand } from '../../api/modules/lifecycle'
import type { BorrowRecord } from '../../types'

const form = reactive<BorrowCreateCommand>({ archiveCode: '', archiveTitle: '', borrower: '普通用户', borrowType: 'PHYSICAL', expectedReturnDate: '' })
const records = ref<BorrowRecord[]>([])
const pendingApprovals = computed(() => records.value.filter(item => item.approvalStatus !== 'APPROVED').length)
const borrowingCount = computed(() => records.value.filter(item => item.borrowStatus === 'BORROWED').length)
const dueSoonCount = computed(() => records.value.filter(item => item.expectedReturnDate).length)

const loadData = async () => {
  records.value = await fetchBorrowRecords()
}

const resetForm = () => {
  form.archiveCode = ''
  form.archiveTitle = ''
  form.borrower = '普通用户'
  form.borrowType = 'PHYSICAL'
  form.expectedReturnDate = ''
}

const submit = async () => {
  await createBorrowRecord(form)
  ElMessage.success('借阅申请已提交')
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
