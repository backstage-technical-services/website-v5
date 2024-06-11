<template>
  <div class="full-width row q-mb-lg">
    <div class="col-auto" style="width: 10em">
      <div>
        <q-img
            class="rounded-borders"
            ratio="1"
            sizes="100px"
            :placeholder-src="blankProfile"
            :error-src="blankProfile"
            src="/" />
      </div>

      <div class="role--email">
        <a>{{ role.email }}</a>
      </div>

    </div>

    <div class="col q-pl-lg">
      <div class="role--name">{{ role.name }}</div>
      <div class="role--held-by">{{ role.user || 'Unelected' }}</div>
      <div>{{ role.description }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CommitteeResponseRole } from '@/api/domains/committee'
import blankProfile from '@/assets/images/blank_profile.jpg'

const props = defineProps<{
  role: CommitteeResponseRole
}>()
</script>

<style scoped lang="scss">
.role--email {
  color: #666;
  font-size: 0.875rem;
  text-align: center;

  a {
    color: inherit;
  }
}
.role--name {
  color: var(--color-bts-orange);
  font-size: 1.4em;
  font-weight: bold;
}
.role--held-by {
  color: var(--color-bts-yellow);
  font-size: 1.1em;
  font-weight: 300;
}
</style>
