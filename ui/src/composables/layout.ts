import { ref } from 'vue'
import router from '@/router'

const isHeaderVisible = ref(true)
const isFooterVisible = ref(true)

export const useLayout = () => {
  const reset = () => {
    isHeaderVisible.value = true
    isFooterVisible.value = true
  }

  const hideHeader = () => {
    isHeaderVisible.value = false
  }
  const hideFooter = () => {
    isFooterVisible.value = false
  }

  router.beforeEach((to, from, next) => {
    reset()
    next()
  })

  return {
    isHeaderVisible,
    isFooterVisible,
    hideHeader,
    hideFooter,
    reset,
  }
}
