<template>
  <div class="dashboard-page">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-skeleton-item variant="rect" style="height: 320px; border-radius: 28px" />
      </template>

      <template #default>
        <el-card shadow="never" class="hero-card">
          <div class="hero-grid">
            <div class="hero-main">
              <div class="hero-brand">
                <div class="hero-brand__logo">
                  <div class="hero-brand__logo-ring">
                    <el-icon><Opportunity /></el-icon>
                  </div>
                  <div class="hero-brand__logo-text">
                    <strong>AI</strong>
                    <span>智能助手入口</span>
                  </div>
                </div>
                <span class="hero-kicker">AI 智能操作入口区</span>
              </div>

              <div class="hero-input-shell">
                <div class="hero-input-shell__label">AI 智能检索 / AI 自然语言发起</div>
                <el-input
                  v-model="aiPrompt"
                  type="textarea"
                  :rows="7"
                  resize="none"
                  placeholder="请输入问题或直接发起业务，例如：帮我查询 2024 年合同档案、发起一批项目资料归档、我要借阅某项目验收文档"
                  @keydown.enter.exact.prevent="submitAiPrompt"
                />
              </div>

              <div class="hero-actions">
                <el-space wrap>
                  <el-tag
                    v-for="sample in promptSamples"
                    :key="sample"
                    round
                    effect="plain"
                    class="clickable"
                    @click="aiPrompt = sample"
                  >
                    {{ sample }}
                  </el-tag>
                </el-space>
                <el-button type="primary" size="large" class="hero-submit" @click="submitAiPrompt">
                  AI 发起
                </el-button>
              </div>
            </div>

            <div class="hero-side">
              <div class="hero-stat">
                <span>今日待处理</span>
                <strong>{{ summary?.workspaceStats.pendingCount ?? 0 }}</strong>
                <p>
                  已逾期 {{ summary?.workspaceStats.overdueCount ?? 0 }} 项，高优先级
                  {{ summary?.workspaceStats.highPriorityCount ?? 0 }} 项。
                </p>
              </div>

              <div class="hero-stat hero-stat--light">
                <span>首屏核心入口</span>
                <strong>4 个已就绪</strong>
                <p>发起归档、发起移交、查询文档、借阅文档均可直接跳转。</p>
              </div>
            </div>
          </div>
        </el-card>

        <div class="quick-grid">
          <button
            v-for="action in quickActions"
            :key="action.title"
            type="button"
            class="quick-card"
            @click="goTo(action.route)"
          >
            <div class="quick-card__icon">
              <el-icon><component :is="action.icon" /></el-icon>
            </div>
            <div>
              <strong>{{ action.title }}</strong>
              <p>{{ action.subtitle }}</p>
            </div>
          </button>
        </div>

        <div class="workspace-grid">
          <el-card id="workspace-card" shadow="never" class="board-card">
            <template #header>
              <div class="section-head">
                <div>
                  <strong>工作空间</strong>
                  <span>顶部统计 + 标签页 + 列表，默认展示“我的待办”。</span>
                </div>
              </div>
            </template>

            <div class="stats-grid">
              <div v-for="stat in workspaceOverview" :key="stat.label" class="stat-box">
                <span>{{ stat.label }}</span>
                <strong>{{ stat.value }}</strong>
                <small :class="stat.className">{{ stat.hint }}</small>
              </div>
            </div>

            <el-tabs v-model="activeWorkspaceTab">
              <el-tab-pane
                v-for="tab in workspaceTabs"
                :key="tab.key"
                :label="tab.label"
                :name="tab.key"
              />
            </el-tabs>

            <el-table :data="activeTasks" border empty-text="暂无待处理任务">
              <el-table-column prop="title" label="任务标题" min-width="220">
                <template #default="{ row }">
                  <el-button link type="primary" @click="goTo(row.route)">{{ row.title }}</el-button>
                </template>
              </el-table-column>
              <el-table-column prop="businessType" label="业务类型" width="110" />
              <el-table-column prop="currentStep" label="当前环节" min-width="120" />
              <el-table-column prop="initiator" label="发起人" width="100" />
              <el-table-column prop="deadline" label="截止时间" width="170" />
              <el-table-column prop="priority" label="紧急程度" width="100">
                <template #default="{ row }">
                  <el-tag :type="priorityTagType(row.priority)" effect="light">{{ row.priority }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="statusTagType(row.status)" effect="light">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-space>
                    <el-button link type="primary" @click="goTo(row.route)">处理</el-button>
                    <el-button link @click="goTo(row.route)">详情</el-button>
                  </el-space>
                </template>
              </el-table-column>
              <template #empty>
                <el-empty description="暂无待处理任务">
                  <el-space>
                    <el-button type="primary" @click="goTo('/archive-management/create')">去发起归档</el-button>
                    <el-button @click="goTo('/archive-management/query')">查询文档</el-button>
                  </el-space>
                </el-empty>
              </template>
            </el-table>
          </el-card>

          <div class="side-column">
            <el-card shadow="never" class="board-card">
              <template #header>
                <div class="section-head">
                  <div>
                    <strong>预警与通知中心</strong>
                    <span>未读优先展示，高风险消息突出提醒。</span>
                  </div>
                </div>
              </template>

              <div v-if="summary?.notifications?.length" class="stack-list">
                <button
                  v-for="notice in summary.notifications"
                  :key="notice.id"
                  type="button"
                  class="list-card"
                  @click="goTo(notice.route)"
                >
                  <div class="list-card__top">
                    <el-tag :type="notice.tagType" size="small">{{ notice.category }}</el-tag>
                    <span>{{ notice.time }}</span>
                  </div>
                  <strong>{{ notice.title }}</strong>
                  <p>{{ notice.summary }}</p>
                </button>
              </div>
              <el-empty v-else description="暂无消息" />
            </el-card>

            <el-card shadow="never" class="board-card">
              <template #header>
                <div class="section-head">
                  <div>
                    <strong>最近访问 / 最近处理</strong>
                    <span>默认按最近时间倒序展示最近 5 条。</span>
                  </div>
                </div>
              </template>

              <div v-if="summary?.recentActivities?.length" class="stack-list">
                <button
                  v-for="item in summary.recentActivities"
                  :key="item.id"
                  type="button"
                  class="list-card"
                  @click="goTo(item.route)"
                >
                  <div class="list-card__top">
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.time }}</span>
                  </div>
                  <p>{{ item.type }}</p>
                </button>
              </div>
              <el-empty v-else description="你最近还没有访问记录">
                <el-space>
                  <el-button type="primary" @click="goTo('/archive-management/query')">去查询文档</el-button>
                  <el-button @click="goTo('/archive-management/create')">发起归档</el-button>
                </el-space>
              </el-empty>
            </el-card>
          </div>
        </div>

        <div class="metric-grid">
          <el-card
            v-for="metric in summary?.metrics ?? []"
            :key="metric.code"
            shadow="hover"
            class="metric-card"
            @click="goTo(metricRouteMap[metric.code] ?? '/dashboard')"
          >
            <span>{{ metric.label }}</span>
            <strong>{{ metric.value }}</strong>
            <small>{{ metric.trend }}</small>
          </el-card>
        </div>

        <div class="panel-grid">
          <el-card shadow="never" class="board-card">
            <template #header>
              <div class="section-head">
                <div>
                  <strong>归档量趋势</strong>
                  <span>默认展示近 30 天。</span>
                </div>
                <el-segmented v-model="archiveTrendRange" :options="trendRanges" />
              </div>
            </template>
            <div class="chart-bars">
              <div v-for="item in archiveTrendData" :key="item.label" class="bar-item">
                <span>{{ item.value }}</span>
                <div class="bar-track">
                  <div class="bar-fill" :style="{ height: `${item.height}%` }" />
                </div>
                <small>{{ item.label }}</small>
              </div>
            </div>
          </el-card>

          <el-card shadow="never" class="board-card">
            <template #header>
              <div class="section-head">
                <div>
                  <strong>借阅趋势</strong>
                  <span>展示借阅申请与归还变化。</span>
                </div>
                <el-segmented v-model="borrowTrendRange" :options="trendRanges" />
              </div>
            </template>
            <div class="chart-bars chart-bars--line">
              <div v-for="item in borrowTrendData" :key="item.label" class="bar-item">
                <span>{{ item.value }}</span>
                <div class="bar-track">
                  <div class="bar-fill bar-fill--green" :style="{ height: `${item.height}%` }" />
                </div>
                <small>{{ item.label }}</small>
              </div>
            </div>
          </el-card>
        </div>

        <div class="panel-grid panel-grid--3">
          <el-card shadow="never" class="board-card">
            <template #header>
              <div class="section-head">
                <div>
                  <strong>结构分布</strong>
                  <span>V1 实现档案类型分布与载体分布。</span>
                </div>
              </div>
            </template>
            <div class="progress-list">
              <div v-for="item in summary?.distributions ?? []" :key="item.label">
                <div class="progress-head">
                  <span>{{ item.label }}</span>
                  <strong>{{ item.value }}%</strong>
                </div>
                <el-progress :percentage="item.value" :stroke-width="10" :color="item.color" />
              </div>
            </div>
          </el-card>

          <el-card shadow="never" class="board-card">
            <template #header>
              <div class="section-head">
                <div>
                  <strong>风险指标区</strong>
                  <span>单模块加载失败不影响其他区域展示。</span>
                </div>
                <el-button text @click="reloadSummary">重试</el-button>
              </div>
            </template>
            <el-alert
              v-if="errorMessage"
              :title="errorMessage"
              type="error"
              :closable="false"
              show-icon
              style="margin-bottom: 12px"
            />
            <div class="risk-grid">
              <div v-for="item in summary?.riskIndicators ?? []" :key="item.label" class="risk-item">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }}</strong>
              </div>
            </div>
          </el-card>

          <el-card shadow="never" class="board-card">
            <template #header>
              <div class="section-head">
                <div>
                  <strong>我的常用功能</strong>
                  <span>以图标宫格形式展示 8 个默认功能。</span>
                </div>
              </div>
            </template>
            <div class="tool-grid">
              <button
                v-for="tool in commonTools"
                :key="tool.title"
                type="button"
                class="tool-card"
                :disabled="tool.disabled"
                @click="tool.disabled ? notifyDisabled() : goTo(tool.route)"
              >
                <el-icon><component :is="tool.icon" /></el-icon>
                <strong>{{ tool.title }}</strong>
                <span>{{ tool.disabled ? '暂未开放' : tool.desc }}</span>
              </button>
            </div>
          </el-card>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
