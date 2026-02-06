export type Genre =
  | 'ROCK'
  | 'JAZZ'
  | 'BLUES'
  | 'CLASSICAL'
  | 'ELECTRONIC'
  | 'POP'
  | 'HIP_HOP'
  | 'COUNTRY'
  | 'FOLK'
  | 'SOUL'
  | 'PUNK'
  | 'METAL'
  | 'OTHER'

export type RecordCondition =
  | 'MINT'
  | 'NEAR_MINT'
  | 'EXCELLENT'
  | 'VERY_GOOD'
  | 'GOOD'
  | 'FAIR'
  | 'POOR'

export interface VinylRecord {
  id: number
  title: string
  artist: string
  releaseYear: number
  genre: Genre
  purchaseSource: string | null
  purchaseDate: string | null
  condition: RecordCondition
  notes: string | null
  createdAt: string
  updatedAt: string
}

export interface RecordCreateRequest {
  title: string
  artist: string
  releaseYear: number
  genre: Genre
  purchaseSource?: string
  purchaseDate?: string
  condition: RecordCondition
  notes?: string
}

export interface RecordUpdateRequest {
  title?: string
  artist?: string
  releaseYear?: number
  genre?: Genre
  purchaseSource?: string
  purchaseDate?: string
  condition?: RecordCondition
  notes?: string
}

export interface Page<T> {
  content: T[]
  pageNumber: number
  pageSize: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export const GENRES: { value: Genre; label: string }[] = [
  { value: 'ROCK', label: 'Rock' },
  { value: 'JAZZ', label: 'Jazz' },
  { value: 'BLUES', label: 'Blues' },
  { value: 'CLASSICAL', label: 'Classical' },
  { value: 'ELECTRONIC', label: 'Electronic' },
  { value: 'POP', label: 'Pop' },
  { value: 'HIP_HOP', label: 'Hip Hop' },
  { value: 'COUNTRY', label: 'Country' },
  { value: 'FOLK', label: 'Folk' },
  { value: 'SOUL', label: 'Soul' },
  { value: 'PUNK', label: 'Punk' },
  { value: 'METAL', label: 'Metal' },
  { value: 'OTHER', label: 'Other' }
]

export const RECORD_CONDITIONS: { value: RecordCondition; label: string }[] = [
  { value: 'MINT', label: 'Mint' },
  { value: 'NEAR_MINT', label: 'Near Mint' },
  { value: 'EXCELLENT', label: 'Excellent' },
  { value: 'VERY_GOOD', label: 'Very Good' },
  { value: 'GOOD', label: 'Good' },
  { value: 'FAIR', label: 'Fair' },
  { value: 'POOR', label: 'Poor' }
]
