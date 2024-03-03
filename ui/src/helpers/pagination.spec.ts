import { afterEach, describe, expect, it, vi } from 'vitest'
import { fetchPaginatedList, generatePageList } from './pagination'

describe('generating a page list', () => {
  it('should generate a list of the correct size', () => {
    const currentPage = 6
    const totalPages = 10
    const pagesToShow = 7

    const expectedPageList: number[] = [3, 4, 5, 6, 7, 8, 9]
    const pageList = generatePageList(currentPage, totalPages, pagesToShow)

    expect(pageList).toStrictEqual(expectedPageList)
  })

  it('should handle even page numbers', () => {
    const currentPage = 6
    const totalPages = 10
    const pagesToShow = 8

    const expectedPageList: number[] = [2, 3, 4, 5, 6, 7, 8, 9]
    const pageList = generatePageList(currentPage, totalPages, pagesToShow)

    expect(pageList).toStrictEqual(expectedPageList)
  })

  it('should not go below 1', () => {
    const currentPage = 1
    const totalPages = 10
    const pagesToShow = 7

    const expectedPageList: number[] = [1, 2, 3, 4]
    const pageList = generatePageList(currentPage, totalPages, pagesToShow)

    expect(pageList).toStrictEqual(expectedPageList)
  })

  it('should not go above the total number of pages', () => {
    const currentPage = 10
    const totalPages = 10
    const pagesToShow = 7

    const expectedPageList: number[] = [7, 8, 9, 10]
    const pageList = generatePageList(currentPage, totalPages, pagesToShow)

    expect(pageList).toStrictEqual(expectedPageList)
  })

  it('should return an empty list for no pages to show', () => {
    const pageList = generatePageList(1, 100, 0)
    expect(pageList).toHaveLength(0)
  })

  it('should return an empty list for a negative number of pages to show', () => {
    const pageList = generatePageList(1, 100, -1)
    expect(pageList).toHaveLength(0)
  })

  it('should handle when there\'s only a single page', () => {
    const currentPage = 1
    const totalPages = 1
    const pagesToShow = 7

    const expectedPageList: number[] = [1]
    const pageList = generatePageList(currentPage, totalPages, pagesToShow)

    expect(pageList).toStrictEqual(expectedPageList)
  })
})

describe('fetching paginated lists', () => {
  const fn = vi.fn()

  afterEach(() => {
    vi.resetAllMocks()
  })

  it('should call the function with the correct parameters', async() => {
    expect.assertions(2)

    await fetchPaginatedList(1, 50, fn)

    expect(fn).toHaveBeenCalledOnce()
    expect(fn).toHaveBeenCalledWith(0, 50)
  })

  it('should prevent a negative page index', async() => {
    expect.assertions(1)

    await fetchPaginatedList(-1, 50, fn)

    expect(fn).toHaveBeenCalledWith(0, 50)
  })

  it('should enforce a positive page size', async() => {
    expect.assertions(1)

    await fetchPaginatedList(1, 0, fn)

    expect(fn).toHaveBeenCalledWith(0, 1)
  })
})
