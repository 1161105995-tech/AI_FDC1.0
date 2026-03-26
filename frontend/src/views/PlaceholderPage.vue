<template>
  <div style="padding: 24px">
    <el-card shadow="never">
      <template #header>
        <div style="font-weight: 600">{{ title }}</div>
      </template>
      <div>{{ description }}</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => (route.meta.title as string) ?? '功能建设中')
const description = computed(() => (route.meta.description as string) ?? '当前页面正在开发中。')
</script>
