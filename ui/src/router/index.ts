import { createRouter, createWebHistory } from 'vue-router'
import type { RouterScrollBehavior } from 'vue-router'

import FrontPage from '@/views/LandingPage/index.vue'
import Error from '@/views/Error.vue'

import auth from './auth'
import legal from './legal'
import quotes from './quotes'
import redirects from './redirects'

const scrollBehavior: RouterScrollBehavior = (to, from, savedPosition) => {
  if (savedPosition) {
    return savedPosition
  } else {
    return {
      top: 0,
      behavior: 'smooth',
    }
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior,
  routes: [
    {
      path: '/',
      name: 'home',
      component: FrontPage,
    },
    {
      path: '/faq',
      name: 'faq',
      component: () => import('@/views/FAQ.vue'),
    },
    ...auth,
    {
      path: '/committee',
      name: 'committee',
      component: () => import('@/views/Committee/index.vue'),
    },
    ...legal,
    ...quotes,
    ...redirects,
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: Error,
      props: {
        code: 404,
        details: 'We couldn\'t find what you were looking for',
      },
    },
  ],
})

export default router
