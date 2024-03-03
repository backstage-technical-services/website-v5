export type QPagination = {
  sortBy?: string
  descending?: boolean
  page?: number
  rowsPerPage?: number
}
export type QPaginatorProps = {
  pagination: QPagination
  pagesNumber: number
  isFirstPage: boolean
  isLastPage: boolean

  firstPage: () => void
  prevPage: () => void
  nextPage: () => void
  lastPage: () => void

  isFullscreen: boolean
  toggleFullscreen: () => void
}
