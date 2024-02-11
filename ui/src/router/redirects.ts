import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/contact/book/terms',
    redirect: { name: 'terms-conditions' },
  },
  {
    path: '/page/faq',
    redirect: { name: 'faq' },
  },
  {
    path: '/page/privacy-policy',
    redirect: { name: 'privacy-policy' },
  },
]

export default routes
