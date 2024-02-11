import type { RouteRecordRaw } from 'vue-router'
import { redirectPath, getRedirect } from '@/config/auth'

const routes: RouteRecordRaw[] = [
  {
    path: redirectPath,
    name: 'auth:redirect',
    redirect: () => getRedirect(),
  },
]
export default routes
