import type { RouteRecordRaw } from 'vue-router'
import { requirePermission } from '@/helpers/auth'
import { permissions } from '@/config/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/quotes-board',
    name: 'quotesboard',
    component: () => import('@/views/QuoteList/index.vue'),
    beforeEnter: (to, from, next) => {
      requirePermission(permissions.quotes.list, from, next)
    },
  },
]

export default routes
