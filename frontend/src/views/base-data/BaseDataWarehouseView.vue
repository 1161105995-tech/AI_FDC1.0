<template>
  <div class="app-page warehouse-workbench">
    <div class="warehouse-overview-grid">
      <el-card v-for="card in overviewCards" :key="card.label" shadow="never" class="overview-card">
        <div class="overview-card__label">{{ card.label }}</div>
        <div class="overview-card__value">{{ card.value }}</div>
        <div class="overview-card__meta">{{ card.meta }}</div>
      </el-card>
    </div>

    <el-row :gutter="16" class="warehouse-workbench__body">
      <el-col :xs="24" :xl="10">
        <el-card shadow="never" class="page-section-card warehouse-query-card">
          <template #header>
            <div class="page-toolbar">
              <div class="page-toolbar__left">
                <span>库房查询</span>
                <el-tag type="info" effect="plain">{{ filteredWarehouseCards.length }} 个结果</el-tag>
              </div>
              <el-button type="primary" @click="openWarehouseCreateDialog">新建库房</el-button>
            </div>
          </template>

          <el-form label-position="top" class="warehouse-query-form">
            <div class="warehouse-query-form__grid">
              <el-form-item label="库房名称">
                <el-input v-model.trim="queryForm.warehouseName" placeholder="请输入库房名称" clearable />
              </el-form-item>
              <el-form-item label="库房编码">
                <el-input v-model.trim="queryForm.warehouseCode" placeholder="请输入库房编码" clearable />
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model.trim="queryForm.address" placeholder="请输入地址" clearable />
              </el-form-item>
              <el-form-item label="是否启用">
                <el-select v-model="queryForm.status" placeholder="全部" clearable>
                  <el-option label="启用" value="ACTIVE" />
                  <el-option label="停用" value="INACTIVE" />
                </el-select>
              </el-form-item>
              <el-form-item label="库位编码">
                <el-input v-model.trim="queryForm.locationCode" placeholder="请输入库位编码" clearable />
              </el-form-item>
            </div>
          </el-form>
        </el-card>

        <div class="warehouse-card-toolbar">
          <div class="warehouse-card-toolbar__title">库房列表</div>
          <div class="warehouse-card-toolbar__actions">
            <span class="warehouse-card-toolbar__label">排序方式</span>
            <el-segmented
              v-model="sortType"
              :options="sortOptions"
              size="small"
            />
          </div>
        </div>

        <div class="warehouse-card-list">
          <el-card
            v-for="item in filteredWarehouseCards"
            :key="item.warehouse.warehouseCode"
            shadow="hover"
            class="warehouse-card"
            :class="{ 'is-active': item.warehouse.warehouseCode === selectedWarehouseCode }"
            @click="selectWarehouse(item.warehouse.warehouseCode)"
          >
            <div class="warehouse-card__body">
              <div class="warehouse-card__image-wrap">
                <img :src="item.warehouse.photoUrl || defaultWarehouseImage" alt="warehouse" class="warehouse-card__image" />
              </div>
              <div class="warehouse-card__content">
                <div class="warehouse-card__header">
                  <div>
                    <div class="warehouse-card__title-row">
                      <span class="warehouse-card__title">{{ item.warehouse.warehouseName }}</span>
                      <el-tag size="small" :type="item.warehouse.status === 'ACTIVE' ? 'success' : 'info'">
                        {{ item.warehouse.status === 'ACTIVE' ? '启用' : '停用' }}
                      </el-tag>
                    </div>
                    <div class="warehouse-card__code">{{ item.warehouse.warehouseCode }}</div>
                  </div>
                  <div class="warehouse-card__actions" @click.stop>
                    <el-button link type="primary" @click="openWarehouseEditDialog(item)">编辑</el-button>
                    <el-button link @click="openWarehouseCopyDialog(item)">复制</el-button>
                    <el-button link type="danger" @click="deleteWarehouseAction(item)">删除</el-button>
                  </div>
                </div>

                <div class="warehouse-card__details">
                  <span>面积：{{ formatArea(item.warehouse.areaSize) }}</span>
                  <span>地址：{{ item.warehouse.address || '-' }}</span>
                  <span>区域数量：{{ item.areaCount }}</span>
                  <span>柜组数量：{{ item.rackCount }}</span>
                  <span>库位数量：{{ item.locationCount }}</span>
                </div>
              </div>
            </div>
          </el-card>

          <el-empty v-if="!filteredWarehouseCards.length" description="暂无符合条件的库房" />
        </div>
      </el-col>

      <el-col :xs="24" :xl="14">
        <el-card shadow="never" class="page-section-card warehouse-visual-card">
          <template #header>
            <div class="page-toolbar">
              <div class="page-toolbar__left">
                <span>{{ selectedWarehouseSummary?.warehouse.warehouseName || '请选择库房' }}</span>
                <el-tag v-if="selectedWarehouseSummary" type="primary" effect="plain">{{ selectedWarehouseSummary.warehouse.warehouseCode }}</el-tag>
              </div>
              <div class="page-toolbar__right">
                <el-button :disabled="!selectedWarehouseSummary" @click="collapseAllRacks">全部收缩</el-button>
                <el-button :disabled="!selectedWarehouseSummary" @click="expandAllRacks">全部展开</el-button>
                <el-button :disabled="!selectedWarehouseSummary" @click="openAreaCreateDialog">新建区域</el-button>
                <el-button :disabled="!selectedWarehouseSummary" @click="refreshSelectedWarehouse">刷新</el-button>
              </div>
            </div>
          </template>

          <div v-if="!selectedWarehouseSummary" class="warehouse-empty-state">
            <el-empty description="请选择左侧库房后查看区域与柜组可视化" />
          </div>

          <div v-else class="warehouse-visual-content">
            <div v-if="!selectedManagement?.areas.length" class="warehouse-empty-state warehouse-empty-state--compact">
              <el-empty description="当前库房暂无区域，请先创建区域" />
            </div>

            <div v-else class="area-visual-list">
              <div v-for="area in selectedManagement.areas" :key="area.areaCode" class="area-visual-box">
                <div class="area-visual-box__header">
                  <div>
                    <div class="area-visual-box__title">{{ area.areaName }}</div>
                    <div class="area-visual-box__meta">{{ area.areaCode }} · {{ area.status === 'ACTIVE' ? '启用' : '停用' }}</div>
                  </div>
                  <div class="area-visual-box__actions">
                    <el-button link @click="collapseAreaRacks(area.areaCode)">收缩柜组</el-button>
                    <el-button link @click="expandAreaRacks(area.areaCode)">展开柜组</el-button>
                    <el-button link type="primary" @click="openRackCreateDialog(area)">新建柜组</el-button>
                    <el-button link type="primary" @click="openAreaEditDialog(area)">编辑</el-button>
                    <el-button link @click="openAreaCopyDialog(area)">复制</el-button>
                    <el-button link type="danger" @click="deleteAreaAction(area)">删除</el-button>
                  </div>
                </div>

                <div class="rack-card-grid">
                  <el-card
                    v-for="rack in racksByArea(area.areaCode)"
                    :key="rack.rackCode"
                    shadow="never"
                    class="rack-board-card"
                    :class="{ 'is-collapsed': isRackCollapsed(rack.rackCode) }"
                  >
                    <template #header>
                      <div class="rack-board-card__header">
                        <div>
                          <div class="rack-board-card__title">{{ rack.rackName }}</div>
                          <div class="rack-board-card__meta">{{ rack.rackCode }} · {{ rack.layerCount }} 层 / {{ rack.slotCount }} 格</div>
                        </div>
                        <div class="rack-board-card__actions">
                          <el-button link @click="toggleRackCollapse(rack.rackCode)">{{ isRackCollapsed(rack.rackCode) ? '展开' : '收缩' }}</el-button>
                          <el-button link type="primary" @click="openRackEditDialog(rack)">编辑</el-button>
                          <el-button link @click="openRackCopyDialog(rack)">复制</el-button>
                          <el-button link type="danger" @click="deleteRackAction(rack)">删除</el-button>
                        </div>
                      </div>
                    </template>

                    <div v-if="!isRackCollapsed(rack.rackCode)" class="rack-slots-board">
                      <div v-for="layer in buildRackLayers(rack)" :key="layer.layerCode" class="rack-layer-row">
                        <span class="rack-layer-row__label">{{ layer.layerLabel }}</span>
                        <div class="rack-layer-row__slots">
                          <button
                            v-for="location in layer.locations"
                            :key="location.locationCode"
                            type="button"
                            class="location-slot"
                            :class="`is-${location.status.toLowerCase()}`"
                            @click="openLocationDialog(location)"
                          >
                            <span class="location-slot__code">{{ displayLocationCode(location.locationCode) }}</span>
                          </button>
                        </div>
                      </div>
                    </div>

                    <div v-else class="rack-collapsed-panel">
                      <div class="rack-collapsed-metric">
                        <strong>{{ rack.layerCount }}</strong>
                        <span>层数</span>
                      </div>
                      <div class="rack-collapsed-metric">
                        <strong>{{ rackStats(rack).total }}</strong>
                        <span>总格数</span>
                      </div>
                      <div class="rack-collapsed-metric rack-collapsed-metric--success">
                        <strong>{{ rackStats(rack).free }}</strong>
                        <span>可用格数</span>
                      </div>
                      <div class="rack-collapsed-metric rack-collapsed-metric--danger">
                        <strong>{{ rackStats(rack).abnormal }}</strong>
                        <span>异常格数</span>
                      </div>
                    </div>
                  </el-card>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogs.warehouseForm" :title="warehouseFormMode === 'create' ? '新建库房' : '编辑库房'" width="620px">
      <el-form label-width="96px">
        <el-form-item v-if="warehouseFormMode === 'create'" label="库房编码"><el-input v-model.trim="warehouseForm.warehouseCode" /></el-form-item>
        <el-form-item label="库房名称"><el-input v-model.trim="warehouseForm.warehouseName" /></el-form-item>
        <el-form-item label="库房类型"><el-input v-model.trim="warehouseForm.warehouseType" /></el-form-item>
        <el-form-item label="负责人"><el-input v-model.trim="warehouseForm.managerName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model.trim="warehouseForm.contactPhone" /></el-form-item>
        <el-form-item label="面积(㎡)"><el-input-number v-model="warehouseForm.areaSize" :min="0" :precision="2" class="form-number" /></el-form-item>
        <el-form-item label="地址"><el-input v-model.trim="warehouseForm.address" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="warehouseForm.status"><el-option label="启用" value="ACTIVE" /><el-option label="停用" value="INACTIVE" /></el-select></el-form-item>
        <el-form-item label="库房图片">
          <el-upload
            class="warehouse-photo-upload"
            action="#"
            :auto-upload="false"
            list-type="picture-card"
            :limit="1"
            :file-list="warehousePhotoList"
            :on-change="handleWarehousePhotoChange"
            :on-remove="handleWarehousePhotoRemove"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="说明"><el-input v-model.trim="warehouseForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogs.warehouseForm = false">取消</el-button>
        <el-button type="primary" @click="submitWarehouseForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogs.warehouseCopy" title="复制库房" width="560px">
      <el-form label-width="110px">
        <el-form-item label="目标库房编码"><el-input v-model.trim="warehouseCopyForm.targetWarehouseCode" /></el-form-item>
        <el-form-item label="目标库房名称"><el-input v-model.trim="warehouseCopyForm.targetWarehouseName" /></el-form-item>
        <el-form-item label="负责人"><el-input v-model.trim="warehouseCopyForm.managerName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model.trim="warehouseCopyForm.contactPhone" /></el-form-item>
        <el-form-item label="面积(㎡)"><el-input-number v-model="warehouseCopyForm.areaSize" :min="0" :precision="2" class="form-number" /></el-form-item>
        <el-form-item label="地址"><el-input v-model.trim="warehouseCopyForm.address" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="warehouseCopyForm.status"><el-option label="启用" value="ACTIVE" /><el-option label="停用" value="INACTIVE" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogs.warehouseCopy = false">取消</el-button>
        <el-button type="primary" @click="submitWarehouseCopy">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogs.areaForm" :title="areaFormMode === 'create' ? '新建区域' : '编辑区域'" width="520px">
      <el-form label-width="96px">
        <el-form-item v-if="areaFormMode === 'create'" label="区域编码"><el-input v-model.trim="areaForm.areaCode" /></el-form-item>
        <el-form-item label="区域名称"><el-input v-model.trim="areaForm.areaName" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="areaForm.status"><el-option label="启用" value="ACTIVE" /><el-option label="停用" value="INACTIVE" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogs.areaForm = false">取消</el-button>
        <el-button type="primary" @click="submitAreaForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogs.areaCopy" title="复制区域" width="520px">
      <el-form label-width="110px">
        <el-form-item label="目标区域编码"><el-input v-model.trim="areaCopyForm.targetAreaCode" /></el-form-item>
        <el-form-item label="目标区域名称"><el-input v-model.trim="areaCopyForm.targetAreaName" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogs.areaCopy = false">取消</el-button>
        <el-button type="primary" @click="submitAreaCopy">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogs.rackForm" :title="rackFormMode === 'create' ? '新建柜组' : '编辑柜组'" width="560px">
      <el-form label-width="96px">
        <el-form-item v-if="rackFormMode === 'create'" label="所属区域">
          <el-select v-model="rackForm.areaCode">
            <el-option v-for="item in selectedManagement?.areas || []" :key="item.areaCode" :label="item.areaName" :value="item.areaCode" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="rackFormMode === 'create'" label="柜组编码"><el-input v-model.trim="rackForm.rackCode" /></el-form-item>
        <el-form-item label="柜组名称"><el-input v-model.trim="rackForm.rackName" /></el-form-item>
        <el-form-item v-if="rackFormMode === 'create'" label="层数"><el-input-number v-model="rackForm.layerCount" :min="1" class="form-number" /></el-form-item>
        <el-form-item v-if="rackFormMode === 'create'" label="每层格数"><el-input-number v-model="rackForm.slotCount" :min="1" class="form-number" /></el-form-item>
        <el-form-item v-if="rackFormMode === 'edit'" label="状态"><el-select v-model="rackForm.status"><el-option label="正常" value="NORMAL" /><el-option label="预警" value="WARNING" /><el-option label="封存" value="SEALED" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogs.rackForm = false">取消</el-button>
        <el-button type="primary" @click="submitRackForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogs.rackCopy" title="复制柜组" width="560px">
      <el-form label-width="110px">
        <el-form-item label="目标区域">
          <el-select v-model="rackCopyForm.targetAreaCode">
            <el-option v-for="item in selectedManagement?.areas || []" :key="item.areaCode" :label="item.areaName" :value="item.areaCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标柜组编码"><el-input v-model.trim="rackCopyForm.targetRackCode" /></el-form-item>
        <el-form-item label="目标柜组名称"><el-input v-model.trim="rackCopyForm.targetRackName" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogs.rackCopy = false">取消</el-button>
        <el-button type="primary" @click="submitRackCopy">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogs.locationEdit" title="库位状态维护" width="520px">
      <el-form label-width="96px">
        <el-form-item label="库位编码"><el-input :model-value="locationForm.locationCode" readonly /></el-form-item>
        <el-form-item label="库位名称"><el-input v-model.trim="locationForm.locationName" /></el-form-item>
        <el-form-item label="可用状态">
          <el-select v-model="locationForm.status">
            <el-option label="可用" value="FREE" />
            <el-option label="已用" value="OCCUPIED" />
            <el-option label="预警" value="WARNING" />
            <el-option label="封存" value="SEALED" />
            <el-option label="异常" value="ABNORMAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量"><el-input-number v-model="locationForm.capacity" :min="1" class="form-number" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogs.locationEdit = false">取消</el-button>
        <el-button type="primary" @click="submitLocationEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type UploadFile, type UploadUserFile } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  copyWarehouse,
  copyWarehouseArea,
  copyWarehouseRack,
  createWarehouse,
  createWarehouseArea,
  createWarehouseRack,
  deleteWarehouse,
  deleteWarehouseArea,
  deleteWarehouseRack,
  fetchWarehouseManagement,
  fetchWarehouses,
  updateWarehouse,
  updateWarehouseArea,
  updateWarehouseLocation,
  updateWarehouseRack,
  type WarehouseAreaCopyCommand,
  type WarehouseAreaCreateCommand,
  type WarehouseAreaUpdateCommand,
  type WarehouseCopyCommand,
  type WarehouseCreateCommand,
  type WarehouseLocationUpdateCommand,
  type WarehouseRackCopyCommand,
  type WarehouseRackCreateCommand,
  type WarehouseRackUpdateCommand,
  type WarehouseUpdateCommand
} from '../../api/modules/warehouse'
import type { WarehouseArea, WarehouseLocation, WarehouseManagementResponse, WarehouseRack, WarehouseSummary } from '../../types'

