<template>
  <div class="page-grid">
    <section class="summary-grid">
      <el-card shadow="never"><strong>{{ visual?.warehouseName }}</strong><span>库房名称</span></el-card>
      <el-card shadow="never"><strong>{{ visual?.totalLocations }}</strong><span>总库位</span></el-card>
      <el-card shadow="never"><strong>{{ visual?.occupiedLocations }}</strong><span>已占用</span></el-card>
      <el-card shadow="never"><strong>{{ visual?.warningLocations }}</strong><span>预警/异常</span></el-card>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span>可视化库房平面</span>
          <el-space>
            <el-tag type="success">空闲</el-tag>
            <el-tag type="success" effect="dark">占用</el-tag>
            <el-tag type="warning">预警</el-tag>
            <el-tag type="danger">异常</el-tag>
          </el-space>
        </div>
      </template>
      <div class="warehouse-canvas">
        <button
          v-for="node in visual?.nodes ?? []"
          :key="node.id"
          class="warehouse-node"
          :style="nodeStyle(node)"
          @click="activeNode = node"
        >
          <strong>{{ node.locationCode }}</strong>
          <span>{{ node.occupiedCount }}/{{ node.capacity }}</span>
        </button>
      </div>
    </el-card>

    <el-card shadow="never">
      <template #header>库位详情</template>
      <div v-if="activeNode" class="detail-grid">
        <div><span>库位编码</span><strong>{{ activeNode.locationCode }}</strong></div>
        <div><span>库位名称</span><strong>{{ activeNode.locationName }}</strong></div>
        <div><span>状态</span><strong>{{ activeNode.status }}</strong></div>
        <div><span>容量</span><strong>{{ activeNode.occupiedCount }}/{{ activeNode.capacity }}</strong></div>
      </div>
      <el-empty v-else description="点击上方库位查看详情" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchWarehouseVisualization } from '../../api/modules/warehouse'
import type { WarehouseVisualNode, WarehouseVisualization } from '../../types'

const visual = ref<WarehouseVisualization>()
const activeNode = ref<WarehouseVisualNode>()

const loadData = async () => {
  visual.value = await fetchWarehouseVisualization()
  activeNode.value = visual.value.nodes[0]
}

const nodeStyle = (node: WarehouseVisualNode) => ({
  left: `${node.x}px`,
  top: `${node.y}px`,
  width: `${node.width}px`,
  height: `${node.height}px`,
  background: node.color
})

onMounted(loadData)
</script>

<style scoped>
.page-grid { display: grid; gap: 20px; }
.summary-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; }
.summary-grid .el-card { text-align: center; }
.summary-grid strong { display: block; font-size: 28px; color: #102542; }
.summary-grid span { color: #64748b; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.warehouse-canvas { position: relative; min-height: 220px; border-radius: 18px; background: linear-gradient(180deg, #f8fafc, #eef4ff); overflow: auto; }
.warehouse-node { position: absolute; border: none; border-radius: 16px; color: #102542; box-shadow: 0 8px 24px rgba(15, 23, 42, .08); display: flex; flex-direction: column; justify-content: center; align-items: center; cursor: pointer; }
.warehouse-node strong { font-size: 12px; }
.warehouse-node span { margin-top: 6px; font-size: 12px; }
.detail-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; }
.detail-grid div { padding: 16px; border-radius: 14px; background: #f8fafc; }
.detail-grid span { display: block; color: #64748b; margin-bottom: 8px; }
.detail-grid strong { color: #0f172a; }
@media (max-width: 960px) {
  .summary-grid, .detail-grid { grid-template-columns: 1fr 1fr; }
}
</style>
