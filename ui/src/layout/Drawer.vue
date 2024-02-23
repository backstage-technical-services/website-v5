<template>
  <q-drawer :model-value="showDrawer" @update:model-value="handleSetDrawer" overlay>
    <q-scroll-area style="height: calc(100% - 150px); margin-top: 150px;">
      <q-list padding>
        <account />

        <template v-if="isAuthenticated">
          <menu-item v-for="item in menu" :key="item.id" :item="item" />
        </template>
      </q-list>
    </q-scroll-area>

    <profile />
  </q-drawer>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useAuth0 } from '@auth0/auth0-vue'

import Profile from './DrawerProfile.vue'
import Account from './DrawerAccount.vue'
import MenuItem from './DrawerMenuItem.vue'
import { useMenu } from '@/data/menu'

defineProps<{
  showDrawer: boolean
}>()

const {
  isAuthenticated,
} = useAuth0()

const menu = computed(() => useMenu())

const emit = defineEmits(['setDrawer'])
const handleSetDrawer = (val: boolean) => {
  emit('setDrawer', [val])
}
</script>

<style lang="scss" scoped>
:deep(.q-drawer) {
  .q-list {
    font-size: 0.9375rem;
  }
}
</style>
