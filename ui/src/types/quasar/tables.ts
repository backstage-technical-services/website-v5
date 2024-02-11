export type QTableOnRequest = {
  pagination: {
    sortBy?: string
    descending?: boolean
    page?: number
    rowsPerPage?: number
  }
  filter?: Function
}
