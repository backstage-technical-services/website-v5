<template>
  <q-btn-group>
    <!-- First page -->
    <q-btn
        v-if="scope.pagesNumber > 2"
        :icon="mdiPageFirst"
        color="bts-yellow"
        text-color="black"
        :disable="scope.isFirstPage"
        @click="scope.firstPage"
        data-cy="pagination-first-page"
        dense />
    <!-- Previous page -->
    <q-btn
        :icon="mdiChevronLeft"
        color="bts-yellow"
        text-color="black"
        :disable="scope.isFirstPage"
        @click="scope.prevPage"
        data-cy="pagination-prev-page"
        dense />
    <!-- Page numbers -->
    <q-btn
        v-for="page in pageList"
        :key="page"
        color="bts-yellow"
        text-color="black"
        :label="page"
        :disable="page === scope.pagination.page"
        @click="goToPage(page, scope.pagination.rowsPerPage ?? 1)"
        data-cy="pagination-choose-page" />
    <!-- Next page -->
    <q-btn
        :icon="mdiChevronRight"
        color="bts-yellow"
        text-color="black"
        :disable="scope.isLastPage"
        @click="scope.nextPage"
        data-cy="pagination-next-page"
        dense />
    <!-- Last page -->
    <q-btn
        v-if="scope.pagesNumber > 2"
        :icon="mdiPageLast"
        color="bts-yellow"
        text-color="black"
        :disable="scope.isLastPage"
        @click="scope.lastPage"
        data-cy="pagination-last-page"
        dense />
  </q-btn-group>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { mdiChevronLeft, mdiChevronRight, mdiPageFirst, mdiPageLast } from '@quasar/extras/mdi-v7'
import type { QPaginatorProps } from '@/types/quasar'
import { generatePageList } from '@/helpers/pagination'

const props = defineProps<{
  goToPage: (pageNum: number, pageSize: number) => void
  scope: QPaginatorProps
}>()

const pageList = computed(() => generatePageList(props.scope.pagination.page, props.scope.pagesNumber))
</script>
