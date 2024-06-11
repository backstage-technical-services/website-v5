<template>
  <q-page>
    <h1>The Committee</h1>

    <div class="q-mx-auto">
      <q-table
          :loading="committeeState.isLoading"
          :loading-label="undefined"
          :rows="committeeRoles"
          row-key="id"
          :pagination="{rowsPerPage: 0}"
          hide-pagination
          grid>
        <template #loading>
          <q-inner-loading color="primary" showing />
        </template>

        <template #no-data>
          <TableNoData
              header="We don't have any committee roles"
              subheader="How has everything not immediately collapsed?"
              v-if="!committeeState.isLoading" />
        </template>

        <template #item="props">
          <role :role="props.row" />
        </template>
      </q-table>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useHead } from '@unhead/vue'
import { handleError, type LoadableState, useApi } from '@/api'
import type { CommitteeResponse } from '@/api/domains/committee'
import { useBreadcrumbs } from '@/composables/breadcrumbs'
import TableNoData from '@/components/TableNoData.vue'
import Role from './Role.vue'

const committeeState = reactive<LoadableState<CommitteeResponse>>({
  isLoading: true,
})
const committeeRoles = computed(() => committeeState.data?.roles)

const api = useApi()
const fetchCommitteeRoles = async() => {
  committeeState.isLoading = true

  return api.committee.list()
    .then(data => {
      committeeState.data = data
    })
    .catch(handleError)
    .then(() => {
      committeeState.isLoading = false
    })
}

onMounted(() => {
  useHead({
    title: 'Committee',
  })

  const { setBreadcrumbs } = useBreadcrumbs()
  setBreadcrumbs('Committee')

  fetchCommitteeRoles()
})

</script>
