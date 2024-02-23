import type { ApiProps } from '@/api/types'
import type { PaginatedResponse } from '@/config/pagination'
import { DateTime } from 'luxon'
import { fromApiDate, toApiDate } from '@/config/dates'

export type QuoteVoteType = 'UPVOTE' | 'DOWNVOTE'
export type QuoteVoteAction = 'ADDED' | 'CHANGED' | 'REMOVED'

export type CreateQuoteRequest = {
  culprit: string
  quote: string
  date: DateTime
}
type RawCreateQuoteRequest = Omit<CreateQuoteRequest, 'date'> & {
  date: string
}

export type DefaultQuoteResponse = {
  id: number
  culprit: string
  quote: string
  date: DateTime
  rating: number
  userVote?: QuoteVoteType
}
type RawDefaultQuoteResponse = Omit<DefaultQuoteResponse, 'date' | 'userVote'> & {
  date: string
  userVote: QuoteVoteType | null
}

export type QuoteVoteResponse = {
  action: QuoteVoteAction
}

export default function({ http, extractData, extractResourceId }: ApiProps) {
  const basePath = '/quote'

  return {
    list: async(pageIndex: number, pageSize: number = 20): Promise<PaginatedResponse<DefaultQuoteResponse>> => http
      .get<PaginatedResponse<RawDefaultQuoteResponse>>(basePath, {
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

    upvote: async(id: number): Promise<QuoteVoteAction> => http.patch<QuoteVoteResponse>(`${basePath}/${id}/upvote`)
      .then(extractData)
      .then(({ action }) => action),

    downvote: async(id: number): Promise<QuoteVoteAction> => http.patch<QuoteVoteResponse>(`${basePath}/${id}/downvote`)
      .then(extractData)
      .then(({ action }) => action),

    delete: async(id: number): Promise<void> => http.delete(`${basePath}/${id}`),
  }
}
