import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLayoutStore = defineStore('layout', () => {
  const collapsed = ref(false)
  const toggle = () => {
    collapsed.value = !collapsed.value
  }

  return {
    collapsed,
    toggle
  }
})
