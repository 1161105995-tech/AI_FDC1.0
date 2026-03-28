<template>
  <div class="ai-search-page">
    <section class="search-top">
      <div class="search-top__ambient search-top__ambient--left" />
      <div class="search-top__ambient search-top__ambient--right" />

      <div class="search-top__hero">
        <div class="hero-badge">
          <span class="hero-badge__pulse" />
          <span>AI 搜索中心</span>
        </div>
        <div class="hero-stats">
          <div class="hero-stat">
            <strong>{{ currentModelName || '未配置模型' }}</strong>
            <span>当前问答模型</span>
          </div>
          <div class="hero-stat">
            <strong>{{ totalResults }}</strong>
            <span>本次命中文档</span>
          </div>
          <div class="hero-stat">
            <strong>{{ modeLabelMap[activeMode] }}</strong>
            <span>当前展示模式</span>
          </div>
        </div>
      </div>

      <div class="search-top__bar">
        <el-button text class="back-button" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>

        <div class="search-container">
          <div class="search-top__input search-top__textarea">
            <div class="search-top__icon">
              <el-icon><Search /></el-icon>
            </div>
            <el-input
              v-model="keyword"
              type="textarea"
              resize="none"
              maxlength="500"
              show-word-limit
              :autosize="{ minRows: 3, maxRows: 5 }"
              placeholder="请输入自然语言问题或关键词，例如：借阅档案超期怎么处理、查询 2024 年合同归档制度、总结一下移交材料要求"
              @keyup.enter.ctrl.prevent="submitSearch"
            />
            <div class="search-mode-integrated">
              <span class="meta-label">搜索模式</span>
              <div class="search-mode-toggle">
                <button 
                  v-for="mode in searchModes" 
                  :key="mode.value"
                  :class="['mode-button', { 'active': activeMode === mode.value }]"
                  @click="activeMode = mode.value; handleModeChange()"
                >
                  {{ mode.label }}
                </button>
              </div>
            </div>
            <div class="search-top__hint">按 `Ctrl + Enter` 发起搜索，也可以直接切换模式后再次检索。</div>
          </div>

          <div class="search-buttons">
            <el-button type="primary" size="large" @click="submitSearch">搜索</el-button>
            <el-button size="large" @click="clearKeyword">清空</el-button>
            <el-button size="large" @click="goTraditionalSearch">传统文档搜索</el-button>
          </div>
        </div>
      </div>

      <div class="search-top__meta">
        <div class="recognition-panel">
          <el-tag type="primary" effect="light">已识别：{{ modeLabelMap[activeMode] }}</el-tag>
          <el-tag type="success" effect="light">检索范围：{{ scopeDescription }}</el-tag>
          <el-tag type="warning" effect="light">检索策略：向量召回 + 文档检索 + AI 总结</el-tag>
          <el-tag effect="light">{{ resultSummaryLabel }}</el-tag>
          <el-tag v-if="currentModelName" effect="light" type="info">当前模型：{{ currentModelName }}</el-tag>
        </div>
      </div>
    </section>

    <div class="results-layout">
      <aside class="filters-pane">
        <el-card shadow="never" class="pane-card filters-card">
          <template #header>
            <div class="pane-head">
              <strong>筛选条件</strong>
              <el-button text @click="resetFilters">重置筛选</el-button>
            </div>
          </template>

          <div class="filters-form">
            <div class="filter-item">
              <label>公司/项目</label>
              <el-select v-model="filters.companyProjectCode" clearable filterable @change="applyFilters">
                <el-option v-for="item in options.companyProjects" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </div>

            <div class="filter-item">
              <label>文档类型</label>
              <CommonTreeSelect
                v-model="filters.documentTypeCode"
                :data="documentTypeTree"
                placeholder="请选择文档类型"
                label-key="typeName"
                value-key="typeCode"
                children-key="children"
                @update:model-value="applyFilters"
              />
            </div>

            <div class="filter-item">
              <label>文档组织</label>
              <el-select v-model="filters.documentOrganizationCode" clearable filterable @change="applyFilters">
                <el-option v-for="item in options.documentOrganizations" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </div>

            <div class="filter-item">
              <label>时间范围</label>
              <el-select v-model="timeRange" @change="handleTimeRangeChange">
                <el-option v-for="option in timeRangeOptions" :key="option.value" :label="option.label" :value="option.value" />
              </el-select>
            </div>

            <div v-if="timeRange === 'custom'" class="filter-item">
              <label>自定义范围</label>
              <el-date-picker
                v-model="customDateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="applyFilters"
              />
            </div>

            <div class="filter-item">
              <label>标签</label>
              <el-select v-model="selectedTags" multiple collapse-tags clearable @change="applyFilters">
                <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
              </el-select>
            </div>
          </div>
        </el-card>
      </aside>

      <main class="results-pane">
        <el-card shadow="never" class="pane-card summary-card">
          <div class="summary-row">
            <div class="summary-row__main">
              <strong>当前问题：{{ currentQuestionText }}</strong>
              <span>{{ totalResults }} 条结果，耗时约 {{ searchElapsed }} 秒，当前排序：{{ sortLabel }}</span>
            </div>
            <div class="summary-row__actions">
              <el-select v-model="sortMode" style="width: 150px" @change="sortResults">
                <el-option v-for="option in sortOptions" :key="option.value" :label="option.label" :value="option.value" />
              </el-select>
            </div>
          </div>

          <div v-if="activeFilterTags.length" class="active-filters">
            <el-tag v-for="tag in activeFilterTags" :key="tag" closable effect="plain" @close="removeFilterTag(tag)">
              {{ tag }}
            </el-tag>
          </div>
        </el-card>

        <el-card v-if="loading" shadow="never" class="pane-card loading-card">
          <el-skeleton animated :rows="8" />
        </el-card>

        <template v-else>
          <el-card shadow="never" class="pane-card answer-card">
            <template #header>
              <div class="pane-head">
                <div>
                  <strong>AI 回答</strong>
                  <span>先给结论，再给依据，再给原文定位。</span>
                </div>
                <el-tag :type="confidenceLevel.type" effect="light">{{ confidenceLevel.label }}</el-tag>
              </div>
            </template>

            <div class="tabs-head">
              <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                <el-tab-pane label="AI答案" name="answer" />
                <el-tab-pane label="相关文档" name="documents" />
              </el-tabs>
            </div>

            <div v-if="activeTab === 'answer'">

              <el-alert
                v-if="lowConfidence"
                type="warning"
                show-icon
                :closable="false"
                title="当前证据较少，建议结合右侧预览区的原文依据一起判断。"
                class="low-confidence-alert"
              />

              <div class="answer-header">
              <div class="answer-header__copy">
                <span class="answer-header__eyebrow">AI 总结</span>
                <h3>{{ answerHeading }}</h3>
                <p>{{ displayedAnswer }}</p>
              </div>
              <div class="answer-header__actions">
                <el-button @click="focusFirstReference">查看依据</el-button>
                <el-button @click="copyAnswer">复制答案</el-button>
                <el-button type="primary" @click="openSelectedDocument">打开相关文档</el-button>
              </div>
            </div>

            <div class="source-section">
              <div class="source-section__head">
                <strong>引用来源</strong>
                <span>本次答案基于 {{ references.length }} 份文档整理</span>
              </div>

              <div v-if="references.length" class="source-list">
                <button
                  v-for="reference in references"
                  :key="reference.archiveCode"
                  type="button"
                  class="source-item"
                  @click="selectDocument(reference)"
                >
                  <strong>{{ reference.documentName }}</strong>
                  <span>{{ reference.documentTypeName || reference.archiveTypeCode || '未分类' }} / {{ reference.companyProjectName || '未标注项目' }}</span>
                </button>
              </div>
              <el-empty v-else description="当前没有可直接引用的依据文档，建议从下方相关文档继续查看。" />
            </div>

            <div class="answer-grid">
              <div class="answer-block">
                <h4>关键要点</h4>
                <ul>
                  <li v-for="point in answerHighlights" :key="point">{{ point }}</li>
                </ul>
              </div>
              <div class="answer-block">
                <h4>适用范围</h4>
                <ul>
                  <li v-for="scope in applicableScopes" :key="scope">{{ scope }}</li>
                </ul>
              </div>
              <div class="answer-block">
                <h4>风险 / 注意事项</h4>
                <ul>
                  <li v-for="risk in answerRisks" :key="risk">{{ risk }}</li>
                </ul>
              </div>
            </div>
            </div>

            <div v-else-if="activeTab === 'documents'">
              <div v-if="displayedRecords.length" class="document-list">
                <article
                  v-for="record in displayedRecords"
                  :key="record.archiveCode"
                  class="document-card"
                  :class="{ 'is-active': selectedDocument?.archiveCode === record.archiveCode }"
                >
                  <div class="document-card__head">
                    <div class="document-card__title">
                      <button type="button" class="link-button" @click="selectDocument(record)">{{ record.documentName }}</button>
                      <div class="document-card__meta">
                        <el-tag size="small">{{ record.documentTypeName || record.archiveTypeCode || '未分类' }}</el-tag>
                        <el-tag size="small" type="success">{{ record.companyProjectName || '默认项目' }}</el-tag>
                        <el-tag size="small" :type="relevanceTag(record).type">{{ relevanceTag(record).label }}</el-tag>
                      </div>
                    </div>
                    <span class="document-card__date">{{ formatDocumentDate(record.lastUpdateDate || record.documentDate) }}</span>
                  </div>

                  <div class="document-card__body">
                    <p class="document-card__summary">{{ summarizeRecord(record) }}</p>
                    <div class="document-card__reason">
                      <strong>命中原因</strong>
                      <ul>
                        <li>{{ buildHitReason(record).title }}</li>
                        <li>{{ buildHitReason(record).body }}</li>
                      </ul>
                    </div>
                    <div class="document-card__status">
                      <span>档案状态：{{ record.archiveStatus || '已归档' }}</span>
                      <span>附件数：{{ record.attachmentCount }}</span>
                    </div>
                  </div>

                  <div class="document-card__actions">
                    <el-button @click="selectDocument(record)">查看预览</el-button>
                    <el-button @click="openDocument(record)">打开原文</el-button>
                    <el-button @click="focusDocumentEvidence(record)">查看依据</el-button>
                    <el-button @click="copyDocumentLink(record)">复制链接</el-button>
                    <el-button @click="toggleFavorite(record)">{{ favorites.has(record.archiveCode) ? '已收藏' : '收藏' }}</el-button>
                  </div>
                </article>
              </div>
              <el-empty v-else description="未找到相关文档，建议补充年份、项目名称或文档类型后重新搜索。" class="empty-state" />
            </div>
          </el-card>
        </template>
      </main>

      <aside class="preview-pane">
        <el-card shadow="never" class="pane-card preview-card">
          <template #header>
            <div class="pane-head">
              <div>
                <strong>右侧预览区</strong>
                <span>展示当前选中文档的命中高亮、摘要与可追问操作。</span>
              </div>
            </div>
          </template>

          <template v-if="selectedDocument">
            <div class="preview-header">
              <strong>{{ selectedDocument.documentName }}</strong>
              <div class="preview-header__meta">
                <el-tag size="small">{{ selectedDocument.documentTypeName || selectedDocument.archiveTypeCode || '未分类' }}</el-tag>
                <el-tag size="small" type="success">{{ selectedDocument.companyProjectName || '默认项目' }}</el-tag>
              </div>
            </div>

            <div class="preview-section">
              <span class="preview-section__label">命中段落高亮</span>
              <div class="preview-quote">
                <mark>{{ previewSnippet }}</mark>
              </div>
            </div>

            <div class="preview-section">
              <span class="preview-section__label">文档摘要</span>
              <p>{{ summarizeRecord(selectedDocument) }}</p>
            </div>

            <div class="preview-section">
              <span class="preview-section__label">目录导航</span>
              <ul class="toc-list">
                <li v-for="item in previewToc" :key="item">{{ item }}</li>
              </ul>
            </div>

            <div class="preview-section">
              <span class="preview-section__label">元信息</span>
              <div class="meta-grid">
                <span>档号：{{ selectedDocument.archiveFilingCode || selectedDocument.archiveCode }}</span>
                <span>责任人：{{ selectedDocument.dutyPerson || '未标注' }}</span>
                <span>更新时间：{{ formatDocumentDate(selectedDocument.lastUpdateDate || selectedDocument.documentDate) }}</span>
                <span>状态：{{ selectedDocument.archiveStatus || '已归档' }}</span>
              </div>
            </div>

            <div class="preview-actions">
              <el-button type="primary" @click="openDocument(selectedDocument)">打开全文</el-button>
              <el-button @click="downloadDocument(selectedDocument)">下载</el-button>
              <el-button @click="focusDocumentEvidence(selectedDocument)">定位原文段落</el-button>
              <el-button @click="showMetadata(selectedDocument)">查看元数据</el-button>
            </div>
          </template>
          <el-empty v-else description="请选择一条文档结果查看预览或依据。" />
        </el-card>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  ArrowLeft,
  Search
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CommonTreeSelect from '../../components/CommonTreeSelect.vue'
import {
  askArchiveQuestion,
  fetchArchiveAiModels,
  fetchArchiveCreateOptions,
  queryArchives,
  type ArchiveAskCommand,
  type ArchiveQueryCommand
} from '../../api/modules/archiveManagement'
import { fetchDocumentTypeTree } from '../../api/modules/documentType'
import type {
  ArchiveAiModelSummary,
  ArchiveAskResult,
  ArchiveCreateOptions,
  ArchiveQueryResult,
  ArchiveRecordSummary,
  DocumentTypeTreeNode,
  LabelValueOption
} from '../../types'

