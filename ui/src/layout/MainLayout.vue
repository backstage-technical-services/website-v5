<template>
    <q-layout view="hHh LpR lfr">
      <app-header v-if="isHeaderVisible" />

      <q-page-container>
        <q-breadcrumbs class="container q-mt-md" v-if="breadcrumbs.length > 0">
          <q-breadcrumbs-el :icon="mdiHome" to="/" class="text-grey-6" />
          <q-breadcrumbs-el
              v-for="(crumb, index) in breadcrumbs"
              :key="index"
              :label="crumb.text"
              :to="crumb.link"
              class="text-grey-6" />
        </q-breadcrumbs>

        <router-view />
      </q-page-container>

      <app-footer v-if="isFooterVisible" />
    </q-layout>
</template>

<script lang="ts" setup>
import AppHeader from './Header.vue'
import AppFooter from './Footer.vue'
import { useBreadcrumbs } from '@/composables/breadcrumbs'
import { mdiHome } from '@quasar/extras/mdi-v7'
import { useLayout } from '@/composables/layout'

const { breadcrumbs } = useBreadcrumbs()
const {
  isHeaderVisible,
  isFooterVisible,
} = useLayout()
</script>

<style lang="scss" scoped>
@import '@/assets/css/mixins';

.q-page-container {
  font-weight: 400;

  & > .q-page {
    padding: 2em 0;
  }

  :deep(.container) {
    padding-left: 1em;
    padding-right: 1em;

    @include breakpoint-from(sm) {
      padding-left: 0;
      padding-right: 0;
    }
  }
}
</style>
