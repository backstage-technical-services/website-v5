<template>
  <q-page class="flex flex-center">
    <div class="relative-position" style="max-width: 600px;">
      <div v-if="code" class="error-code text-center text-weight-bold">{{ code }}</div>

      <div class="error-content absolute-top full-width text-weight-thin">
        <div>{{ details }}</div>

        <div class="text-center q-mt-md">
          <router-link to="/">
            <q-icon :name="mdiHome" size="xl" />
          </router-link>
          <a href="https://github.com/backstage-technical-services/website-v5/issues" target="_blank" title="Report issue">
            <q-icon :name="mdiGithub" size="xl" />
          </a>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script lang="ts" setup>
import { mdiHome, mdiGithub } from '@quasar/extras/mdi-v7'
import { useHead } from '@unhead/vue'
import { useLayout } from '@/composables/layout'

type Props = {
  code?: number
  details: string
}

const { code = 500, details } = defineProps<Props>()

useHead({
  title: `Error ${code}`,
})

const {
  hideHeader,
  hideFooter,
} = useLayout()
hideHeader()
hideFooter()
</script>

<style lang="scss" scoped>
@import "@/assets/css/mixins";

.error-code {
  color: rgba($color-bts-yellow-dark, 0.1);
  font-size: 23em;
}

.error-content {
  font-size: 2.5rem;
  padding: 2em 1em 1em;
  top: 3em;
  z-index: 500;

  a, a:visited {
    @include transition(opacity);
    color: #fff;
    margin: 0 0.25em;
    opacity: 0.5;

    &:hover {
      opacity: 1;
      text-decoration: none;
    }

    &:focus {
      outline: none;
    }
  }
}
</style>
