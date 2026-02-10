const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

let authHeaderProvider: (() => string | null) | null = null

export function setAuthHeaderProvider(provider: () => string | null): void {
  authHeaderProvider = provider
}

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
    if (this.status === 401) {
      return 'Authentication required. Please log in.'
    }
    if (this.status === 403) {
      return 'Access denied. You do not have permission to access this resource.'
    }
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

function getHeaders(): HeadersInit {
  const headers: HeadersInit = {
    'Content-Type': 'application/json'
  }

  const authHeader = authHeaderProvider?.()
  if (authHeader) {
    headers['Authorization'] = authHeader
  }

  return headers
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    if (response.status === 401) {
      window.dispatchEvent(new CustomEvent('auth:unauthorized'))
    }

    let apiError: ApiError

    try {
      const errorBody = await response.json()

      // Parse validation errors from either custom ErrorResponse format ({errors: {field: msg}})
      // or Micronaut default format ({_embedded: {errors: [{path, message}]}})
      let validationErrors: Record<string, string> | undefined
      if (errorBody.errors && typeof errorBody.errors === 'object' && !Array.isArray(errorBody.errors)) {
        validationErrors = errorBody.errors
      } else if (errorBody._embedded?.errors) {
        validationErrors = errorBody._embedded.errors.reduce(
          (acc: Record<string, string>, err: { path?: string; message: string }) => {
            if (err.path) {
              acc[err.path] = err.message
            }
            return acc
          },
          {}
        )
      }

      apiError = {
        status: response.status,
        error: errorBody.error || response.statusText,
        message: errorBody.message || `Request failed with status ${response.status}`,
        path: errorBody.path,
        timestamp: errorBody.timestamp,
        validationErrors
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
  const response = await fetch(`${API_BASE}${endpoint}`, {
    headers: getHeaders()
  })
  return handleResponse<T>(response)
}

export async function apiPost<T, R>(endpoint: string, data: T): Promise<R> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(data)
  })
  return handleResponse<R>(response)
}

export async function apiPut<T, R>(endpoint: string, data: T): Promise<R> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'PUT',
    headers: getHeaders(),
    body: JSON.stringify(data)
  })
  return handleResponse<R>(response)
}

export async function apiDelete(endpoint: string): Promise<void> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'DELETE',
    headers: getHeaders()
  })
  return handleResponse<void>(response)
}
