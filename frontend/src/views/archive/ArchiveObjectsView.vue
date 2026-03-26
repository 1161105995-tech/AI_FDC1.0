<template>
  <div class="app-page">
    <div class="page-summary-grid">
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">档案对象总量</div>
        <div class="summary-card__value">{{ rows.length }}</div>
        <div class="summary-card__meta">统一档案对象模型当前结果</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">已完成 AI 分类</div>
        <div class="summary-card__value">{{ aiClassifiedCount }}</div>
        <div class="summary-card__meta">自动分类结果已入库</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">已抽取元数据</div>
        <div class="summary-card__value">{{ aiMetadataCount }}</div>
        <div class="summary-card__meta">支撑编目、检索与质检</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">已分配库位</div>
        <div class="summary-card__value">{{ locationAssignedCount }}</div>
        <div class="summary-card__meta">纸电一体化位置统一管理</div>
      </el-card>
    </div>

    <el-card class="page-section-card" shadow="never">
      <template #header>
        <div class="page-toolbar">
          <div class="page-toolbar__left">
            <span>档案对象检索</span>
          </div>
          <div class="page-toolbar__right">
            <el-button @click="resetQuery">重置</el-button>
            <el-button type="primary" @click="loadData">查询</el-button>
          </div>
        </div>
      </template>

      <el-form :model="query" label-width="88px" class="page-form-grid">
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" placeholder="档号 / 题名 / 责任者" clearable />
        </el-form-item>
        <el-form-item label="档案类型">
          <el-input v-model="query.archiveType" placeholder="例如：合同档案" clearable />
        </el-form-item>
        <el-form-item label="密级">
          <el-input v-model="query.securityLevel" placeholder="例如：SECRET" clearable />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="page-section-card" shadow="never">
      <template #header>
        <div class="page-toolbar">
          <div class="page-toolbar__left">
            <span>统一档案对象列表</span>
            <el-tag type="info" effect="plain">共 {{ rows.length }} 条</el-tag>
          </div>
        </div>
      </template>

      <el-table :data="rows" stripe border>
        <el-table-column prop="archiveCode" label="档号" min-width="150" />
        <el-table-column prop="title" label="题名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="archiveType" label="类型" width="140" />
        <el-table-column prop="securityLevel" label="密级" width="110">
          <template #default="{ row }">
            <el-tag :type="row.securityLevel === 'SECRET' ? 'danger' : 'info'" effect="light">
              {{ row.securityLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="organizationName" label="归档主体" width="140" />
        <el-table-column prop="currentWorkflowStage" label="流程阶段" width="140" />
        <el-table-column prop="currentLocationCode" label="库位" min-width="160">
          <template #default="{ row }">
            <span v-if="row.currentLocationCode">{{ row.currentLocationCode }}</span>
            <el-tag v-else type="warning" effect="plain">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="AI 状态" width="180">
          <template #default="{ row }">
            <el-space>
              <el-tag :type="row.aiClassified ? 'success' : 'info'" effect="light">分类</el-tag>
              <el-tag :type="row.aiMetadataExtracted ? 'success' : 'warning'" effect="light">抽取</el-tag>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { fetchArchiveObjects, type ArchiveQuery } from '../../api/modules/archive'
import type { ArchiveObject } from '../../types'

const query = reactive<ArchiveQuery>({})
const rows = ref<ArchiveObject[]>([])
const aiClassifiedCount = computed(() => rows.value.filter(item => item.aiClassified).length)
const aiMetadataCount = computed(() => rows.value.filter(item => item.aiMetadataExtracted).length)
const locationAssignedCount = computed(() => rows.value.filter(item => item.currentLocationCode).length)

const loadData = async () => {
  rows.value = await fetchArchiveObjects(query)
}

const resetQuery = () => {
  query.keyword = ''
  query.archiveType = ''
  query.securityLevel = ''
  void loadData()
}

onMounted(loadData)
</script>
