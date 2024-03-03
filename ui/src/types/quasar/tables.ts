import type { QPagination } from '@/types/quasar/pagination'

export type QTableOnRequest = {
  pagination: QPagination
  filter?: Function
}
