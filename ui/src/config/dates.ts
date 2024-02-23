import { DateTime } from 'luxon'

export const dateTimeFormat = 'dd/MM/yyyy HH:mm'
export const dateTimeMask = 'DD/MM/YYYY HH:mm'

export const fromApiDate = (date: string, format: string = 'yyyy-MM-dd HH:mm:ss'): DateTime => DateTime.fromFormat(date, format)
export const toApiDate = (date: DateTime, format: string = 'yyyy-MM-dd HH:mm:ss'): string => date.toFormat(format)
