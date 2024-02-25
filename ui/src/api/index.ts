import axios from 'axios'
import type { AxiosResponse } from 'axios'

import type { ApiProps } from '@/api/types'
import auth from '@/config/auth'
import quotesApi from './domains/quotes'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
})
http.interceptors.request.use(async(config) => {
  if (auth.isAuthenticated.value) {
    try {
      config.headers.Authorization = `Bearer ${await auth.getAccessTokenSilently()}`
    } catch (e) {
      // TODO: report failure to fetch access token
    }
  }

  return config
})

const extractData = <T>({ data }: AxiosResponse<T>): T => data

const extractResourceId = async({ headers }: AxiosResponse<unknown>): Promise<number> => {
  const header = headers['resource-id']

  return header !== undefined ? Promise.resolve(parseInt(header)) : Promise.reject(new Error('Could not determine resource ID'))
}

const apiProps: ApiProps = {
  http,
  extractData,
  extractResourceId,
}
const api = {
  quotes: quotesApi(apiProps),
}

export type { ApiError, GenericApiResponse, LoadableState } from './types'
export { mapError, handleError } from './utils'
export const useApi = () => api
