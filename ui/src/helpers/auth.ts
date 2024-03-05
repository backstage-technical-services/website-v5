import { watch } from 'vue'
import type { NavigationGuardNext, RouteLocationNormalized, RouteLocationRaw } from 'vue-router'
import { storeToRefs } from 'pinia'
import jwtDecode from 'jwt-decode'
import type { JwtPayload } from 'jwt-decode'

import { useNotifications } from '@/composables/notifications'
import auth0, { login } from '@/config/auth'
import { useAuthStore } from '@/stores'

type Auth0Jwt = JwtPayload & {
  permissions?: string[]
}
export const getUserPermissions = async(): Promise<string[]> => {
  if (!auth0.isAuthenticated.value) {
    return []
  }

  const token = await auth0.getAccessTokenSilently()
  const decodedToken = jwtDecode<Auth0Jwt>(token)

  return decodedToken.permissions || []
}

export const can = (permission: string): boolean => {
  const { isAuthenticated, permissions } = storeToRefs(useAuthStore())

  if (!isAuthenticated.value) {
    return false
  }

  return permissions.value.indexOf(permission) !== -1
}

export const requirePermission = (permission: string, from: RouteLocationNormalized, next: NavigationGuardNext) => {
  const { accessNotAllowed } = useNotifications()

  const determineResult = (permission: string, from: RouteLocationNormalized): boolean | RouteLocationRaw => {
    if (can(permission)) {
      return true
    } else {
      if (from.fullPath === '/' && from.name === undefined) {
        return { name: 'home' }
      } else {
        return false
      }
    }
  }

  const performCheck = (permission: string, from: RouteLocationNormalized, next: NavigationGuardNext) => {
    const result = determineResult(permission, from)

    if (result === true) {
      next()
    } else if(!auth0.isAuthenticated.value) {
      login()
      next(false)
    } else {
      accessNotAllowed()
      result === false ? next(false) : next(result)
    }
  }

  const { permissionsReady } = storeToRefs(useAuthStore())

  if (permissionsReady.value) {
    performCheck(permission, from, next)
  } else {
    watch(permissionsReady, ready => {
      if (ready) {
        performCheck(permission, from, next)
      }
    })
  }
}