import {
  CollectionTag,
  Files,
  FolderAdd,
  FolderChecked,
  Notebook,
  Opportunity,
  Search,
  Switch,
  Tickets
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchDashboardSummary } from '../../api/modules/dashboard'
import type { DashboardSummary, DashboardTaskItem, DashboardTrendPoint } from '../../types'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const errorMessage = ref('')
const summary = ref<DashboardSummary | null>(null)
const aiPrompt = ref('')
const activeWorkspaceTab = ref('todo')
const archiveTrendRange = ref('近30天')
const borrowTrendRange = ref('近30天')

const promptSamples = ['帮我查询 2024 年合同档案', '发起一批项目资料归档', '我要借阅某项目验收文档']
const trendRanges = ['近7天', '近30天', '近90天']

const metricRouteMap: Record<string, string> = {
  archive_total: '/archive-management/query',
  month_new: '/archive-management/create',
  transfer_pending: '/archive-management/transfer',
  storage_pending: '/archive-management/storage',
  borrowing: '/archive-management/borrow',
  overdue_return: '/archive-management/borrow'
}

const quickActions = [
  { title: '发起归档', subtitle: '进入归档发起页面', route: '/archive-management/create', icon: FolderAdd },
  { title: '发起移交', subtitle: '进入移交流程发起页面', route: '/archive-management/transfer', icon: Switch },
  { title: '查询文档', subtitle: '进入文档查询页面', route: '/archive-management/query', icon: Search },
  { title: '借阅文档', subtitle: '进入借阅申请与审批中心', route: '/archive-management/borrow', icon: Notebook }
]

