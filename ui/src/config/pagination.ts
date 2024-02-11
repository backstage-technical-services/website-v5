export type PageInfo = {
  pageIndex: number
  pageSize: number
  totalPages: number
  totalItems: number
}

export type PaginatedResponse<T> = {
  page: PageInfo
  items: T[]
}

export const DEFAULT_PAGE_NUM = 1
export const DEFAULT_PAGE_SIZE = 20

export const PAGINATION_OPTIONS = [5, 10, 20, 50, 100]
