<template>
  <div class="detail-page">
    <el-card shadow="never" class="flow-card">
      <div class="flow-track">
        <div
          v-for="(step, index) in archiveSteps"
          :key="step.code"
          class="flow-step"
          :class="{ 'is-active': step.code === currentStep, 'is-future': index > currentStepIndex }"
        >
          <div class="flow-step__dot">{{ index + 1 }}</div>
          <div class="flow-step__body">
            <div class="flow-step__title">{{ step.label }}</div>
            <div class="flow-step__meta">{{ step.meta }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="section-head">
          <div>
            <strong>档案详情</strong>
            <span>查看档案的详细信息，内容与发起归档界面一致</span>
          </div>
          <div class="section-actions">
            <el-tag type="info" effect="plain">状态：{{ archiveDetail?.archiveStatus || '-' }}</el-tag>
            <el-button @click="goBack">返回列表</el-button>
          </div>
        </div>
      </template>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="20" animated />
      </div>

      <div v-else-if="archiveDetail" class="archive-form">
        <el-card shadow="never" class="sub-card">
          <template #header><div class="sub-card__title">文档基本信息</div></template>
          <div class="form-grid form-grid--4">
            <el-form-item label="文档类型" class="span-2">
              <el-input :model-value="archiveDetail.documentTypeName" disabled />
            </el-form-item>
            <el-form-item label="公司/项目">
              <el-input :model-value="archiveDetail.companyProjectName" disabled />
            </el-form-item>
            <el-form-item label="载体类型">
              <el-input :model-value="archiveDetail.carrierTypeCode" disabled />
            </el-form-item>

            <el-form-item label="文档名称" class="span-2">
              <el-input :model-value="archiveDetail.documentName" disabled />
            </el-form-item>
            <el-form-item label="文档业务编码">
              <el-input :model-value="archiveDetail.businessCode || '-'" disabled />
            </el-form-item>
            <el-form-item label="档案类型">
              <el-input :model-value="archiveDetail.archiveTypeCode" disabled />
            </el-form-item>

            <el-form-item label="开始档期">
              <el-input :model-value="archiveDetail.beginPeriod" disabled />
            </el-form-item>
            <el-form-item label="结束档期">
              <el-input :model-value="archiveDetail.endPeriod" disabled />
            </el-form-item>
            <el-form-item label="文档生成日期">
              <el-input :model-value="archiveDetail.documentDate" disabled />
            </el-form-item>
            <el-form-item label="保存期限">
              <div class="inline-unit">
                <el-input :model-value="archiveDetail.retentionPeriodYears" disabled style="width: 100%" />
                <el-tag effect="plain" type="info">年</el-tag>
              </div>
            </el-form-item>

            <el-form-item label="归档责任人">
              <el-input :model-value="archiveDetail.dutyPerson" disabled />
            </el-form-item>
            <el-form-item label="归档责任部门">
              <el-input :model-value="archiveDetail.dutyDepartment" disabled />
            </el-form-item>
            <el-form-item label="密级">
              <el-input :model-value="archiveDetail.securityLevelCode" disabled />
            </el-form-item>
            <el-form-item label="文档组织">
              <el-input :model-value="archiveDetail.documentOrganizationCode" disabled />
            </el-form-item>

            <el-form-item label="归档地">
              <el-input :model-value="archiveDetail.archiveDestination || '-'" disabled />
            </el-form-item>
            <el-form-item label="系统来源">
              <el-input :model-value="archiveDetail.sourceSystem || '-'" disabled />
            </el-form-item>
            <el-form-item label="产生地">
              <el-input :model-value="archiveDetail.originPlace || '-'" disabled />
            </el-form-item>
            <el-form-item label="文档归档编码">
              <el-input :model-value="archiveDetail.archiveFilingCode" disabled />
            </el-form-item>
          </div>

          <el-form-item label="备注">
            <el-input :model-value="archiveDetail.remark || '-'" type="textarea" :rows="2" disabled />
          </el-form-item>
          <el-form-item label="AI档案摘要">
            <el-input :model-value="archiveDetail.aiArchiveSummary || '-'" type="textarea" :rows="3" disabled />
          </el-form-item>
        </el-card>

        <el-card v-if="Object.keys(archiveDetail.extValues).length > 0" shadow="never" class="sub-card">
          <template #header><div class="sub-card__title">文档扩展信息</div></template>
          <div class="form-grid form-grid--4">
            <el-form-item
              v-for="(value, key) in archiveDetail.extValues"
              :key="key"
              :label="key"
              :class="{ 'span-2': key.length > 8 }"
            >
              <el-input :model-value="value" disabled />
            </el-form-item>
          </div>
        </el-card>

        <el-card v-if="showElectronicSection" shadow="never" class="sub-card">
          <template #header>
            <div class="sub-card__header">
              <div>
                <div class="sub-card__title">电子件信息</div>
                <div class="sub-card__subtitle">每个附件都可单独维护附件类型、备注和 AI 摘要。</div>
              </div>
            </div>
          </template>
          <el-table :data="electronicAttachments" border class="attachment-table" empty-text="暂无电子附件">
            <el-table-column prop="fileName" label="文件名称" min-width="240" show-overflow-tooltip />
            <el-table-column prop="attachmentTypeCode" label="附件类型" width="180" />
            <el-table-column prop="remark" label="本次备注" min-width="220" />
            <el-table-column prop="aiSummary" label="AI摘要" min-width="340" />
            <el-table-column prop="parseStatus" label="解析状态" width="110" />
            <el-table-column prop="vectorStatus" label="向量状态" width="110" />
            <el-table-column prop="versionNo" label="版本" width="80" />
          </el-table>
        </el-card>

        <el-card v-if="showPaperSection" shadow="never" class="sub-card">
          <template #header>
            <div class="sub-card__header">
              <div>
                <div class="sub-card__title">纸质件信息</div>
                <div class="sub-card__subtitle">扫描件同样支持单附件维护，纸质件数量单独记录。</div>
              </div>
            </div>
          </template>
          <div class="form-grid form-grid--4 compact-grid">
            <el-form-item label="计划归档份数">
              <el-input :model-value="archiveDetail.paperInfo?.plannedCopyCount || '-'" disabled />
            </el-form-item>
            <el-form-item label="实际归档份数">
              <el-input :model-value="archiveDetail.paperInfo?.actualCopyCount || '-'" disabled />
            </el-form-item>
            <el-form-item label="纸质件说明" class="span-2">
              <el-input :model-value="archiveDetail.paperInfo?.remark || '-'" disabled />
            </el-form-item>
          </div>
          <el-table :data="paperAttachments" border class="attachment-table" empty-text="暂无扫描件">
            <el-table-column prop="fileName" label="扫描件名称" min-width="240" show-overflow-tooltip />
            <el-table-column prop="attachmentTypeCode" label="附件类型" width="180" />
            <el-table-column prop="remark" label="本次备注" min-width="220" />
            <el-table-column prop="aiSummary" label="AI摘要" min-width="340" />
            <el-table-column prop="parseStatus" label="解析状态" width="110" />
            <el-table-column prop="vectorStatus" label="向量状态" width="110" />
            <el-table-column prop="versionNo" label="版本" width="80" />
          </el-table>
        </el-card>
      </div>

      <div v-else class="error-container">
        <el-empty description="未找到档案信息" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArchiveDetail } from '../../api/modules/archiveManagement'