interface RackLayerView {
  layerCode: string
  layerLabel: string
  locations: WarehouseLocation[]
}

const defaultWarehouseImage = `data:image/svg+xml;utf8,${encodeURIComponent(`
<svg xmlns="http://www.w3.org/2000/svg" width="240" height="160" viewBox="0 0 240 160">
  <defs><linearGradient id="g" x1="0" x2="1" y1="0" y2="1"><stop stop-color="#e8f3ff" offset="0"/><stop stop-color="#cfe5ff" offset="1"/></linearGradient></defs>
  <rect width="240" height="160" rx="18" fill="url(#g)"/>
  <path d="M44 70 120 32l76 38v52a10 10 0 0 1-10 10H54a10 10 0 0 1-10-10Z" fill="#409eff" opacity=".16"/>
  <path d="M58 79h124v44H58z" fill="#409eff" opacity=".22"/>
  <path d="M74 92h24v30H74zm34 0h24v30h-24zm34 0h24v30h-24z" fill="#409eff" opacity=".4"/>
  <text x="120" y="145" text-anchor="middle" fill="#2f5da8" font-size="16" font-family="Microsoft YaHei">仓库示意图</text>
</svg>
`)}`

const sortOptions = [
  { label: '最后更新时间', value: 'updatedAt' },
  { label: '库房命名', value: 'name' },
  { label: '库位数', value: 'locationCount' }
]

