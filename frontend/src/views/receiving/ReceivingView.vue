<template>
  <div class="app-page">
    <div class="page-summary-grid">
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">接收单总数</div>
        <div class="summary-card__value">{{ receipts.length }}</div>
        <div class="summary-card__meta">覆盖待归档与审核中的归档材料</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">编目任务数</div>
        <div class="summary-card__value">{{ catalogTasks.length }}</div>
        <div class="summary-card__meta">归档接收后自动生成的后续工作项</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">处理中接收单</div>
        <div class="summary-card__value">{{ processingReceipts }}</div>
        <div class="summary-card__meta">待审核、待编目、待复核</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">近期待办</div>
        <div class="summary-card__value">{{ dueSoonTasks }}</div>
        <div class="summary-card__meta">编目截止日期已落入当前任务窗口</div>
      </el-card>
    </div>

    <el-card class="page-section-card" shadow="never">
      <template #header>
        <div class="page-toolbar">
          <div class="page-toolbar__left">
            <span>新增归档接收单</span>
          </div>
          <div class="page-toolbar__right">
            <el-tag type="primary" effect="plain">归档入口</el-tag>
          </div>
        </div>
      </template>

      <el-form :model="form" label-width="88px" class="page-form-grid">
        <el-form-item label="来源部门"><el-input v-model="form.sourceDept" /></el-form-item>
        <el-form-item label="档案题名"><el-input v-model="form.archiveTitle" /></el-form-item>
        <el-form-item label="档案类型"><el-input v-model="form.archiveType" /></el-form-item>
        <el-form-item label="密级"><el-input v-model="form.securityLevel" /></el-form-item>
        <el-form-item label="提交人"><el-input v-model="form.submittedBy" /></el-form-item>
        <el-form-item label="说明">
          <el-input value="提交后自动进入工作流引擎，生成接收单与编目任务。" disabled />
        </el-form-item>
      </el-form>

      <div class="form-actions">
        <el-button @click="resetForm">重置</el-button>
        <el-button type="primary" @click="submit">提交接收</el-button>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card class="page-section-card" shadow="never">
          <template #header>归档接收单</template>
          <el-table :data="receipts" stripe border>
            <el-table-column prop="receiptCode" label="接收单号" min-width="140" />
            <el-table-column prop="archiveTitle" label="题名" min-width="180" show-overflow-tooltip />
            <el-table-column prop="sourceDept" label="来源部门" width="140" />
            <el-table-column prop="archiveType" label="档案类型" width="140" />
            <el-table-column prop="securityLevel" label="密级" width="100" />
            <el-table-column prop="receiveStatus" label="状态" width="120">
              <template #default="{ row }">
                <el-tag type="warning" effect="light">{{ row.receiveStatus }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="page-section-card" shadow="never">
          <template #header>编目任务</template>
          <el-table :data="catalogTasks" stripe border>
            <el-table-column prop="taskCode" label="任务号" min-width="120" />
            <el-table-column prop="archiveCode" label="档号" min-width="140" />
            <el-table-column prop="archiveTitle" label="题名" min-width="180" show-overflow-tooltip />
            <el-table-column prop="taskStatus" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="row.taskStatus === 'PENDING' ? 'warning' : 'success'" effect="light">
                  {{ row.taskStatus }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="assignee" label="处理人" width="120" />
            <el-table-column prop="dueDate" label="截止日期" width="140" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { createArchiveReceipt, fetchArchiveReceipts, fetchCatalogTasks, type ReceiptCreateCommand } from '../../api/modules/archive'
import type { ArchiveReceipt, CatalogTask } from '../../types'

const form = reactive<ReceiptCreateCommand>({
  sourceDept: '综合办公室',
  archiveTitle: '',
  archiveType: '综合文书档案',
  securityLevel: 'INTERNAL',
  submittedBy: '系统管理员'
})

const receipts = ref<ArchiveReceipt[]>([])
const catalogTasks = ref<CatalogTask[]>([])
const processingReceipts = computed(() => receipts.value.filter(item => item.receiveStatus !== 'COMPLETED').length)
const dueSoonTasks = computed(() => catalogTasks.value.filter(item => item.taskStatus === 'PENDING').length)

const loadData = async () => {
  const [receiptData, taskData] = await Promise.all([fetchArchiveReceipts(), fetchCatalogTasks()])
  receipts.value = receiptData
  catalogTasks.value = taskData
}

const resetForm = () => {
  form.sourceDept = '综合办公室'
  form.archiveTitle = ''
  form.archiveType = '综合文书档案'
  form.securityLevel = 'INTERNAL'
  form.submittedBy = '系统管理员'
}

const submit = async () => {
  if (!form.archiveTitle.trim()) {
    ElMessage.warning('请填写档案题名')
    return
  }
  await createArchiveReceipt(form)
  ElMessage.success('归档接收单已创建')
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
