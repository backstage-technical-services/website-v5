<template>
  <div class="row full-width flex-center">
    <q-icon :name="mdiAlert" color="warning" size="xl" class="q-mr-lg" />

    <div>
      <div class="text-h3 text-bts-gold" style="margin: 0;">
        {{ header }}
      </div>
      <div class="text-h4 text-grey" style="margin: 0;" v-if="subheader">
        {{ subheader }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { mdiAlert } from '@quasar/extras/mdi-v7'

const props = defineProps<{
  header: string
  subheader?: string
}>()
</script>