const warehouses = ref<WarehouseSummary[]>([])
const selectedWarehouseCode = ref('')
const managementMap = ref<Record<string, WarehouseManagementResponse>>({})
const collapsedRacks = reactive<Record<string, boolean>>({})
const warehousePhotoList = ref<UploadUserFile[]>([])
const sortType = ref<'updatedAt' | 'name' | 'locationCount'>('updatedAt')

const dialogs = reactive({ warehouseForm: false, warehouseCopy: false, areaForm: false, areaCopy: false, rackForm: false, rackCopy: false, locationEdit: false })
const warehouseFormMode = ref<'create' | 'edit'>('create')
const areaFormMode = ref<'create' | 'edit'>('create')
const rackFormMode = ref<'create' | 'edit'>('create')
const currentWarehouseActionCode = ref('')
const currentAreaActionCode = ref('')
const currentRackActionCode = ref('')

const queryForm = reactive({ warehouseName: '', warehouseCode: '', address: '', status: '', locationCode: '' })
const warehouseForm = reactive<WarehouseCreateCommand>({ warehouseCode: '', warehouseName: '', warehouseType: '综合档案库', managerName: '', contactPhone: '', address: '', areaSize: undefined, photoUrl: '', status: 'ACTIVE', description: '' })
const warehouseCopyForm = reactive<WarehouseCopyCommand>({ targetWarehouseCode: '', targetWarehouseName: '', managerName: '', contactPhone: '', address: '', areaSize: undefined, photoUrl: '', status: 'ACTIVE', description: '' })
const areaForm = reactive<WarehouseAreaCreateCommand & WarehouseAreaUpdateCommand>({ warehouseCode: '', areaCode: '', areaName: '', startX: 0, startY: 0, width: 620, height: 260, status: 'ACTIVE' })
const areaCopyForm = reactive<WarehouseAreaCopyCommand>({ targetAreaCode: '', targetAreaName: '' })
const rackForm = reactive<WarehouseRackCreateCommand & WarehouseRackUpdateCommand>({ warehouseCode: '', areaCode: '', rackCode: '', rackName: '', layerCount: 5, slotCount: 8, startX: 40, startY: 40, status: 'NORMAL' })
const rackCopyForm = reactive<WarehouseRackCopyCommand>({ targetAreaCode: '', targetRackCode: '', targetRackName: '' })
const locationForm = reactive({ locationCode: '', locationName: '', status: 'FREE', capacity: 1 })

