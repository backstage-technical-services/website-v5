<template>
  <template v-if="isHeader">
    <q-separator spaced  />

    <q-item-label header>{{ item.text }}</q-item-label>

    <drawer-menu-item v-for="child in item.children" :key="child.id" :item="child" />
  </template>
  <template v-else>
    <q-item :to="item.link" clickable v-ripple>
      <q-item-section avatar v-if="item.icon">
        <q-icon :name="item.icon" />
      </q-item-section>
      <q-item-section>
        <q-item-label>{{ item.text }}</q-item-label>
      </q-item-section>
    </q-item>
  </template>
</template>

<script lang="ts" setup>
import type { MainMenuItem } from '@/data/menu'
import { computed } from 'vue'

const props = defineProps<{
  item: MainMenuItem
}>()

const isHeader = computed(() => props.item.children !== undefined && props.item.children.length > 0)
</script>

<style lang="scss" scoped>

</style>