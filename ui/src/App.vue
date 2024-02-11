<template>
  <div class="bg-black" v-if="isLoading"></div>
  <main-layout v-else />
</template>

<script lang="ts" setup>
import { watch } from 'vue'
import { useAuth0 } from '@auth0/auth0-vue'

import MainLayout from '@/layout'
import { useAuthStore } from '@/stores'
import { useNotifications } from '@/composables/notifications'
import { getUserPermissions } from '@/helpers/auth'

const { error, isLoading } = useAuth0()
const { error: notifyError } = useNotifications()
watch(error, err => {
  if (err.value) {
    notifyError(`Authentication error: ${error.value}`)
  }
})

const { setPermissions } = useAuthStore()
watch(isLoading, loading => {
  if (!loading) {
    getUserPermissions().then(setPermissions)
  }
})
</script>