const selectedWarehouseSummary = computed(() => warehouses.value.find(item => item.warehouse.warehouseCode === selectedWarehouseCode.value))
const selectedManagement = computed(() => managementMap.value[selectedWarehouseCode.value])

const filteredWarehouseCards = computed(() => {
  const keywordName = queryForm.warehouseName.toLowerCase()
  const keywordCode = queryForm.warehouseCode.toLowerCase()
  const keywordAddress = queryForm.address.toLowerCase()
  const keywordLocation = queryForm.locationCode.toLowerCase()

  const list = warehouses.value.filter(item => {
    const warehouse = item.warehouse
    const basicMatched = (!keywordName || warehouse.warehouseName.toLowerCase().includes(keywordName))
      && (!keywordCode || warehouse.warehouseCode.toLowerCase().includes(keywordCode))
      && (!keywordAddress || (warehouse.address || '').toLowerCase().includes(keywordAddress))
      && (!queryForm.status || warehouse.status === queryForm.status)

    if (!basicMatched) return false
    if (!keywordLocation) return true
    const management = managementMap.value[warehouse.warehouseCode]
    return Boolean(management?.locations.some(location => location.locationCode.toLowerCase().includes(keywordLocation)))
  })

  return [...list].sort((a, b) => {
    if (sortType.value === 'name') return a.warehouse.warehouseName.localeCompare(b.warehouse.warehouseName, 'zh-CN')
    if (sortType.value === 'locationCount') return b.locationCount - a.locationCount
    return new Date(b.lastUpdatedAt || 0).getTime() - new Date(a.lastUpdatedAt || 0).getTime()
  })
})