type SearchMode = 'qa' | 'document'
type SortMode = 'relevance' | 'latest' | 'usage' | 'official'
type TimeRange = 'all' | '7d' | '30d' | '1y' | 'custom'

const router = useRouter()
const route = useRoute()

const keyword = ref('')
const activeMode = ref<SearchMode>('qa')
const activeTab = ref<'answer' | 'documents'>('answer')
const sortMode = ref<SortMode>('relevance')
const timeRange = ref<TimeRange>('all')
const customDateRange = ref<[string, string] | []>([])
const selectedTags = ref<string[]>([])
const loading = ref(false)
const searchElapsed = ref('0.0')
const answerResult = ref<ArchiveAskResult | null>(null)
const queryResult = ref<ArchiveQueryResult>({ records: [], queryFields: [] })
const aiModels = ref<ArchiveAiModelSummary[]>([])
const documentTypeTree = ref<DocumentTypeTreeNode[]>([])
const selectedDocument = ref<ArchiveRecordSummary | null>(null)
const favorites = ref(new Set<string>())

const options = reactive<ArchiveCreateOptions>({
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

const filters = reactive({
  companyProjectCode: '',
  documentTypeCode: '',
  documentOrganizationCode: ''
})

const searchModes = [
  { label: '智能问答', value: 'qa' },
  { label: '文档搜索', value: 'document' }
]

const timeRangeOptions = [
  { label: '全部', value: 'all' },
  { label: '最近 7 天', value: '7d' },
  { label: '最近 30 天', value: '30d' },
  { label: '最近 1 年', value: '1y' },
  { label: '自定义范围', value: 'custom' }
]

const sortOptions = [
  { label: '综合相关度', value: 'relevance' },
  { label: '最新优先', value: 'latest' },
  { label: '最常使用', value: 'usage' },
  { label: '官方优先', value: 'official' }
]

const modeLabelMap: Record<SearchMode, string> = {
  qa: '智能问答',
  document: '文档搜索'
}

const sortLabelMap: Record<SortMode, string> = {
  relevance: '综合相关度',
  latest: '最新优先',
  usage: '最常使用',
  official: '官方优先'
}

const currentModelName = computed(() => {
  const chatModel = aiModels.value.find(item => item.modelType === 'CHAT')
  return chatModel?.modelName ?? aiModels.value[0]?.modelName ?? ''
})

const totalResults = computed(() => queryResult.value.records.length)
const currentQuestionText = computed(() => keyword.value.trim() || '请输入问题后搜索')
const sortLabel = computed(() => sortLabelMap[sortMode.value])
const references = computed(() => answerResult.value?.references ?? [])
const resultSummaryLabel = computed(() => totalResults.value ? `共命中：${totalResults.value} 条结果` : '当前未命中文档')
const lowConfidence = computed(() => references.value.length < 2)

const selectedCompanyName = computed(() => findOptionName(options.companyProjects, filters.companyProjectCode))
const selectedOrganizationName = computed(() => findOptionName(options.documentOrganizations, filters.documentOrganizationCode))
const selectedDocumentTypeName = computed(() => findDocumentTypeName(documentTypeTree.value, filters.documentTypeCode))

const scopeDescription = computed(() => {
  const scopes = ['全库档案、制度资料与操作手册']
  if (selectedCompanyName.value) scopes.push(`项目：${selectedCompanyName.value}`)
  if (selectedDocumentTypeName.value) scopes.push(`类型：${selectedDocumentTypeName.value}`)
  if (selectedOrganizationName.value) scopes.push(`组织：${selectedOrganizationName.value}`)
  return scopes.join(' / ')
})

const confidenceLevel = computed(() => {
  if (references.value.length >= 3) return { label: '可信度较高', type: 'success' as const }
  if (references.value.length === 2) return { label: '可信度中等', type: 'warning' as const }
  return { label: '建议结合原文', type: 'danger' as const }
})

const displayedRecords = computed(() => {
  const records = [...filteredRecords.value]
  if (sortMode.value === 'latest') {
    return records.sort((a, b) => new Date(b.lastUpdateDate || b.documentDate || '').getTime() - new Date(a.lastUpdateDate || a.documentDate || '').getTime())
  }
  if (sortMode.value === 'usage') {
    return records.sort((a, b) => (b.attachmentCount || 0) - (a.attachmentCount || 0))
  }
  if (sortMode.value === 'official') {
    return records.sort((a, b) => Number(b.archiveStatus === 'STORED') - Number(a.archiveStatus === 'STORED'))
  }
  return records.sort((a, b) => scoreRecord(b) - scoreRecord(a))
})

const answerHeading = computed(() => {
  if (!currentQuestionText.value.trim() || currentQuestionText.value === '请输入问题后搜索') return '请输入问题并发起 AI 搜索'
  if (references.value.length) return '根据命中文档整理出的回答'
  if (displayedRecords.value.length) return '已找到相关文档，但暂未形成高置信答案'
  return '暂未找到与你的问题直接相关的高质量结果'
})

const displayedAnswer = computed(() => {
  const answer = answerResult.value?.answer?.trim()
  if (answer) return answer.replace(/\n{3,}/g, '\n\n')
  if (references.value.length) {
    return `系统已结合 ${references.value.length} 份命中文档生成摘要。若需要确认条款细节、版本差异或执行边界，建议继续查看右侧原文依据。`
  }
  if (displayedRecords.value.length) {
    return '当前检索到了相关文档，但证据还不足以给出确定性结论。建议补充年份、项目名称、制度名称或文档类型后再次搜索。'
  }
  return '当前没有检索到足够相关的文档内容。可以尝试换一种问法，或先从下方相关文档列表继续排查。'
})

const answerHighlights = computed(() => {
  if (!displayedRecords.value.length) {
    return [
      '暂未命中足够文档，建议补充年份、项目、制度名称或文档类型。',
      '如果你是在找具体文件，可以切换到“文档搜索”模式再试。'
    ]
  }

  const sourceRecords = references.value.length ? references.value : displayedRecords.value.slice(0, 3)
  return sourceRecords.slice(0, 4).map(record => {
    const reason = buildHitReason(record)
    return `${reason.title}：${record.documentName}。${truncateText(summarizeRecord(record), 64)}`
  })
})

const applicableScopes = computed(() => {
  const scopes: string[] = []
  if (selectedDocumentTypeName.value) scopes.push(`当前优先覆盖文档类型：${selectedDocumentTypeName.value}`)
  if (selectedCompanyName.value) scopes.push(`当前限定公司/项目：${selectedCompanyName.value}`)
  if (selectedOrganizationName.value) scopes.push(`当前限定文档组织：${selectedOrganizationName.value}`)

  const typeNames = Array.from(new Set(displayedRecords.value.map(item => item.documentTypeName || item.archiveTypeCode).filter(Boolean))).slice(0, 3)
  if (!scopes.length && typeNames.length) scopes.push(`当前结果主要覆盖：${typeNames.join('、')}`)
  if (!scopes.length) scopes.push('当前问题会在全库档案、制度资料与操作手册中综合检索')
  scopes.push(activeMode.value === 'document' ? '当前更偏向返回具体文档结果' : '当前更偏向返回可解释的知识总结')
  return scopes
})

const answerRisks = computed(() => {
  if (!displayedRecords.value.length) {
    return [
      '当前没有足够命中文档支撑确定性结论。',
      '建议优先补充更具体的约束条件，再重新发起搜索。',
      '模型生成内容不能替代制度原文与正式审批要求。'
    ]
  }

  return [
    `当前回答基于 ${references.value.length || displayedRecords.value.length} 份命中文档整理，筛选条件变化后结果可能同步变化。`,
    lowConfidence.value ? '当前证据数量偏少，关键规则请优先查看右侧原文依据。' : '当前证据相对充分，但关键条款仍建议回看原文确认。',
    currentModelName.value ? `问答总结由 ${currentModelName.value} 结合检索结果生成，不替代正式制度文本。` : '模型配置不可见时，请优先以原文制度条款为准。'
  ]
})

const availableTags = computed(() => {
  const tagSet = new Set<string>()
  queryResult.value.records.forEach(record => {
    if (record.documentTypeName) tagSet.add(record.documentTypeName)
    if (record.archiveTypeCode) tagSet.add(record.archiveTypeCode)
    if (record.companyProjectName) tagSet.add(record.companyProjectName)
    if (record.archiveStatus) tagSet.add(record.archiveStatus)
  })
  return Array.from(tagSet).slice(0, 10)
})

const filteredRecords = computed(() => {
  let records = [...queryResult.value.records]

  if (selectedTags.value.length) {
    records = records.filter(record =>
      selectedTags.value.some(tag =>
        [record.documentTypeName, record.archiveTypeCode, record.companyProjectName, record.archiveStatus]
          .filter(Boolean)
          .includes(tag)
      )
    )
  }

  if (timeRange.value !== 'all') {
    const now = new Date()
    records = records.filter(record => {
      const raw = record.lastUpdateDate || record.documentDate
      if (!raw) return false
      const date = new Date(raw)
      if (Number.isNaN(date.getTime())) return false
      const diff = now.getTime() - date.getTime()
      const day = 24 * 60 * 60 * 1000
      if (timeRange.value === '7d') return diff <= 7 * day
      if (timeRange.value === '30d') return diff <= 30 * day
      if (timeRange.value === '1y') return diff <= 365 * day
      if (timeRange.value === 'custom' && customDateRange.value.length === 2) {
        const [start, end] = customDateRange.value
        return date >= new Date(start) && date <= new Date(`${end}T23:59:59`)
      }
      return true
    })
  }

  return records
})

const activeFilterTags = computed(() => {
  const tags: string[] = []
  if (selectedCompanyName.value) tags.push(`公司/项目：${selectedCompanyName.value}`)
  if (selectedDocumentTypeName.value) tags.push(`文档类型：${selectedDocumentTypeName.value}`)
  if (selectedOrganizationName.value) tags.push(`文档组织：${selectedOrganizationName.value}`)
  if (timeRange.value !== 'all') tags.push(`时间范围：${timeRangeOptions.find(item => item.value === timeRange.value)?.label}`)
  if (selectedTags.value.length) tags.push(...selectedTags.value.map(tag => `标签：${tag}`))
  return tags
})

const previewSnippet = computed(() => {
  if (!selectedDocument.value) return ''
  const reason = buildHitReason(selectedDocument.value)
  return `${reason.title}：${truncateText(summarizeRecord(selectedDocument.value), 180)}`
})

const previewToc = computed(() => {
  if (!selectedDocument.value) return []
  return ['1. 文档摘要', '2. 命中段落', '3. 相关元数据', '4. 扩展字段']
})

watch(displayedRecords, (records) => {
  if (!records.length) {
    selectedDocument.value = null
    return
  }
  if (!selectedDocument.value || !records.some(item => item.archiveCode === selectedDocument.value?.archiveCode)) {
    selectedDocument.value = references.value[0] ?? records[0]
  }
}, { immediate: true })

async function loadBaseData() {
  const [createOptions, tree, models] = await Promise.all([
    fetchArchiveCreateOptions(),
    fetchDocumentTypeTree(),
    fetchArchiveAiModels()
  ])
  Object.assign(options, createOptions)
  documentTypeTree.value = tree
  aiModels.value = models
}

function inferMode(text: string): SearchMode {
  if (/总结|解释|怎么|为何|区别|要求|流程|规定/.test(text)) return 'qa'
  return 'document'
}

async function runSearch() {
  const text = keyword.value.trim()
  if (!text) return

  loading.value = true
  const startedAt = performance.now()

  try {
    const queryCommand: ArchiveQueryCommand = {
      keyword: text,
      companyProjectCode: filters.companyProjectCode || undefined,
      documentTypeCode: filters.documentTypeCode || undefined,
      documentOrganizationCode: filters.documentOrganizationCode || undefined
    }
    const askCommand: ArchiveAskCommand = {
      question: text,
      companyProjectCode: filters.companyProjectCode || undefined,
      documentTypeCode: filters.documentTypeCode || undefined
    }
    const [queryResponse, askResponse] = await Promise.all([
      queryArchives(queryCommand),
      askArchiveQuestion(askCommand)
    ])
    queryResult.value = queryResponse
    answerResult.value = askResponse
    activeTab.value = activeMode.value === 'document' ? 'documents' : 'answer'
    searchElapsed.value = ((performance.now() - startedAt) / 1000).toFixed(1)
    if (!queryResponse.records.length) {
      ElMessage.info('未找到与你的问题直接相关的结果，建议换一种问法。')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

async function submitSearch() {
  const text = keyword.value.trim()
  if (!text) {
    ElMessage.warning('请输入搜索内容')
    return
  }
  if (text.length > 500) {
    ElMessage.warning('输入内容超出长度限制')
    return
  }
  await runSearch()
  router.replace({ path: '/archive-management/ai-search', query: { q: text, mode: activeMode.value } })
}

function clearKeyword() {
  keyword.value = ''
}

function handleModeChange() {
  activeTab.value = activeMode.value === 'document' ? 'documents' : 'answer'
  if (keyword.value.trim()) {
    submitSearch()
  }
}

function handleTabChange(tabName: string | number) {
  activeTab.value = tabName as 'answer' | 'documents'
}

function handleTimeRangeChange() {
  if (timeRange.value !== 'custom') {
    customDateRange.value = []
    applyFilters()
  }
}

function applyFilters() {
  sortResults()
}

function resetFilters() {
  filters.companyProjectCode = ''
  filters.documentTypeCode = ''
  filters.documentOrganizationCode = ''
  timeRange.value = 'all'
  customDateRange.value = []
  selectedTags.value = []
}

function removeFilterTag(tag: string) {
  if (tag.startsWith('公司/项目')) filters.companyProjectCode = ''
  if (tag.startsWith('文档类型')) filters.documentTypeCode = ''
  if (tag.startsWith('文档组织')) filters.documentOrganizationCode = ''
  if (tag.startsWith('时间范围')) {
    timeRange.value = 'all'
    customDateRange.value = []
  }
  if (tag.startsWith('标签：')) {
    const value = tag.replace('标签：', '')
    selectedTags.value = selectedTags.value.filter(item => item !== value)
  }
}

function sortResults() {
  selectedDocument.value = displayedRecords.value[0] ?? null
}

function goBack() {
  router.back()
}

function goTraditionalSearch() {
  router.push({ path: '/archive-management/query', query: { q: keyword.value.trim() } })
}

function selectDocument(record: ArchiveRecordSummary) {
  selectedDocument.value = record
}

function scoreRecord(record: ArchiveRecordSummary) {
  let score = 0
  const text = keyword.value.trim()
  if (record.documentName?.includes(text)) score += 4
  if (record.aiArchiveSummary?.includes(text)) score += 3
  if (record.documentTypeName?.includes(text)) score += 2
  score += Math.min(record.attachmentCount || 0, 3)
  return score
}

function relevanceTag(record: ArchiveRecordSummary) {
  const score = scoreRecord(record)
  if (score >= 6) return { label: '高相关', type: 'danger' as const }
  if (score >= 3) return { label: '中相关', type: 'warning' as const }
  return { label: '一般相关', type: 'info' as const }
}

function summarizeRecord(record: ArchiveRecordSummary) {
  return record.aiArchiveSummary?.trim()
    || `命中文档《${record.documentName}》，当前状态为 ${record.archiveStatus || '已归档'}，可进一步查看原文依据和扩展字段。`
}

function buildHitReason(record: ArchiveRecordSummary) {
  const text = keyword.value.trim()
  if (text && record.documentName?.includes(text)) {
    return { title: '命中标题', body: '问题关键词与文档标题高度匹配，优先返回该文档。' }
  }
  if (text && record.aiArchiveSummary?.includes(text)) {
    return { title: '命中摘要', body: 'AI 摘要中包含与问题语义高度相关的片段。' }
  }
  return { title: '语义相关', body: '系统判断该文档与当前问题语义接近，建议结合右侧预览继续核对。' }
}

function formatDocumentDate(value?: string) {
  if (!value) return '未更新'
  return value.replace('T', ' ').slice(0, 16)
}

function openDocument(record?: ArchiveRecordSummary | null) {
  const target = record ?? selectedDocument.value
  if (!target) return
  const url = `${window.location.origin}/archive-management/query?q=${encodeURIComponent(target.documentName)}&focus=${target.archiveCode}`
  window.open(url, '_blank')
}

function openSelectedDocument() {
  openDocument(selectedDocument.value)
}

function copyAnswer() {
  navigator.clipboard.writeText(displayedAnswer.value)
  ElMessage.success('答案已复制')
}

function focusSearchInput() {
  const input = document.querySelector('.search-top__textarea textarea, .search-top__textarea input') as HTMLTextAreaElement | HTMLInputElement | null
  input?.focus()
}

function focusFirstReference() {
  if (references.value[0]) {
    selectDocument(references.value[0])
    return
  }
  if (displayedRecords.value[0]) {
    selectDocument(displayedRecords.value[0])
  }
}

function focusDocumentEvidence(record: ArchiveRecordSummary) {
  selectDocument(record)
  ElMessage.success('已定位到右侧预览区的相关依据')
}

function copyDocumentLink(record: ArchiveRecordSummary) {
  const link = `${window.location.origin}/archive-management/ai-search?q=${encodeURIComponent(keyword.value.trim())}&focus=${record.archiveCode}`
  navigator.clipboard.writeText(link)
  ElMessage.success('链接已复制')
}

function toggleFavorite(record: ArchiveRecordSummary) {
  const next = new Set(favorites.value)
  if (next.has(record.archiveCode)) {
    next.delete(record.archiveCode)
    ElMessage.success('已取消收藏')
  } else {
    next.add(record.archiveCode)
    ElMessage.success('已加入收藏')
  }
  favorites.value = next
}



function downloadDocument(record: ArchiveRecordSummary) {
  ElMessage.success(`已开始准备下载：${record.documentName}`)
}



function showMetadata(record: ArchiveRecordSummary) {
  ElMessageBox.alert(
    [
      `档号：${record.archiveFilingCode || record.archiveCode}`,
      `文档类型：${record.documentTypeName || record.archiveTypeCode || '未分类'}`,
      `责任人：${record.dutyPerson || '未标注'}`,
      `来源系统：${record.sourceSystem || '未标注'}`,
      `文档组织：${record.documentOrganizationCode || '未标注'}`
    ].join('<br/>'),
    '文档元数据',
    { dangerouslyUseHTMLString: true, confirmButtonText: '知道了' }
  )
}



function findOptionName(optionsList: LabelValueOption[], code: string) {
  return optionsList.find(item => item.code === code)?.name ?? code
}

function findDocumentTypeName(nodes: DocumentTypeTreeNode[], code: string): string {
  if (!code) return ''
  for (const node of nodes) {
    if (node.typeCode === code) return node.typeName
    const childMatch = findDocumentTypeName(node.children || [], code)
    if (childMatch) return childMatch
  }
  return code
}

function truncateText(text: string, maxLength: number) {
  if (text.length <= maxLength) return text
  return `${text.slice(0, maxLength)}...`
}

onMounted(async () => {
  await loadBaseData()
  const q = typeof route.query.q === 'string' ? route.query.q : ''
  if (q) {
    keyword.value = q
    activeMode.value = (route.query.mode as SearchMode) || inferMode(q)
    activeTab.value = activeMode.value === 'document' ? 'documents' : 'answer'
    await runSearch()
  }
})
</script>

<style scoped>
.ai-search-page {
  display: grid;
  gap: 18px;
}

.search-top {
  position: sticky;
  top: 0;
  z-index: 12;
  display: grid;
  gap: 18px;
  padding: 22px;
  overflow: hidden;
  border: 1px solid rgba(157, 198, 255, 0.9);
  border-radius: 28px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(242, 249, 255, 0.98)),
    linear-gradient(120deg, rgba(84, 181, 255, 0.12), rgba(107, 240, 210, 0.12));
  box-shadow:
    0 24px 60px rgba(25, 87, 141, 0.12),
    inset 0 0 0 1px rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(18px);
}

.search-top__ambient {
  position: absolute;
  width: 280px;
  height: 280px;
  border-radius: 999px;
  pointer-events: none;
  filter: blur(10px);
  opacity: 0.5;
}

.search-top__ambient--left {
  top: -120px;
  left: -80px;
  background: radial-gradient(circle, rgba(72, 170, 255, 0.28), rgba(72, 170, 255, 0));
  animation: breathe 5.2s ease-in-out infinite;
}

.search-top__ambient--right {
  right: -60px;
  bottom: -140px;
  background: radial-gradient(circle, rgba(76, 226, 200, 0.28), rgba(76, 226, 200, 0));
  animation: breathe 6.4s ease-in-out infinite;
}

.search-top__hero,
.search-top__bar,
.search-top__meta,
.pane-head,
.summary-row,
.summary-row__actions,
.source-section__head,
.preview-header,
.preview-header__meta,
.document-card__head,
.document-card__meta,
.document-card__actions,
.tabs-head {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.search-container {
  flex: 1;
  display: grid;
  gap: 16px;
}

.search-mode-integrated {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(192, 220, 255, 0.5);
}

.search-mode-toggle {
  flex: 1;
  max-width: 400px;
  display: flex;
  background: rgba(255, 255, 255, 0.78);
  border-radius: 24px;
  padding: 6px;
  box-shadow: inset 0 0 0 1px rgba(192, 220, 255, 0.9);
  position: relative;
  overflow: hidden;
}

.mode-button {
  flex: 1;
  padding: 12px 24px;
  border: none;
  background: transparent;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  color: #6e8094;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.mode-button:hover {
  color: #2d78bc;
}

.mode-button.active {
  color: #ffffff;
  font-weight: 700;
}

.search-mode-toggle::before {
  content: '';
  position: absolute;
  top: 6px;
  left: 6px;
  width: calc(50% - 6px);
  height: calc(100% - 12px);
  background: linear-gradient(135deg, #4ba8ff, #63efd0);
  border-radius: 18px;
  transition: all 0.3s ease;
  z-index: 0;
}

.search-mode-toggle:has(.mode-button:nth-child(2).active)::before {
  transform: translateX(100%);
}

.search-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  align-items: center;
}

.search-buttons .el-button {
  min-width: 120px;
}

.search-buttons .el-button--primary {
  min-width: 140px;
}

.search-top__hero {
  align-items: flex-start;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border: 1px solid rgba(121, 171, 255, 0.42);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  color: #1e4468;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.06em;
}

.hero-badge__pulse {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4ba8ff, #63efd0);
  box-shadow: 0 0 0 0 rgba(74, 170, 255, 0.45);
  animation: pulse 2.4s ease-out infinite;
}

.hero-stats {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 12px;
}

.hero-stat {
  min-width: 140px;
  display: grid;
  gap: 4px;
  padding: 12px 14px;
  border: 1px solid rgba(178, 214, 255, 0.8);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.65);
}

.hero-stat strong {
  font-size: 16px;
  color: #173857;
}

.hero-stat span,
.summary-row__main span,
.pane-head span,
.document-card__date {
  color: #6e8094;
  font-size: 13px;
}

.search-top__input {
  flex: 1;
}

.search-top__textarea {
  position: relative;
  padding: 14px 16px 12px 54px;
  border: 1px solid rgba(133, 196, 255, 0.9);
  border-radius: 24px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 251, 255, 0.98)),
    linear-gradient(135deg, rgba(84, 181, 255, 0.08), rgba(107, 240, 210, 0.08));
  box-shadow:
    0 18px 36px rgba(61, 123, 182, 0.12),
    0 0 0 1px rgba(255, 255, 255, 0.92) inset;
}

.search-top__icon {
  position: absolute;
  top: 18px;
  left: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  color: #2d78bc;
  font-size: 18px;
}

.search-top__hint {
  margin-top: 10px;
  color: #6f8499;
  font-size: 12px;
}



.search-top :deep(.el-textarea__inner) {
  min-height: 120px !important;
  padding: 4px 0 0;
  border: 0;
  background: transparent;
  box-shadow: none;
  color: #173857;
  font-size: 16px;
  line-height: 1.8;
}

.search-top :deep(.el-textarea__inner:focus) {
  box-shadow: none;
}

.search-top :deep(.el-input__count) {
  background: transparent;
}

.search-top__meta {
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.mode-switch,
.filters-form,
.filter-item,
.answer-card,
.answer-grid,
.answer-block,
.source-section,
.source-list,
.follow-up-section,
.follow-up-tags,
.document-list,
.document-card,
.document-card__body,
.preview-card,
.preview-section,
.meta-grid,
.toc-list,
.active-filters {
  display: grid;
  gap: 12px;
}

.search-mode-integrated :deep(.el-segmented__item) {
  min-height: 42px;
  font-weight: 700;
}

.meta-label,
.filter-item label,
.follow-up-section__label,
.preview-section__label,
.answer-header__eyebrow {
  font-size: 12px;
  color: #688197;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.recognition-panel {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
}

.results-layout {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr) 360px;
  gap: 16px;
  align-items: start;
}

.filters-pane,
.preview-pane {
  position: sticky;
  top: 136px;
}

.pane-card {
  border: 1px solid #e3edf8;
  border-radius: 24px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(249, 252, 255, 0.98));
  box-shadow: 0 16px 34px rgba(37, 78, 116, 0.06);
}

.summary-card,
.answer-card,
.tabs-card {
  margin-bottom: 16px;
}

.summary-row__main {
  display: grid;
  gap: 4px;
}

.summary-row__main strong,
.answer-block h4,
.source-section__head strong,
.preview-header strong {
  margin: 0;
  color: #193754;
}

.answer-card {
  position: relative;
  overflow: hidden;
  border-color: rgba(159, 207, 255, 0.95);
}

.answer-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, rgba(83, 176, 255, 0.6), rgba(98, 236, 206, 0.6));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.answer-header,
.answer-header__copy {
  display: grid;
  gap: 16px;
}

