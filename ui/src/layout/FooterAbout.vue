<template>
  <div v-for="(line, index) in lines" :key="index">
    <q-icon class="q-mr-sm" size="xs" :name="line.icon" />

    <a v-if="line.link !== undefined" :href="line.link">{{ line.text }}</a>
    <span v-else>{{ line.text }}</span>
  </div>
</template>

<script lang="ts" setup>
import { mdiEmail, mdiPhone, mdiHome, mdiMapMarker } from '@quasar/extras/mdi-v7'

type AboutLine = {
  text: string
  link?: string
  icon: string
}
const lines: AboutLine[] = [
  {
    text: 'info@bts-crew.com',
    link: 'mailto:info@bts-crew.com',
    icon: mdiEmail,
  },
  {
    text: '+44 (0) 1225 383067',
    icon: mdiPhone,
  },
  {
    text: '1E 3.4',
    icon: mdiHome,
  },
  {
    text: 'The SU, University of Bath, Bath, BA2 7AY',
    icon:mdiMapMarker,
  },
]
</script>

<style lang="scss" scoped>
div {
  line-height: 1.5;
}
</style>