const overviewCards = computed(() => {
  const selected = selectedWarehouseSummary.value
  const scope = selected ? [selected] : filteredWarehouseCards.value
  const areaCount = scope.reduce((sum, item) => sum + item.areaCount, 0)
  const rackCount = scope.reduce((sum, item) => sum + item.rackCount, 0)
  const locationCount = scope.reduce((sum, item) => sum + item.locationCount, 0)
  const freeLocationCount = scope.reduce((sum, item) => sum + item.freeLocationCount, 0)
  const occupiedLocationCount = scope.reduce((sum, item) => sum + item.occupiedLocationCount, 0)
  const scopeLabel = selected ? `当前库房：${selected.warehouse.warehouseName}` : '当前查询范围'
  return [
    { label: '总库房数', value: String(filteredWarehouseCards.value.length), meta: '符合查询条件的库房数量' },
    { label: '总区域数', value: String(areaCount), meta: scopeLabel },
    { label: '柜组数量', value: String(rackCount), meta: scopeLabel },
    { label: '可用库位', value: String(freeLocationCount), meta: `总 ${locationCount} / 已用 ${occupiedLocationCount}` }
  ]
})

const formatArea = (value?: number) => (value == null ? '-' : `${value} ㎡`)
const racksByArea = (areaCode: string) => (selectedManagement.value?.racks || []).filter(item => item.areaCode === areaCode)
const isRackCollapsed = (rackCode: string) => Boolean(collapsedRacks[rackCode])
const toggleRackCollapse = (rackCode: string) => { collapsedRacks[rackCode] = !collapsedRacks[rackCode] }
const collapseAreaRacks = (areaCode: string) => racksByArea(areaCode).forEach(rack => { collapsedRacks[rack.rackCode] = true })
const expandAreaRacks = (areaCode: string) => racksByArea(areaCode).forEach(rack => { collapsedRacks[rack.rackCode] = false })
const collapseAllRacks = () => (selectedManagement.value?.racks || []).forEach(rack => { collapsedRacks[rack.rackCode] = true })
const expandAllRacks = () => (selectedManagement.value?.racks || []).forEach(rack => { collapsedRacks[rack.rackCode] = false })
const displayLocationCode = (locationCode: string) => locationCode.split('-').slice(-2).join('-')

const rackLocations = (rack: WarehouseRack) => selectedManagement.value?.locations.filter(item => item.shelfCode === rack.rackCode) || []
const rackStats = (rack: WarehouseRack) => {
  const locations = rackLocations(rack)
  return {
    total: locations.length,
    free: locations.filter(item => item.status === 'FREE').length,
    abnormal: locations.filter(item => ['WARNING', 'ABNORMAL', 'SEALED'].includes(item.status)).length
  }
}

const buildRackLayers = (rack: WarehouseRack): RackLayerView[] => {
  const map = new Map<string, WarehouseLocation[]>()
  rackLocations(rack).forEach(location => {
    if (!map.has(location.layerCode)) map.set(location.layerCode, [])
    map.get(location.layerCode)?.push(location)
  })
  return [...map.entries()].sort((a, b) => a[0].localeCompare(b[0])).map(([layerCode, locations]) => ({
    layerCode,
    layerLabel: `第${Number(layerCode)}层`,
    locations: [...locations].sort((a, b) => a.locationCode.localeCompare(b.locationCode))
  }))
}

const syncWarehousePhotoList = (photoUrl?: string) => {
  warehousePhotoList.value = photoUrl ? [{ name: 'warehouse-photo', url: photoUrl }] : []
}

const handleWarehousePhotoChange = async (uploadFile: UploadFile) => {
  const raw = uploadFile.raw
  if (!raw) return
  if (!raw.type.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    warehousePhotoList.value = []
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    warehouseForm.photoUrl = String(reader.result || '')
    syncWarehousePhotoList(warehouseForm.photoUrl)
  }
  reader.readAsDataURL(raw)
}

const handleWarehousePhotoRemove = () => {
  warehouseForm.photoUrl = ''
  warehousePhotoList.value = []
}

const ensureManagementLoaded = async (warehouseCode: string, force = false) => {
  if (!warehouseCode) return
  if (!force && managementMap.value[warehouseCode]) return
  const data = await fetchWarehouseManagement(warehouseCode)
  managementMap.value = { ...managementMap.value, [warehouseCode]: data }
}

const preloadLocationSearchData = async () => {
  if (!queryForm.locationCode) return
  await Promise.all(warehouses.value.map(item => ensureManagementLoaded(item.warehouse.warehouseCode)))
}

