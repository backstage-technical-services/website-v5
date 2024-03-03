import type { PaginatedResponse } from '@/config/pagination'

const range = (start: number, end: number) => Array
  .from({ length: end - start + 1 }, (_, i) => start + i)

export const generatePageList = (currentPage: number, totalPages: number, pagesToShow: number = 5) => {
  if (pagesToShow < 1) {
    return []
  }

  const lowerPageCount = Math.ceil((pagesToShow - 1) / 2)
  const upperPageCount = Math.max(pagesToShow - 1 - lowerPageCount, 0)

  const initialPageNum = Math.max(currentPage - lowerPageCount, 1)
  const finalPageNum = Math.min(currentPage + upperPageCount, totalPages)

  return range(initialPageNum, finalPageNum)
}

export const fetchPaginatedList = async<R>(
  pageNum: number,
  pageSize: number,
  fn: (pageIndex: number, pageSize: number) => Promise<PaginatedResponse<R>>,
) => fn(Math.max(pageNum - 1, 0), Math.max(pageSize, 1))
