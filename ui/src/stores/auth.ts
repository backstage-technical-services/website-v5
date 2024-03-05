import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false)
  const setAuthenticated = (authenticated: boolean) => {
    isAuthenticated.value = authenticated
  }


  const permissions = ref<string[]>([])
  const permissionsReady = ref(false)
  const setPermissions = (newPermissions: string[]) => {
    permissions.value = newPermissions
    permissionsReady.value = true
  }

  return {
    isAuthenticated,
    setAuthenticated,
    permissions,
    setPermissions,
    permissionsReady,
  }
})