const loadWarehouses = async () => {
  warehouses.value = await fetchWarehouses()
  if ((!selectedWarehouseCode.value || !warehouses.value.some(item => item.warehouse.warehouseCode === selectedWarehouseCode.value)) && warehouses.value.length) {
    selectedWarehouseCode.value = warehouses.value[0].warehouse.warehouseCode
  }
  if (selectedWarehouseCode.value) await ensureManagementLoaded(selectedWarehouseCode.value, true)
}

const selectWarehouse = async (warehouseCode: string) => {
  selectedWarehouseCode.value = warehouseCode
  await ensureManagementLoaded(warehouseCode)
}

const refreshSelectedWarehouse = async () => {
  if (!selectedWarehouseCode.value) return
  await ensureManagementLoaded(selectedWarehouseCode.value, true)
  await loadWarehouses()
}

const resetWarehouseForm = () => {
  Object.assign(warehouseForm, { warehouseCode: '', warehouseName: '', warehouseType: '综合档案库', managerName: '', contactPhone: '', address: '', areaSize: undefined, photoUrl: '', status: 'ACTIVE', description: '' })
  syncWarehousePhotoList()
}
const resetAreaForm = () => Object.assign(areaForm, { warehouseCode: selectedWarehouseCode.value, areaCode: '', areaName: '', startX: 0, startY: 0, width: 620, height: 260, status: 'ACTIVE' })
const resetRackForm = () => Object.assign(rackForm, { warehouseCode: selectedWarehouseCode.value, areaCode: selectedManagement.value?.areas[0]?.areaCode || '', rackCode: '', rackName: '', layerCount: 5, slotCount: 8, startX: 40, startY: 40, status: 'NORMAL' })

const openWarehouseCreateDialog = () => {
  warehouseFormMode.value = 'create'
  currentWarehouseActionCode.value = ''
  resetWarehouseForm()
  dialogs.warehouseForm = true
}
const openWarehouseEditDialog = (item: WarehouseSummary) => {
  warehouseFormMode.value = 'edit'
  currentWarehouseActionCode.value = item.warehouse.warehouseCode
  Object.assign(warehouseForm, item.warehouse)
  syncWarehousePhotoList(item.warehouse.photoUrl)
  dialogs.warehouseForm = true
}
const openWarehouseCopyDialog = (item: WarehouseSummary) => {
  currentWarehouseActionCode.value = item.warehouse.warehouseCode
  Object.assign(warehouseCopyForm, { targetWarehouseCode: `${item.warehouse.warehouseCode}-COPY`, targetWarehouseName: `${item.warehouse.warehouseName}-副本`, managerName: item.warehouse.managerName, contactPhone: item.warehouse.contactPhone, address: item.warehouse.address, areaSize: item.warehouse.areaSize, photoUrl: item.warehouse.photoUrl, status: item.warehouse.status, description: item.warehouse.description })
  dialogs.warehouseCopy = true
}

const submitWarehouseForm = async () => {
  if (!warehouseForm.warehouseName || !warehouseForm.warehouseType || !warehouseForm.managerName || (warehouseFormMode.value === 'create' && !warehouseForm.warehouseCode)) {
    ElMessage.warning('请完整填写库房必填信息')
    return
  }
  if (warehouseFormMode.value === 'create') {
    const created = await createWarehouse(warehouseForm)
    ElMessage.success(`已创建库房 ${created.warehouseName}`)
    selectedWarehouseCode.value = created.warehouseCode
  } else {
    await updateWarehouse(currentWarehouseActionCode.value, warehouseForm as WarehouseUpdateCommand)
    ElMessage.success('库房信息已更新')
  }
  dialogs.warehouseForm = false
  await loadWarehouses()
}

const submitWarehouseCopy = async () => {
  if (!warehouseCopyForm.targetWarehouseCode || !warehouseCopyForm.targetWarehouseName) {
    ElMessage.warning('请填写复制后的库房编码和名称')
    return
  }
  const created = await copyWarehouse(currentWarehouseActionCode.value, warehouseCopyForm)
  ElMessage.success(`已复制为 ${created.warehouseName}`)
  dialogs.warehouseCopy = false
  selectedWarehouseCode.value = created.warehouseCode
  await loadWarehouses()
}

const deleteWarehouseAction = async (item: WarehouseSummary) => {
  await ElMessageBox.confirm(`确认删除库房 ${item.warehouse.warehouseName} 吗？`, '删除确认', { type: 'warning' })
  await deleteWarehouse(item.warehouse.warehouseCode)
  ElMessage.success('库房已删除')
  if (selectedWarehouseCode.value === item.warehouse.warehouseCode) selectedWarehouseCode.value = ''
  await loadWarehouses()
}