import type { ArchiveRecordSummary } from '../../types'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const archiveDetail = ref<ArchiveRecordSummary | null>(null)

const archiveSteps = [
  { code: 'CREATED', label: '创建', meta: '办理人：当前用户 · 时间：待保存' },
  { code: 'TRANSFERRED', label: '移交/核销', meta: '办理人：待流转 · 时间：待处理' },
  { code: 'BOUND', label: '成册', meta: '办理人：待流转 · 时间：待处理' },
  { code: 'STORED', label: '入库', meta: '办理人：待流转 · 时间：待处理' },
  { code: 'ARCHIVED', label: '已归档', meta: '归档完成 · 时间：待完成' }
]

const currentStep = ref('CREATED')
const currentStepIndex = computed(() => Math.max(archiveSteps.findIndex(item => item.code === currentStep.value), 0))

const electronicAttachments = computed(() => archiveDetail.value?.attachments?.filter(item => item.attachmentRole === 'ELECTRONIC') ?? [])
const paperAttachments = computed(() => archiveDetail.value?.attachments?.filter(item => item.attachmentRole === 'PAPER_SCAN') ?? [])
const showElectronicSection = computed(() => archiveDetail.value && ['ELECTRONIC', 'HYBRID'].includes(archiveDetail.value.carrierTypeCode))
const showPaperSection = computed(() => archiveDetail.value && ['PAPER', 'HYBRID'].includes(archiveDetail.value.carrierTypeCode))

