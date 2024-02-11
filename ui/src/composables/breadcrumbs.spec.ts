import { describe, expect, it, vi } from 'vitest'
import { useBreadcrumbs } from './breadcrumbs'
import router from '@/router'

describe('useBreadcrumbs', () => {
  it('can set breadcrumbs', () => {
    const {
      breadcrumbs,
      setBreadcrumbs,
    } = useBreadcrumbs()

    setBreadcrumbs({ text: 'One', link: '/link' }, 'Two')

    expect(breadcrumbs.value).to.have.length(2)
    expect(breadcrumbs.value[0]).toEqual({ text: 'One', link: '/link' })
    expect(breadcrumbs.value[1]).toEqual({ text: 'Two' })
  })

  it('resets the breadcrumbs when navigating to a new page', async() => {
    expect.assertions(1)
    // @ts-ignore
    window.scrollTo = vi.fn()

    const {
      breadcrumbs,
      setBreadcrumbs,
    } = useBreadcrumbs()

    setBreadcrumbs('One')

    await router.push('/')

    expect(breadcrumbs.value).to.have.length(0)
  })
})
