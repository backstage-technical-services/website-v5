import type { ApiProps } from '@/api/types'
import type { PaginatedResponse } from '@/config/pagination'
import { DateTime } from 'luxon'
import { fromApiDate, toApiDate } from '@/config/dates'

export type QuoteLikeType = 'LIKE' | 'DISLIKE'

export type CreateQuoteRequest = {
  culprit: string
  quote: string
  date: DateTime
}
type RawCreateQuoteRequest = Omit<CreateQuoteRequest, 'date'> & {
  date: string
}

export type QuoteResponse = {
  id: number
  culprit: string
  quote: string
  date: DateTime
  rating: number
  userVote?: QuoteLikeType
}
type RawQuoteResponse = Omit<QuoteResponse, 'date' | 'userVote'> & {
  date: string
  userVote: QuoteLikeType | null
}

export default function({ http, extractData, extractResourceId }: ApiProps) {
  const basePath = '/quote'

  return {
    list: async(pageIndex: number, pageSize: number = 20): Promise<PaginatedResponse<QuoteResponse>> => http
      .get<PaginatedResponse<RawQuoteResponse>>(basePath, {
        params: {
          pageIndex,
          pageSize,
        },
      })
      .then(extractData)
      .then(response => ({
        ...response,
        items: response.items.map(quote => ({
          ...quote,
          date: fromApiDate(quote.date),
          userVote: quote.userVote ?? undefined,
        })),
      })),

    create: async(request: CreateQuoteRequest): Promise<number> => {
      const rawRequest: RawCreateQuoteRequest = {
        ...request,
        date: toApiDate(request.date),
      }

      return http.post(basePath, rawRequest)
        .then(extractResourceId)
    },

    like: async(id: number): Promise<void> => http.patch(`${basePath}/${id}/like`),

    dislike: async(id: number): Promise<void> => http.patch(`${basePath}/${id}/dislike`),

    delete: async(id: number): Promise<void> => http.delete(`${basePath}/${id}`),
  }
}
