import type { ApiError, GenericApiResponse } from '@/api/types'
import { useNotifications } from '@/composables/notifications'

const DEFAULT_ERROR_CODE = 500
const DEFAULT_ERROR_MESSAGE = 'Unknown error'

const DEFAULT_ERROR_MESSAGES: Record<number, string> = {
  401: 'You need to be logged in to do that',
  403: 'You do not have permission to do that',
  500: DEFAULT_ERROR_MESSAGE,
}

const { error: notifyError } = useNotifications()

const convertErrorMessage = (message: string): string => {
  const mappedErrors: Record<string, string> = {
    'Network Error': 'Could not contact the API',
  }

  return mappedErrors[message] || message
}

export const mapError = (error: GenericApiResponse): ApiError => {
  const statusCode = error.response ? (error.response.status || DEFAULT_ERROR_CODE) : undefined

  const defaultErrorMessage = statusCode && statusCode in DEFAULT_ERROR_MESSAGES ? DEFAULT_ERROR_MESSAGES[statusCode] : DEFAULT_ERROR_MESSAGE
  const errorMessage = error.response ? (error.response.data?.message || defaultErrorMessage) : error.message

  return {
    statusCode,
    text: convertErrorMessage(errorMessage),
  }
}

export const handleError = (error: GenericApiResponse) => {
  const mappedError = mapError(error)

  notifyError(mappedError.text)
}
