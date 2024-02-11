import type { AxiosInstance, AxiosResponse } from 'axios'

export type GenericApiResponse = Record<string, any>

export type ApiError = {
  statusCode?: number
  text: string
}

export type LoadableState<T> = {
  isLoading: boolean
  error?: ApiError
  data?: T
}

export type ApiProps = {
  http: AxiosInstance
  extractData: <T>(response: AxiosResponse<T>) => T
  extractResourceId: (response: AxiosResponse<unknown>) => Promise<number>
}