const commonTools = [
  { title: '发起归档', desc: '快速创建', route: '/archive-management/create', disabled: false, icon: FolderAdd },
  { title: '发起移交', desc: '流程发起', route: '/archive-management/transfer', disabled: false, icon: Switch },
  { title: '档案查询', desc: '全局检索', route: '/archive-management/query', disabled: false, icon: Search },
  { title: '借阅申请', desc: '申请借阅', route: '/archive-management/borrow', disabled: false, icon: Notebook },
  { title: '归还登记', desc: '借阅归还', route: '/archive-management/borrow', disabled: false, icon: FolderChecked },
  { title: '批量导入', desc: '暂未开放', route: '/archive-management/create', disabled: true, icon: Files },
  { title: '标签管理', desc: '暂未开放', route: '/base-data/dictionaries', disabled: true, icon: CollectionTag },
  { title: '入库上架', desc: '实体入库', route: '/archive-management/storage', disabled: false, icon: Tickets }
]

const workspaceTabs = computed(() => summary.value?.workspaceBuckets ?? [])
const activeTasks = computed<DashboardTaskItem[]>(() => {
  return summary.value?.workspaceBuckets.find(item => item.key === activeWorkspaceTab.value)?.tasks ?? []
})

const workspaceOverview = computed(() => [
  { label: '待处理数量', value: summary.value?.workspaceStats.pendingCount ?? 0, hint: '默认看我的待办', className: '' },
  { label: '今日到期数量', value: summary.value?.workspaceStats.dueTodayCount ?? 0, hint: '建议优先处理', className: 'warning' },
  { label: '已逾期数量', value: summary.value?.workspaceStats.overdueCount ?? 0, hint: '需要尽快闭环', className: 'danger' },
  { label: '高优先级数量', value: summary.value?.workspaceStats.highPriorityCount ?? 0, hint: '风险项已突出', className: 'primary' }
])

