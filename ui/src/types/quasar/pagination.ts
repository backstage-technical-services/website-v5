export type QPagination = {
  sortBy: string | null
  descending: boolean
  page: number
  rowsPerPage: number
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

  inFullscreen: boolean
  toggleFullscreen: () => void
}
