import { describe, expect, it } from 'vitest'
import { DateTime } from 'luxon'
import { fromApiDate, toApiDate } from './dates'

describe('converting API dates', () => {
  const expectedDateString = '2024-02-23 21:11:56'
  const expectedDate = DateTime.local(2024, 2, 23, 21, 11, 56)

  it('should convert strings from the API to DateTime objects', () => {
    const date = fromApiDate(expectedDateString)

    expect(date.isValid).toBeTruthy()
    expect(date).toEqual(expectedDate)
  })

  it('should convert DateTime objects to the correct datetime format', () => {
    const dateString = toApiDate(expectedDate)

    expect(dateString).toEqual(expectedDateString)
  })
})