const openAreaCreateDialog = () => {
  if (!selectedWarehouseCode.value) return ElMessage.warning('请先选择库房')
  areaFormMode.value = 'create'
  currentAreaActionCode.value = ''
  resetAreaForm()
  dialogs.areaForm = true
}
const openAreaEditDialog = (area: WarehouseArea) => {
  areaFormMode.value = 'edit'
  currentAreaActionCode.value = area.areaCode
  Object.assign(areaForm, area)
  dialogs.areaForm = true
}
const openAreaCopyDialog = (area: WarehouseArea) => {
  currentAreaActionCode.value = area.areaCode
  areaCopyForm.targetAreaCode = `${area.areaCode}-COPY`
  areaCopyForm.targetAreaName = `${area.areaName}-副本`
  dialogs.areaCopy = true
}
const submitAreaForm = async () => {
  if (!selectedWarehouseCode.value || !areaForm.areaName || (areaFormMode.value === 'create' && !areaForm.areaCode)) {
    ElMessage.warning('请完整填写区域信息')
    return
  }
  if (areaFormMode.value === 'create') {
    await createWarehouseArea({ ...areaForm, warehouseCode: selectedWarehouseCode.value })
    ElMessage.success('区域已创建')
  } else {
    await updateWarehouseArea(selectedWarehouseCode.value, currentAreaActionCode.value, areaForm as WarehouseAreaUpdateCommand)
    ElMessage.success('区域已更新')
  }
  dialogs.areaForm = false
  await refreshSelectedWarehouse()
}
const submitAreaCopy = async () => {
  if (!selectedWarehouseCode.value || !areaCopyForm.targetAreaCode || !areaCopyForm.targetAreaName) {
    ElMessage.warning('请完整填写目标区域信息')
    return
  }
  await copyWarehouseArea(selectedWarehouseCode.value, currentAreaActionCode.value, areaCopyForm)
  ElMessage.success('区域已复制')
  dialogs.areaCopy = false
  await refreshSelectedWarehouse()
}
const deleteAreaAction = async (area: WarehouseArea) => {
  if (!selectedWarehouseCode.value) return
  await ElMessageBox.confirm(`确认删除区域 ${area.areaName} 吗？`, '删除确认', { type: 'warning' })
  await deleteWarehouseArea(selectedWarehouseCode.value, area.areaCode)
  ElMessage.success('区域已删除')
  await refreshSelectedWarehouse()
}

const openRackCreateDialog = (area?: WarehouseArea) => {
  if (!selectedWarehouseCode.value) return ElMessage.warning('请先选择库房')
  rackFormMode.value = 'create'
  currentRackActionCode.value = ''
  resetRackForm()
  rackForm.areaCode = area?.areaCode || rackForm.areaCode
  dialogs.rackForm = true
}
const openRackEditDialog = (rack: WarehouseRack) => {
  rackFormMode.value = 'edit'
  currentRackActionCode.value = rack.rackCode
  Object.assign(rackForm, rack)
  dialogs.rackForm = true
}
const openRackCopyDialog = (rack: WarehouseRack) => {
  currentRackActionCode.value = rack.rackCode
  Object.assign(rackCopyForm, { targetAreaCode: rack.areaCode, targetRackCode: `${rack.rackCode}-COPY`, targetRackName: `${rack.rackName}-副本` })
  dialogs.rackCopy = true
}
const submitRackForm = async () => {
  if (!selectedWarehouseCode.value || !rackForm.rackName || !rackForm.areaCode || (rackFormMode.value === 'create' && !rackForm.rackCode)) {
    ElMessage.warning('请完整填写柜组信息')
    return
  }
  if (rackFormMode.value === 'create') {
    await createWarehouseRack({ ...rackForm, warehouseCode: selectedWarehouseCode.value } as WarehouseRackCreateCommand)
    ElMessage.success('柜组已创建')
  } else {
    await updateWarehouseRack(selectedWarehouseCode.value, currentRackActionCode.value, rackForm as WarehouseRackUpdateCommand)
    ElMessage.success('柜组已更新')
  }
  dialogs.rackForm = false
  await refreshSelectedWarehouse()
}
const submitRackCopy = async () => {
  if (!selectedWarehouseCode.value || !rackCopyForm.targetRackCode || !rackCopyForm.targetRackName) {
    ElMessage.warning('请填写目标柜组信息')
    return
  }
  await copyWarehouseRack(selectedWarehouseCode.value, currentRackActionCode.value, rackCopyForm)
  ElMessage.success('柜组已复制')
  dialogs.rackCopy = false
  await refreshSelectedWarehouse()
}
const deleteRackAction = async (rack: WarehouseRack) => {
  if (!selectedWarehouseCode.value) return
  await ElMessageBox.confirm(`确认删除柜组 ${rack.rackName} 吗？`, '删除确认', { type: 'warning' })
  await deleteWarehouseRack(selectedWarehouseCode.value, rack.rackCode)
  ElMessage.success('柜组已删除')
  await refreshSelectedWarehouse()
}

const openLocationDialog = (location: WarehouseLocation) => {
  Object.assign(locationForm, { locationCode: location.locationCode, locationName: location.locationName, status: location.status, capacity: location.capacity })
  dialogs.locationEdit = true
}
const submitLocationEdit = async () => {
  if (!locationForm.locationCode || !locationForm.locationName) return ElMessage.warning('请完整填写库位信息')
  await updateWarehouseLocation(locationForm.locationCode, { locationName: locationForm.locationName, status: locationForm.status, capacity: locationForm.capacity } as WarehouseLocationUpdateCommand)
  ElMessage.success('库位状态已更新')
  dialogs.locationEdit = false
  await refreshSelectedWarehouse()
}

watch(() => selectedWarehouseCode.value, async value => { if (value) await ensureManagementLoaded(value) })
watch(() => queryForm.locationCode, async () => { await preloadLocationSearchData() })

onMounted(async () => {
  try {
    await loadWarehouses()
  } catch (error) {
    console.error(error)
    ElMessage.error('库房数据加载失败')
  }
})
</script>

