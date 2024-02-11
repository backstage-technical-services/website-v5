import { ref } from 'vue'
import type { RouteLocationRaw } from 'vue-router'
import router from '@/router'

export type Breadcrumb = {
  text: string
  link?: RouteLocationRaw
}

const breadcrumbs = ref<Breadcrumb[]>([])
export const useBreadcrumbs = () => {
  router.beforeResolve(() => {
    breadcrumbs.value = []
  })

  const setBreadcrumbs = (...crumbs: (Breadcrumb | string)[]) => {
    breadcrumbs.value = crumbs.map(crumb => {
      if (typeof crumb === 'string') {
        return { text: crumb }
      }

      return crumb
    })
  }

  return { breadcrumbs, setBreadcrumbs }
}
