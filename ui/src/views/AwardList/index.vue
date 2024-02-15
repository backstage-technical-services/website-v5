<template>
  <q-page>
    <h1>Awards</h1>

    <div class="q-mx-auto">
      <div class="text-right">
        <q-btn
            class="q-ml-auto q-mr-md"
            color="positive"
            :icon="mdiPlus"
            :label="canCreate ? 'New award' : 'Suggest award'"
            :to="{ name: canCreate ? 'awards:add' : 'awards:suggest' }"
            v-if="canCreate || canSuggest"/>
      </div>

      <q-table
          :loading="awardState.isLoading"
          :loading-label="undefined"
          :rows="awardList"
          row-key="id"
          @request="onRequest"
          v-model:pagination="pagination"
          :rows-per-page-options="PAGINATION_OPTIONS"
          rows-per-page-label="Awards per page:"
          grid>
        <template #loading>
          <q-inner-loading color="primary" showing/>
        </template>

        <template #no-data>
          <div class="row full-width flex-center text-h6" v-if="!awardState.isLoading">
            <q-icon :name="mdiAlert" size="sm" class="q-mr-md"/>
            No data.
          </div>
        </template>

        <template #item="props">
          <award-card :award="props.row" @refresh="refresh"/>
        </template>
      </q-table>
    </div>
  </q-page>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { mdiAlert, mdiPlus } from '@quasar/extras/mdi-v7'

import AwardCard from './AwardCard.vue'
import type { QTableOnRequest } from '@/types/quasar'
import type { AwardResponse } from '@/api/domains/awards'
import { handleError, useApi } from '@/api'
import type { LoadableState } from '@/api'
import { useBreadcrumbs } from '@/composables/breadcrumbs'
import { DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE, PAGINATION_OPTIONS } from '@/config/pagination'
import type { PaginatedResponse } from '@/config/pagination'
import { permissions } from '@/config/auth'
import { can } from '@/helpers/auth'

const canCreate = computed(() => can(permissions.awards.add))
const canSuggest = computed(() => can(permissions.awards.suggest))

const awardState = reactive<LoadableState<PaginatedResponse<AwardResponse>>>({
  isLoading: true,
})
const awardList = computed(() => awardState.data?.items)
const pagination = ref({
  sortBy: 'name',
  descending: false,
  page: DEFAULT_PAGE_NUM,
  rowsPerPage: DEFAULT_PAGE_SIZE,
  rowsNumber: 1,
})

const api = useApi()
const fetchAwardsPage = async(pageNum: number, pageSize: number) => {
  awardState.isLoading = true

  return api.awards.list(Math.max(pageNum - 1, 0), Math.max(pageSize, 1))
    .then(data => {
      awardState.data = data

      pagination.value.page = data.page.pageIndex + 1
      pagination.value.rowsPerPage = data.page.pageSize
      pagination.value.rowsNumber = data.page.totalItems
    })
    .catch(handleError)
    .then(() => {
      awardState.isLoading = false
    })
}

const onRequest = (props: QTableOnRequest) => {
  fetchAwardsPage(props.pagination.page ?? DEFAULT_PAGE_NUM, props.pagination.rowsPerPage ?? DEFAULT_PAGE_SIZE)
}
const refresh = () => fetchAwardsPage(pagination.value.page, pagination.value.rowsPerPage)

onMounted(() => {
  const { setBreadcrumbs } = useBreadcrumbs()
  setBreadcrumbs('Awards')

  fetchAwardsPage(1, pagination.value.rowsPerPage)
})
</script>

<style lang="scss" scoped>
.q-table--grid {
  .q-item__section--avatar {
    min-width: 0 !important;
  }
}
</style>