<style scoped>
.warehouse-workbench { gap: 16px; }
.warehouse-overview-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; }
.overview-card { border-radius: 14px; }
.overview-card__label { color: var(--text-secondary); font-size: 13px; }
.overview-card__value { margin-top: 12px; font-size: 30px; line-height: 1; font-weight: 700; text-align: left; }
.overview-card__meta { margin-top: 10px; color: var(--text-regular); font-size: 13px; }
.warehouse-query-card { min-height: 0; }
.warehouse-visual-card { min-height: calc(100vh - 250px); }
.warehouse-query-form :deep(.el-card__body) { padding-bottom: 10px; }
.warehouse-query-form :deep(.el-form-item) { margin-bottom: 10px; }
.warehouse-query-form :deep(.el-form-item__label) { padding-bottom: 4px; }
.warehouse-query-form__grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 0 12px; }
.warehouse-card-toolbar { margin: 14px 0 10px; display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.warehouse-card-toolbar__title { font-size: 15px; font-weight: 700; }
.warehouse-card-toolbar__actions { display: flex; align-items: center; gap: 8px; }
.warehouse-card-toolbar__label { color: var(--text-secondary); font-size: 13px; }
.warehouse-card-list { display: grid; gap: 12px; }
.warehouse-card { cursor: pointer; border-radius: 14px; transition: all 0.2s ease; }
.warehouse-card.is-active { border-color: var(--brand-color); box-shadow: 0 8px 24px rgba(64, 158, 255, 0.12); }
.warehouse-card__body { display: grid; grid-template-columns: 112px 1fr; gap: 14px; }
.warehouse-card__image-wrap { width: 112px; height: 112px; border-radius: 14px; overflow: hidden; background: #eef5ff; }
.warehouse-card__image { width: 100%; height: 100%; object-fit: cover; }
.warehouse-card__header, .area-visual-box__header, .rack-board-card__header { display: flex; justify-content: space-between; gap: 12px; }
.warehouse-card__title-row { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.warehouse-card__title { font-size: 18px; font-weight: 700; }
.warehouse-card__code, .area-visual-box__meta, .rack-board-card__meta { margin-top: 4px; color: var(--text-secondary); font-size: 12px; }
.warehouse-card__actions, .area-visual-box__actions, .rack-board-card__actions { display: flex; gap: 4px; flex-wrap: wrap; }
.warehouse-card__details { margin-top: 14px; display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 8px 16px; color: var(--text-regular); font-size: 13px; }
.warehouse-empty-state { display: flex; min-height: 420px; align-items: center; justify-content: center; }
.warehouse-empty-state--compact { min-height: 300px; }
.area-visual-list { display: grid; gap: 16px; }
.area-visual-box { border: 1px dashed #cbd5e1; border-radius: 16px; padding: 16px; background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%); }
.area-visual-box__title { font-size: 17px; font-weight: 700; }
.rack-card-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 14px; margin-top: 16px; }
.rack-board-card { border-radius: 14px; min-height: 240px; }
.rack-board-card.is-collapsed { min-height: 200px; }
.rack-board-card__title { font-size: 15px; font-weight: 700; }
.rack-slots-board { display: grid; gap: 8px; }
.rack-layer-row { display: grid; grid-template-columns: 48px 1fr; gap: 8px; align-items: start; }
.rack-layer-row__label { padding-top: 6px; color: var(--text-secondary); font-size: 12px; }
.rack-layer-row__slots { display: grid; grid-template-columns: repeat(auto-fit, minmax(42px, 1fr)); gap: 6px; }
.location-slot { min-height: 34px; border: 1px solid #dcdfe6; border-radius: 8px; background: #fff; cursor: pointer; padding: 4px; transition: all 0.2s ease; }
.location-slot:hover { transform: translateY(-1px); border-color: var(--brand-color); }
.location-slot__code { display: block; font-size: 10px; line-height: 1.2; word-break: break-all; }
.location-slot.is-free { background: #f0f9eb; border-color: #b7eb8f; }
.location-slot.is-occupied { background: #ecf5ff; border-color: #91caff; }
.location-slot.is-warning { background: #fff7e6; border-color: #ffd591; }
.location-slot.is-sealed { background: #f4f4f5; border-color: #c8c9cc; }
.location-slot.is-abnormal { background: #fef0f0; border-color: #fab6b6; }
.rack-collapsed-panel { min-height: 120px; display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; align-content: center; }
.rack-collapsed-metric { border-radius: 12px; padding: 12px; background: #f8fafc; display: flex; flex-direction: column; gap: 4px; }
.rack-collapsed-metric strong { font-size: 22px; }
.rack-collapsed-metric span { color: var(--text-secondary); font-size: 12px; }
.rack-collapsed-metric--success strong { color: var(--success-color); }
.rack-collapsed-metric--danger strong { color: var(--danger-color); }
.form-number { width: 100%; }
.warehouse-photo-upload :deep(.el-upload--picture-card), .warehouse-photo-upload :deep(.el-upload-list__item) { width: 112px; height: 112px; }
@media (max-width: 1400px) { .warehouse-overview-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } .warehouse-query-form__grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 960px) { .warehouse-overview-grid, .warehouse-query-form__grid, .warehouse-card__details { grid-template-columns: 1fr; } .warehouse-card__body, .rack-layer-row { grid-template-columns: 1fr; } .warehouse-card__image-wrap { width: 100%; height: 160px; } .warehouse-card-toolbar { flex-direction: column; align-items: flex-start; } }
</style>
