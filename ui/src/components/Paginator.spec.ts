import { vi, describe, it, afterEach, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { installQuasarPlugin } from '@quasar/quasar-app-extension-testing-unit-vitest'
import Paginator from './Paginator.vue'
import type { QPaginatorProps } from '../types/quasar'

installQuasarPlugin()

const mockGoToPage = vi.fn()
const mockFirstPage = vi.fn()
const mockPrevPage = vi.fn()
const mockNextPage = vi.fn()
const mockLastPage = vi.fn()

const makeScope = (page: number = 5, numPages: number = 10): QPaginatorProps => ({
  pagination: {
    page,
    rowsPerPage: 20,
  },
  pagesNumber: numPages,
  isFirstPage: page === 1,
  isLastPage: page === numPages,

  firstPage: mockFirstPage,
  prevPage: mockPrevPage,
  nextPage: mockNextPage,
  lastPage: mockLastPage,

  isFullscreen: false,
  toggleFullscreen: vi.fn(),
})

describe('Paginator', () => {
  const mountComponent = (page: number = 5, numPages: number = 10) => mount(
    Paginator,
    {
      props: {
        goToPage: mockGoToPage,
        scope: makeScope(page, numPages),
      },
    },
  )

  afterEach(() => {
    vi.resetAllMocks()
  })

  it('should mount correctly', async() => {
    const wrapper = mountComponent()

    expect(wrapper).toBeTruthy()
  })

  it('should show all the buttons', async() => {
    const wrapper = mountComponent()

    expect(wrapper.find('.q-btn[data-cy=pagination-first-page]').exists()).toBeTruthy()
    expect(wrapper.find('.q-btn[data-cy=pagination-prev-page]').exists()).toBeTruthy()
    expect(wrapper.findAll('.q-btn[data-cy=pagination-choose-page]').map(c => c.text())).toEqual(['3', '4', '5', '6', '7'])
    expect(wrapper.find('.q-btn[data-cy=pagination-next-page]').exists()).toBeTruthy()
    expect(wrapper.find('.q-btn[data-cy=pagination-last-page]').exists()).toBeTruthy()
  })

  it('should disable the current page button', () => {
    const wrapper = mountComponent()

    const btn = wrapper.findAll('.q-btn[data-cy=pagination-choose-page]')
      .find(btn => btn.text() === '5')!!

    expect(btn.attributes()).toHaveProperty('disabled')
  })

  it('should not show the "go to first page" button when the number of pages is < 3', () => {
    const wrapper = mountComponent(1, 2)

    expect(wrapper.find('.q-btn[data-cy=pagination-first-page]').exists()).toBeFalsy()
  })

  it('should not show the "go to last page" button when the number of pages is < 3', () => {
    const wrapper = mountComponent(1, 2)

    expect(wrapper.find('.q-btn[data-cy=pagination-last-page]').exists()).toBeFalsy()
  })

  it('should disable the "go to first page" and "prev page" buttons if on the first page', () => {
    const wrapper = mountComponent(1)

    expect(wrapper.get('.q-btn[data-cy=pagination-first-page]').attributes()).toHaveProperty('disabled')
    expect(wrapper.get('.q-btn[data-cy=pagination-prev-page]').attributes()).toHaveProperty('disabled')
  })

  it('should disable the "go to last page" and "next page" buttons if on the last page', () => {
    const wrapper = mountComponent(5, 5)

    expect(wrapper.get('.q-btn[data-cy=pagination-next-page]').attributes()).toHaveProperty('disabled')
    expect(wrapper.get('.q-btn[data-cy=pagination-last-page]').attributes()).toHaveProperty('disabled')
  })

  it('should trigger the firstPage function when the button is clicked', () => {
    const wrapper = mountComponent()

    wrapper.get('.q-btn[data-cy=pagination-first-page]').trigger('click')

    expect(mockFirstPage).toHaveBeenCalled()
  })

  it('should trigger the prevPage function when the button is clicked', () => {
    const wrapper = mountComponent()

    wrapper.get('.q-btn[data-cy=pagination-prev-page]').trigger('click')

    expect(mockPrevPage).toHaveBeenCalled()
  })

  it('should trigger the nextPage function when the button is clicked', () => {
    const wrapper = mountComponent()

    wrapper.get('.q-btn[data-cy=pagination-next-page]').trigger('click')

    expect(mockNextPage).toHaveBeenCalled()
  })

  it('should trigger the lastPage function when the button is clicked', () => {
    const wrapper = mountComponent()

    wrapper.get('.q-btn[data-cy=pagination-last-page]').trigger('click')

    expect(mockLastPage).toHaveBeenCalled()
  })

  it('should trigger the goToPage function when the button is clicked', () => {
    const wrapper = mountComponent()

    wrapper.get('.q-btn[data-cy=pagination-choose-page]').trigger('click')

    expect(mockGoToPage).toHaveBeenCalledWith(3, 20)
  })
})