const archiveTrendData = computed(() => normalizeTrend(summary.value?.archiveTrends, archiveTrendRange.value))
const borrowTrendData = computed(() => normalizeTrend(summary.value?.borrowTrends, borrowTrendRange.value))

function normalizeTrend(groups: DashboardSummary['archiveTrends'] | undefined, range: string) {
  const points = groups?.find(item => item.range === range)?.points ?? []
  const max = Math.max(1, ...points.map(item => item.value))
  return points.map((item: DashboardTrendPoint) => ({
    ...item,
    height: Math.max(20, Math.round((item.value / max) * 100))
  }))
}

async function loadSummary() {
  errorMessage.value = ''
  try {
    summary.value = await fetchDashboardSummary()
    if (summary.value.workspaceBuckets.length > 0 && !summary.value.workspaceBuckets.some(item => item.key === activeWorkspaceTab.value)) {
      activeWorkspaceTab.value = summary.value.workspaceBuckets[0].key
    }
  } catch (error) {
    console.error(error)
    errorMessage.value = '加载失败，请重试'
  } finally {
    loading.value = false
  }
}

function goTo(path: string) {
  router.push(path)
}

function notifyDisabled() {
  ElMessage.info('暂未开放')
}

function priorityTagType(value: string) {
  if (value === '高优') return 'danger'
  if (value === '紧急') return 'warning'
  return 'info'
}

function statusTagType(value: string) {
  if (value === '已完成') return 'success'
  if (value === '已超期') return 'danger'
  if (value === '处理中') return 'warning'
  return 'primary'
}

async function submitAiPrompt() {
  const text = aiPrompt.value.trim()
  if (!text) {
    ElMessage.warning('请输入内容')
    return
  }

  try {
    if (/归档|建档/.test(text)) {
      await router.push({ path: '/archive-management/create', query: { prompt: text } })
      ElMessage.success('已识别为发起归档')
      return
    }
    if (/移交|交接/.test(text)) {
      await router.push({ path: '/archive-management/transfer', query: { prompt: text } })
      ElMessage.success('已识别为发起移交')
      return
    }
    if (/借阅|归还|借出/.test(text)) {
      await router.push({ path: '/archive-management/borrow', query: { prompt: text } })
      ElMessage.success('已识别为借阅文档')
      return
    }
    if (/查|查询|检索|档案|文档/.test(text)) {
      await router.push({ path: '/archive-management/ai-search', query: { q: text, mode: 'qa' } })
      ElMessage.success('已识别为查询类问题')
      return
    }
    ElMessage.warning('暂未识别你的意图，请尝试更明确的描述')
  } catch (error) {
    console.error(error)
    ElMessage.error('智能服务暂不可用，可通过快捷入口继续操作')
  }
}

async function reloadSummary() {
  loading.value = true
  await loadSummary()
}

