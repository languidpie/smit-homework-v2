const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

export interface ApiError {
  status: number
  error: string
  message: string
  path?: string
  timestamp?: string
  validationErrors?: Record<string, string>
}

export class ApiException extends Error {
  public readonly status: number
  public readonly error: string
  public readonly path?: string
  public readonly validationErrors?: Record<string, string>

  constructor(apiError: ApiError) {
    super(apiError.message)
    this.name = 'ApiException'
    this.status = apiError.status
    this.error = apiError.error
    this.path = apiError.path
    this.validationErrors = apiError.validationErrors
  }

  get userMessage(): string {
    if (this.status === 404) {
      return 'The requested item was not found. It may have been deleted.'
    }
    if (this.status === 400) {
      return this.message || 'Invalid data submitted. Please check your input.'
    }
    if (this.status === 409) {
      return 'A conflict occurred. The item may have been modified by someone else.'
    }
    if (this.status >= 500) {
      return 'A server error occurred. Please try again later.'
    }
    return this.message || 'An unexpected error occurred.'
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let apiError: ApiError

    try {
      const errorBody = await response.json()
      apiError = {
        status: response.status,
        error: errorBody.error || response.statusText,
        message: errorBody.message || `Request failed with status ${response.status}`,
        path: errorBody.path,
        timestamp: errorBody.timestamp,
        validationErrors: errorBody._embedded?.errors?.reduce(
          (acc: Record<string, string>, err: { path?: string; message: string }) => {
            if (err.path) {
              acc[err.path] = err.message
            }
            return acc
          },
          {}
        )
      }
    } catch {
      apiError = {
        status: response.status,
        error: response.statusText,
        message: `Request failed with status ${response.status}`
      }
    }

    throw new ApiException(apiError)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json()
}

export async function apiGet<T>(endpoint: string): Promise<T> {
  const response = await fetch(`${API_BASE}${endpoint}`)
  return handleResponse<T>(response)
}

export async function apiPost<T, R>(endpoint: string, data: T): Promise<R> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
  return handleResponse<R>(response)
}

export async function apiPut<T, R>(endpoint: string, data: T): Promise<R> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
  return handleResponse<R>(response)
}

export async function apiDelete(endpoint: string): Promise<void> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'DELETE'
  })
  return handleResponse<void>(response)
}
