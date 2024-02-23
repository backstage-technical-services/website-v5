<template>
  <q-page>
    <h1>Quotes Board</h1>

    <div class="q-mx-auto">
      <div class="text-right" v-if="canCreate">
        <q-btn
            class="q-ml-auto q-mr-md"
            color="positive"
            :icon="mdiPlus"
            label="Add quote"
            @click="showAddDialog" />
      </div>

      <q-table
        :loading="quoteState.isLoading"
        :loading-label="undefined"
        :rows="quoteList"
        row-key="id"
        @request="onRequest"
        v-model:pagination="pagination"
        :rows-per-page-options="PAGINATION_OPTIONS"
        rows-per-page-label="Quotes per page:"
        grid>
        <template #loading>
          <q-inner-loading color="primary" showing />
        </template>

        <template #no-data>
          <div class="row full-width flex-center" v-if="!quoteState.isLoading">
            <q-icon :name="mdiAlert" color="warning" size="xl" class="q-mr-lg" />

            <div>
              <div class="text-h3 text-bts-gold" style="margin: 0;">
                We don't seem to have any good quotes
              </div>
              <div class="text-h4 text-grey" style="margin: 0;">
                You guys need to start embarrassing yourselves
              </div>
            </div>
          </div>
        </template>

        <template #item="props">
          <quote :quote="props.row" @refresh="refresh" />
        </template>
      </q-table>
    </div>
  </q-page>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { mdiAlert, mdiPlus } from '@quasar/extras/mdi-v7'

import type { QTableOnRequest } from '@/types/quasar'
import type { DefaultQuoteResponse } from '@/api/domains/quotes'
import Quote from './Quote.vue'
import AddQuote from './AddQuote.vue'
import { can } from '@/helpers/auth'
import { permissions } from '@/config/auth'
import { handleError, type LoadableState, useApi } from '@/api'
import { DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE, type PaginatedResponse, PAGINATION_OPTIONS } from '@/config/pagination'
import { useBreadcrumbs } from '@/composables/breadcrumbs'
import { Dialog } from 'quasar'
import { useHead } from '@unhead/vue'

const canCreate = computed(() => can(permissions.quotes.add))

const quoteState = reactive<LoadableState<PaginatedResponse<DefaultQuoteResponse>>>({
  isLoading: true,
})
const quoteList = computed(() => quoteState.data?.items)
const pagination = ref({
  sortBy: 'date',
  descending: true,
  page: DEFAULT_PAGE_NUM,
  rowsPerPage: DEFAULT_PAGE_SIZE,
  rowsNumber: 1,
})

const showAddDialog = () => Dialog.create({
  component: AddQuote,
}).onOk(() => refresh())

const api = useApi()
const fetchQuotesPage = async(pageNum: number, pageSize: number) => {
  quoteState.isLoading = true

  return api.quotes.list(Math.max(pageNum - 1, 0), Math.max(pageSize, 1))
    .then(data => {
      quoteState.data = data

      pagination.value.page = data.page.pageIndex + 1
      pagination.value.rowsPerPage = data.page.pageSize
      pagination.value.rowsNumber = data.page.totalItems
    })
    .catch(handleError)
    .then(() => {
      quoteState.isLoading = false
    })
}

const onRequest = (props: QTableOnRequest) => {
  fetchQuotesPage(props.pagination.page ?? DEFAULT_PAGE_NUM, props.pagination.rowsPerPage ?? DEFAULT_PAGE_SIZE)
}
const refresh = () => fetchQuotesPage(pagination.value.page, pagination.value.rowsPerPage)

onMounted(() => {
  useHead({
    title: 'Quotes Board',
  })

  const { setBreadcrumbs } = useBreadcrumbs()
  setBreadcrumbs('Quotes Board')

  fetchQuotesPage(1, pagination.value.rowsPerPage)
})
</script>
