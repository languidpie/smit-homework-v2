export interface ValidationError {
  field: string
  message: string
}

export interface ValidationResult {
  valid: boolean
  errors: Record<string, string>
}

// Part validation rules (matching backend constraints)
export function validatePart(data: {
  name: string
  type: string
  location: string
  quantity: number
  condition: string
}): ValidationResult {
  const errors: Record<string, string> = {}

  // Name: required, not blank
  if (!data.name || data.name.trim() === '') {
    errors.name = 'Name is required'
  } else if (data.name.length > 255) {
    errors.name = 'Name must be less than 255 characters'
  }

  // Type: required
  if (!data.type) {
    errors.type = 'Type is required'
  }

  // Location: required, not blank
  if (!data.location || data.location.trim() === '') {
    errors.location = 'Location is required'
  } else if (data.location.length > 255) {
    errors.location = 'Location must be less than 255 characters'
  }

  // Quantity: must be >= 1
  if (data.quantity === null || data.quantity === undefined) {
    errors.quantity = 'Quantity is required'
  } else if (data.quantity < 1) {
    errors.quantity = 'Quantity must be at least 1'
  } else if (!Number.isInteger(data.quantity)) {
    errors.quantity = 'Quantity must be a whole number'
  }

  // Condition: required
  if (!data.condition) {
    errors.condition = 'Condition is required'
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors
  }
}

// Vinyl record validation rules (matching backend constraints)
export function validateRecord(data: {
  title: string
  artist: string
  releaseYear: number
  genre: string
  condition: string
}): ValidationResult {
  const errors: Record<string, string> = {}
  const currentYear = new Date().getFullYear()

  // Title: required, not blank
  if (!data.title || data.title.trim() === '') {
    errors.title = 'Title is required'
  } else if (data.title.length > 255) {
    errors.title = 'Title must be less than 255 characters'
  }

  // Artist: required, not blank
  if (!data.artist || data.artist.trim() === '') {
    errors.artist = 'Artist is required'
  } else if (data.artist.length > 255) {
    errors.artist = 'Artist must be less than 255 characters'
  }

  // Release year: required, between 1900 and current year
  if (data.releaseYear === null || data.releaseYear === undefined) {
    errors.releaseYear = 'Release year is required'
  } else if (data.releaseYear < 1900) {
    errors.releaseYear = 'Release year must be 1900 or later'
  } else if (data.releaseYear > currentYear) {
    errors.releaseYear = `Release year cannot be in the future (max: ${currentYear})`
  } else if (!Number.isInteger(data.releaseYear)) {
    errors.releaseYear = 'Release year must be a whole number'
  }

  // Genre: required
  if (!data.genre) {
    errors.genre = 'Genre is required'
  }

  // Condition: required
  if (!data.condition) {
    errors.condition = 'Condition is required'
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors
  }
}

// Helper to get error message for display
export function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    // Check if it's our ApiException
    if ('userMessage' in error && typeof (error as any).userMessage === 'string') {
      return (error as any).userMessage
    }
    return error.message
  }
  return 'An unexpected error occurred'
}

// Helper to get validation errors from ApiException
export function getValidationErrors(error: unknown): Record<string, string> | null {
  if (error && typeof error === 'object' && 'validationErrors' in error) {
    return (error as any).validationErrors || null
  }
  return null
}
