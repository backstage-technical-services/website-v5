import type { ApiProps } from '../types'
import type { PaginatedResponse } from '@/config/pagination'

export type CreateAwardRequest = {
  name: string
  description: string
  recurring: boolean
}

export type UpdateAwardRequest = {
  name: string
  description: string
  recurring: boolean
}

export type AwardResponse = {
  id: number
  name: string
  description: string
  recurring: boolean
  suggestedBy?: string
  approved: boolean
}

export default function({ http, extractData, extractResourceId }: ApiProps) {
  const basePath = '/award'

  return {
    list: async(pageIndex: number, pageSize: number = 50): Promise<PaginatedResponse<AwardResponse>> => http
      .get<PaginatedResponse<AwardResponse>>(basePath, {
        params: {
          pageIndex,
          pageSize,
        },
      })
      .then(extractData),

    create: async(request: CreateAwardRequest): Promise<number> => http
      .post(basePath, request)
      .then(extractResourceId),

    suggest: async(request: CreateAwardRequest): Promise<number> => http
      .post(`${basePath}/suggest`, request)
      .then(extractResourceId),

    get: async(id: number): Promise<AwardResponse> => http
      .get<AwardResponse>(`${basePath}/${id}`)
      .then(extractData),

    update: async(id: number, request: UpdateAwardRequest): Promise<void> => http.patch(`${basePath}/${id}`, request),

    approve: async(id: number): Promise<void> => http.patch(`${basePath}/${id}/approve`),

    delete: async(id: number): Promise<void> => http.delete(`${basePath}/${id}`),
  }
}
