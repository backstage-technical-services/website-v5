import { vi, beforeEach, describe, expect, it } from 'vitest'
import { useLayout } from './layout'
import router from '@/router'

describe('useLayout', () => {
  beforeEach(() => {
    const { reset } = useLayout()
    reset()
  })

  it('by default, header and footer should be visible', () => {
    const { isHeaderVisible,  isFooterVisible } = useLayout()
    expect(isHeaderVisible.value).toBeTruthy()
    expect(isFooterVisible.value).toBeTruthy()
  })

  it('should allow you to hide the header', () => {
    const {
      isHeaderVisible,
      isFooterVisible,
      hideHeader,
    } = useLayout()

    hideHeader()

    expect(isHeaderVisible.value).toBeFalsy()
    expect(isFooterVisible.value).toBeTruthy()
  })

  it('should allow you to hide the footer', () => {
    const {
      isHeaderVisible,
      isFooterVisible,
      hideFooter,
    } = useLayout()

    hideFooter()

    expect(isHeaderVisible.value).toBeTruthy()
    expect(isFooterVisible.value).toBeFalsy()
  })

  it('should reset the visibility when navigating', async() => {
    expect.assertions(2)

    // @ts-ignore
    window.scrollTo = vi.fn()

    const {
      isHeaderVisible,
      isFooterVisible,
      hideHeader,
      hideFooter,
    } = useLayout()
    hideHeader()
    hideFooter()
    
    await router.push('/new')

    expect(isHeaderVisible.value).toBeTruthy()
    expect(isFooterVisible.value).toBeTruthy()
  })
})