.answer-header__copy h3 {
  margin: 0;
  font-size: 24px;
  color: #153553;
}

.answer-header__copy p,
.source-item span,
.document-card__summary,
.document-card__status,
.preview-section p,
.meta-grid span,
.toc-list li {
  margin: 0;
  color: #5e7388;
  line-height: 1.75;
  white-space: pre-wrap;
}

.answer-header__actions,
.preview-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.answer-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.answer-block {
  padding: 18px;
  border: 1px solid rgba(194, 220, 255, 0.72);
  border-radius: 22px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(242, 249, 255, 0.98));
}

.answer-block ul,
.document-card__reason ul,
.toc-list {
  margin: 0;
  padding-left: 18px;
  color: #536579;
  line-height: 1.75;
}

.low-confidence-alert {
  margin-bottom: 14px;
}

.source-list {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.source-item,
.document-card {
  padding: 14px;
  border: 1px solid #e5eef8;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.94);
  text-align: left;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.source-item:hover,
.document-card:hover {
  transform: translateY(-2px);
  border-color: rgba(113, 176, 255, 0.84);
  box-shadow: 0 18px 34px rgba(31, 77, 122, 0.08);
}

.source-item strong,
.link-button,
.preview-header strong {
  color: #193754;
}

.clickable-tag {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.clickable-tag:hover {
  transform: translateY(-1px);
}

.document-card.is-active {
  border-color: #6faeff;
  box-shadow: 0 0 0 1px rgba(111, 174, 255, 0.18), 0 18px 34px rgba(31, 77, 122, 0.08);
}

.document-card__title {
  display: grid;
  gap: 8px;
}

.link-button {
  padding: 0;
  border: 0;
  background: transparent;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  text-align: left;
}

.document-card__reason {
  display: grid;
  gap: 8px;
  padding: 12px;
  border-radius: 16px;
  background: linear-gradient(180deg, #f9fbff, #f3f8ff);
}

.document-card__reason strong {
  color: #29506c;
}

.document-card__status {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.preview-header {
  align-items: flex-start;
}

.preview-quote {
  padding: 16px;
  border-left: 4px solid #7caeff;
  border-radius: 16px;
  background: linear-gradient(180deg, #f8fbff, #f4f8ff);
  line-height: 1.8;
}

.preview-quote mark {
  background: rgba(255, 225, 131, 0.55);
  color: inherit;
}

.meta-grid {
  grid-template-columns: 1fr 1fr;
}

.empty-state {
  padding: 24px 0;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(74, 170, 255, 0.48);
  }
  70% {
    box-shadow: 0 0 0 12px rgba(74, 170, 255, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(74, 170, 255, 0);
  }
}

@keyframes breathe {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.45;
  }
  50% {
    transform: scale(1.08);
    opacity: 0.68;
  }
}

@media (max-width: 1440px) {
  .results-layout {
    grid-template-columns: 260px minmax(0, 1fr) 320px;
  }
}

@media (max-width: 1200px) {
  .results-layout {
    grid-template-columns: 1fr;
  }

  .filters-pane,
  .preview-pane {
    position: static;
  }

  .answer-grid,
  .source-list,
  .meta-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 860px) {
  .search-top__hero,
  .search-top__bar,
  .summary-row,
  .document-card__head,
  .document-card__actions,
  .preview-actions,
  .pane-head {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-stats,
  .recognition-panel {
    justify-content: flex-start;
  }

  .search-top__actions {
    grid-template-columns: 1fr;
  }
}
</style>
