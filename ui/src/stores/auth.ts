import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { useAuth0 } from '@auth0/auth0-vue'

export const useAuthStore = defineStore('auth', () => {
  const { isLoading } = useAuth0()
  const permissions = ref<string[]>([])
  const permissionsSet = ref(false)
  const setPermissions = (newPermissions: string[]) => {
    permissions.value = newPermissions
    permissionsSet.value = true
  }
  const permissionsReady = computed(() => permissionsSet.value && !isLoading.value)

  return {
    permissions,
    setPermissions,
    permissionsReady,
  }
})