const loadArchiveDetail = async () => {
  const archiveId = Number(route.params.id)
  if (isNaN(archiveId)) {
    loading.value = false
    return
  }
  
  try {
    loading.value = true
    const result = await getArchiveDetail(archiveId)
    archiveDetail.value = result
    // 更新当前步骤
    if (result.archiveStatus) {
      currentStep.value = result.archiveStatus
    }
  } catch (error) {
    console.error('Failed to load archive detail:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/archive-management/query')
}

onMounted(() => {
  loadArchiveDetail()
})
</script>

<style scoped>
.detail-page { display: grid; gap: 16px; }
.flow-track { position: relative; display: grid; grid-template-columns: repeat(5, minmax(0, 1fr)); gap: 18px; }
.flow-track::before { content: ''; position: absolute; left: 48px; right: 48px; top: 16px; height: 2px; background: #dfe7f2; }
.flow-step { position: relative; display: flex; align-items: flex-start; gap: 12px; z-index: 1; padding-right: 8px; background: linear-gradient(180deg, #fff 0, #fff 88%, transparent 100%); }
.flow-step__dot { width: 32px; height: 32px; border-radius: 999px; background: #d9ecff; color: #409eff; display: grid; place-items: center; font-weight: 700; flex: 0 0 auto; }
.flow-step__body { min-width: 0; padding-top: 2px; }
.flow-step__title { font-weight: 600; color: #24324a; line-height: 1.4; }
.flow-step__meta { margin-top: 6px; font-size: 12px; line-height: 1.55; color: #8a94a6; }
.flow-step.is-active .flow-step__dot { background: #409eff; color: #fff; box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.15); }
.flow-step.is-future .flow-step__dot { background: #f0f2f5; color: #a0a8b5; }
.section-head { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; }
.section-head strong { display: block; font-size: 16px; color: #24324a; }
.section-head span { color: #7a879a; font-size: 12px; }
.section-actions { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.loading-container { padding: 20px 0; }
.error-container { padding: 40px 0; text-align: center; }
.archive-form { display: grid; gap: 16px; }
.sub-card { border: 1px solid #edf2f7; }
.sub-card__title { font-weight: 600; color: #24324a; }
.sub-card__subtitle { margin-top: 4px; font-size: 12px; color: #7a879a; }
.sub-card__header { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.form-grid { display: grid; gap: 12px 16px; }
.form-grid--4 { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.compact-grid { margin-bottom: 12px; }
.span-2 { grid-column: span 2; }
.inline-unit { display: flex; align-items: center; gap: 8px; width: 100%; }
.attachment-table :deep(.el-textarea__inner) { min-height: 60px !important; }
.attachment-table :deep(.el-input__wrapper) { min-height: 34px; }
@media (max-width: 1280px) {
  .flow-track, .form-grid--4 { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .span-2 { grid-column: span 2; }
}
@media (max-width: 960px) {
  .flow-track, .form-grid--4 { grid-template-columns: 1fr; }
  .span-2 { grid-column: span 1; }
  .flow-track::before { display: none; }
  .section-head, .sub-card__header { flex-direction: column; }
  .section-actions { width: 100%; justify-content: flex-start; }
}
</style>