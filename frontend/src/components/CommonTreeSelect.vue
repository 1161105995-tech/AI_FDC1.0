<template>
  <el-tree-select
    :model-value="modelValue"
    :data="normalizedData"
    :props="treeProps"
    :node-key="valueKey"
    :value-key="valueKey"
    :render-after-expand="false"
    :filter-node-method="filterNode"
    check-strictly
    clearable
    filterable
    default-expand-all
    :placeholder="placeholder"
    @update:model-value="emit('update:modelValue', $event)"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface TreeNodeRecord {
  [key: string]: any
}

const props = withDefaults(defineProps<{
  modelValue?: string
  data: TreeNodeRecord[]
  placeholder?: string
  labelKey?: string
  valueKey?: string
  childrenKey?: string
}>(), {
  modelValue: '',
  placeholder: '请选择',
  labelKey: 'label',
  valueKey: 'value',
  childrenKey: 'children'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value?: string): void
}>()

const normalizeNodes = (nodes: TreeNodeRecord[]): TreeNodeRecord[] =>
  (Array.isArray(nodes) ? nodes : []).map(node => ({
    ...node,
    [props.childrenKey]: normalizeNodes(Array.isArray(node[props.childrenKey]) ? node[props.childrenKey] : [])
  }))

const normalizedData = computed(() => normalizeNodes(props.data))

const treeProps = computed(() => ({
  label: props.labelKey,
  children: props.childrenKey
}))

const filterNode = (keyword: string, data: TreeNodeRecord) => {
  if (!keyword) return true
  const label = String(data[props.labelKey] ?? '')
  const value = String(data[props.valueKey] ?? '')
  return label.includes(keyword) || value.includes(keyword)
}
</script>
