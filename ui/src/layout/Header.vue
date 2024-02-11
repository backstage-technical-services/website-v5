<template>
  <q-header>
    <q-toolbar class="q-px-md-none">
      <q-btn flat round :icon="mdiMenu" @click="showDrawer = !showDrawer" />

      <div class="container row no-wrap items-center">
        <q-toolbar-title class="full-height" shrink>
          <router-link to="/">
            <img alt="The logo of Backstage Technical Services" src="../assets/images/logo_transparent.png" />
          </router-link>
        </q-toolbar-title>

        <q-space />

        <q-btn class="q-mr-lg" label="Gallery" to="/gallery" flat />
        <q-btn class="q-mr-lg" label="Committee" to="/committee" flat />
        <q-btn color="primary" class="text-grey-9" label="Book Us" to="/book-us" />
      </div>
    </q-toolbar>
  </q-header>

  <drawer :show-drawer="showDrawer" @set-drawer="show => showDrawer = show" side="left" />
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { mdiMenu } from '@quasar/extras/mdi-v7'

import Drawer from './Drawer.vue'

const showDrawer = ref(false)
</script>

<style lang="scss" scoped>
$header-bar-height: 70px;

.q-header {
  background-color: #171717;
  color: #9e9e9e;
  display: flex;
  height: $header-bar-height;

  .q-toolbar__title {
    img {
      height: $header-bar-height;
    }
  }
}
</style>
