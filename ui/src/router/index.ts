import { createRouter, createWebHistory } from 'vue-router'
import type { RouterScrollBehavior } from 'vue-router'

import Error from '@/views/Error.vue'

import auth from './auth'

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
    ...auth,
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