async function focusWorkspace() {
  if (route.query.focus !== 'workspace') return
  await nextTick()
  document.getElementById('workspace-card')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

watch(() => route.query.focus, focusWorkspace)

onMounted(async () => {
  await loadSummary()
  await focusWorkspace()
})
</script>

<style scoped>
.dashboard-page { display: grid; gap: 20px; }
.hero-card, .board-card { border: 1px solid #e6edf5; border-radius: 24px; }
.hero-card {
  overflow: hidden;
  position: relative;
  border: 1px solid rgba(110, 169, 255, 0.22);
  background:
    radial-gradient(circle at 0% 0%, rgba(255, 211, 126, 0.22), transparent 28%),
    radial-gradient(circle at 100% 0%, rgba(87, 198, 255, 0.2), transparent 30%),
    linear-gradient(135deg, #f7fbff 0%, #eef7ff 45%, #f6fffd 100%);
  color: #16324a;
  box-shadow: 0 20px 50px rgba(71, 120, 173, 0.12);
}
.hero-card::before {
  content: '';
  position: absolute;
  inset: 0;
  padding: 1px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(86, 156, 255, 0.7), rgba(77, 224, 196, 0.65), rgba(255, 194, 102, 0.6));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}
.hero-card::after {
  content: '';
  position: absolute;
  width: 320px;
  height: 320px;
  right: -60px;
  top: -90px;
  background: radial-gradient(circle, rgba(86, 156, 255, 0.2) 0%, rgba(86, 156, 255, 0) 68%);
  filter: blur(6px);
  animation: heroPulse 4.6s ease-in-out infinite;
  pointer-events: none;
}
.hero-grid, .workspace-grid, .panel-grid, .panel-grid--3, .quick-grid, .metric-grid, .stats-grid, .tool-grid, .risk-grid { display: grid; gap: 16px; }
.hero-grid { grid-template-columns: 1.55fr 0.8fr; align-items: stretch; }
.hero-main, .hero-side, .stack-list { display: grid; gap: 18px; }
.hero-brand,
.hero-actions,
.section-head,
.list-card__top,
.progress-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}
.hero-brand { align-items: flex-start; }
.hero-brand__logo {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  border: 1px solid rgba(90, 151, 223, 0.16);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 16px 36px rgba(87, 121, 168, 0.12);
  backdrop-filter: blur(12px);
}
.hero-brand__logo-ring {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #7bb5ff, #58dbc6 72%, #ffd28d);
  color: #0f3552;
  font-size: 28px;
  font-weight: 700;
  box-shadow: 0 0 0 8px rgba(119, 173, 255, 0.14);
}
.hero-brand__logo-text {
  display: grid;
  gap: 4px;
}
.hero-brand__logo-text strong {
  font-size: 24px;
  line-height: 1;
  color: #1a3a58;
}
.hero-brand__logo-text span {
  font-size: 12px;
  color: #6c8298;
}
.hero-kicker {
  padding: 9px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  letter-spacing: 0.14em;
  color: #577089;
  border: 1px solid rgba(90, 151, 223, 0.16);
}
.hero-main p, .hero-stat p { margin: 0; line-height: 1.7; color: #5b7388; }
.hero-input-shell {
  position: relative;
  padding: 22px;
  border: 1px solid rgba(132, 181, 255, 0.24);
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(245, 251, 255, 0.9));
  box-shadow:
    0 18px 42px rgba(78, 129, 184, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  animation: shellPulse 4.8s ease-in-out infinite;
}
.hero-input-shell::before {
  content: '';
  position: absolute;
  inset: 0;
  padding: 1px;
  border-radius: 28px;
  background: linear-gradient(135deg, rgba(108, 164, 255, 0.65), rgba(83, 222, 201, 0.5), rgba(255, 199, 112, 0.45));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}
.hero-input-shell__label {
  margin-bottom: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #2d617f;
  letter-spacing: 0.04em;
}
.hero-input-shell :deep(.el-textarea__inner) {
  min-height: 220px;
  padding: 22px 22px;
  border: 0;
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff, #f8fcff);
  color: #18344d;
  box-shadow: 0 0 0 1px rgba(132, 181, 255, 0.18) inset;
  font-size: 16px;
  line-height: 1.75;
}
.hero-input-shell :deep(.el-textarea__inner::placeholder) {
  color: #87a0b5;
}
.clickable {
  cursor: pointer;
  border-color: rgba(100, 156, 233, 0.2);
  color: #335a78;
  background: rgba(255, 255, 255, 0.78);
}
.hero-submit {
  min-width: 140px;
  height: 50px;
  border: 0;
  border-radius: 18px;
  background: linear-gradient(135deg, #6ca4ff, #50dcc9 74%, #ffd28d);
  color: #12324d;
  font-weight: 700;
  box-shadow: 0 16px 28px rgba(108, 164, 255, 0.24);
}
.hero-stat {
  padding: 22px;
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255,255,255,0.84), rgba(247,252,255,0.72));
  border: 1px solid rgba(132, 181, 255, 0.16);
  box-shadow: 0 16px 36px rgba(87, 121, 168, 0.1);
}
.hero-stat--light { background: linear-gradient(180deg, rgba(233, 249, 245, 0.92), rgba(255, 255, 255, 0.78)); }
.hero-stat span { font-size: 13px; color: #6f8698; }
.hero-stat strong { font-size: 32px; color: #153650; }
.quick-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.quick-card, .list-card, .tool-card {
  padding: 18px;
  border: 1px solid #e6edf5;
  border-radius: 20px;
  background: #fff;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}
.quick-card:hover, .list-card:hover, .tool-card:hover, .metric-card:hover { transform: translateY(-2px); box-shadow: 0 14px 32px rgba(19, 41, 64, 0.08); }
.quick-card__icon {
  width: 52px;
  height: 52px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #e8f4ff, #d9f5ef);
  color: #14576d;
  font-size: 20px;
  margin-bottom: 14px;
}
.quick-card strong, .section-head strong, .list-card strong, .tool-card strong { color: #213547; }
.quick-card p, .section-head span, .list-card p, .tool-card span { margin: 0; color: #708090; line-height: 1.5; }
.workspace-grid { grid-template-columns: 1.5fr 0.8fr; }
.side-column { display: grid; gap: 16px; }
.stats-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); margin-bottom: 16px; }
.stat-box { padding: 16px; border-radius: 18px; background: #f7fbff; display: grid; gap: 8px; }
.stat-box strong { font-size: 28px; color: #213547; }
.stat-box span, .stat-box small, .metric-card span, .metric-card small { color: #738395; }
.danger { color: #dc2626; }
.warning { color: #d97706; }
.primary { color: #2563eb; }
.metric-grid { grid-template-columns: repeat(6, minmax(0, 1fr)); }
.metric-card { padding: 18px; border-radius: 20px; cursor: pointer; }
.metric-card strong { display: block; margin: 10px 0 8px; font-size: 30px; color: #213547; }
.panel-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
.panel-grid--3 { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.chart-bars { min-height: 240px; display: grid; grid-auto-flow: column; grid-auto-columns: 1fr; align-items: end; gap: 14px; }
.bar-item { display: grid; gap: 8px; justify-items: center; }
.bar-track { width: 100%; height: 180px; display: flex; align-items: end; padding: 6px; border-radius: 16px; background: #eef4fa; }
.bar-fill { width: 100%; border-radius: 12px; background: linear-gradient(180deg, #6ea8ff, #2563eb); }
.bar-fill--green { background: linear-gradient(180deg, #34d399, #0f766e); }
.progress-list { display: grid; gap: 14px; }
.risk-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
.risk-item { padding: 16px; border-radius: 18px; background: #fff7f5; display: grid; gap: 10px; }
.risk-item span { color: #9d6a5a; }
.risk-item strong { font-size: 26px; color: #c2410c; }
.tool-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.tool-card { display: grid; gap: 10px; }
.tool-card:disabled { opacity: 0.72; cursor: not-allowed; }
.tool-card .el-icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: #eef5fb;
  color: #2d5d88;
  font-size: 18px;
}
@media (max-width: 1400px) {
  .metric-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
  .tool-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
@media (max-width: 1200px) {
  .hero-grid, .workspace-grid, .panel-grid, .panel-grid--3, .quick-grid, .stats-grid { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 900px) {
  .hero-grid, .workspace-grid, .panel-grid, .panel-grid--3, .quick-grid, .metric-grid, .stats-grid, .tool-grid, .risk-grid { grid-template-columns: 1fr; }
  .hero-brand, .hero-actions, .section-head, .list-card__top, .progress-head { flex-direction: column; align-items: flex-start; }
  .hero-input-shell :deep(.el-textarea__inner) { min-height: 180px; }
}
@keyframes heroPulse {
  0%, 100% { opacity: 0.55; transform: scale(1); }
  50% { opacity: 0.95; transform: scale(1.08); }
}
@keyframes shellPulse {
  0%, 100% { box-shadow: 0 18px 42px rgba(78, 129, 184, 0.12), inset 0 1px 0 rgba(255, 255, 255, 0.9); }
  50% { box-shadow: 0 22px 50px rgba(92, 169, 255, 0.18), 0 0 0 6px rgba(95, 189, 246, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.94); }
}
</style>
