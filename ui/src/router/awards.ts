import type { RouteRecordRaw } from 'vue-router'
import { permissions } from '@/config/auth'
import { requirePermission } from '@/helpers/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/awards',
    name: 'awards:list',
    component: () => import('@/views/AwardList/index.vue'),
    beforeEnter: (to, from, next) => {
      requirePermission(permissions.awards.list, from, next)
    },
  },
  {
    path: '/awards/add',
    name: 'awards:add',
    component: () => import('@/views/AwardAdd.vue'),
    props: {
      mode: 'create',
    },
    beforeEnter: (to, from, next) => {
      requirePermission(permissions.awards.add, from, next)
    },
  },
  {
    path: '/awards/suggest',
    name: 'awards:suggest',
    component: () => import('@/views/AwardAdd.vue'),
    props: {
      mode: 'suggest',
    },
    beforeEnter: (to, from, next) => {
      requirePermission(permissions.awards.suggest, from, next)
    },
  },
  {
    path: '/awards/:id/edit',
    name: 'awards:edit',
    component: () => import('@/views/AwardEdit.vue'),
    beforeEnter: (to, from, next) => {
      requirePermission(permissions.awards.edit, from, next)
    },
  },
]
export default routes
