<template>
  <q-img class="absolute-top" src="https://cdn.quasar.dev/img/material.png" style="height: 150px">
    <div class="absolute-bottom bg-transparent">
      <q-avatar
          v-if="isAuthenticated"
          size="56px"
          class="q-mb-sm"
          :icon="avatar ? undefined : mdiAccountCircle"
          color="grey-8"
          font-size="42px">
        <img :src="avatar" v-if="avatar" />
      </q-avatar>

      <div class="text-weight-bold">{{ isAuthenticated ? user?.name : 'Not logged in' }}</div>
      <div v-if="isAuthenticated">{{ user?.email }}</div>
    </div>
  </q-img>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useAuth0 } from '@auth0/auth0-vue'
import { mdiAccountCircle } from '@quasar/extras/mdi-v7'

const { isAuthenticated, user } = useAuth0()
const avatar = computed(() => user?.value?.picture)
</script>

<style lang="scss" scoped>

</style>
