<template>
  <el-tree-select
    :model-value="modelValue"
    :data="data"
    :props="treeProps"
    :node-key="valueKey"
    :render-after-expand="false"
    check-strictly
    clearable
    filterable
    default-expand-all
    :placeholder="placeholder"
    @update:model-value="emit('update:modelValue', $event)"
  />
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  modelValue?: string
  data: Record<string, any>[]
  placeholder?: string
  labelKey?: string
  valueKey?: string
}>(), {
  modelValue: '',
  placeholder: '请选择',
  labelKey: 'typeName',
  valueKey: 'typeCode'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value?: string): void
}>()

const treeProps = {
  label: props.labelKey,
  children: 'children'
}
</script>
