import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/legal',
    component: null,
    children: [
      {
        path: 'privacy-policy',
        name: 'privacy-policy',
        component: () => import('@/views/PrivacyPolicy.vue'),
      },
      {
        path: 'terms-conditions',
        name: 'terms-conditions',
        component: () => import('@/views/TermsConditions.vue'),
      },
    ],
  },
]

export default routes