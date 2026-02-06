export type PartType = 'FRAME' | 'BRAKE' | 'TIRE' | 'PUMP' | 'OTHER'

export type PartCondition = 'NEW' | 'EXCELLENT' | 'GOOD' | 'FAIR' | 'POOR'

export interface Part {
  id: number
  name: string
  description: string | null
  type: PartType
  location: string
  quantity: number
  condition: PartCondition
  notes: string | null
  createdAt: string
  updatedAt: string
}

export interface PartCreateRequest {
  name: string
  description?: string
  type: PartType
  location: string
  quantity: number
  condition: PartCondition
  notes?: string
}

export interface PartUpdateRequest {
  name?: string
  description?: string
  type?: PartType
  location?: string
  quantity?: number
  condition?: PartCondition
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

export const PART_TYPES: { value: PartType; label: string }[] = [
  { value: 'FRAME', label: 'Frame' },
  { value: 'BRAKE', label: 'Brake' },
  { value: 'TIRE', label: 'Tire' },
  { value: 'PUMP', label: 'Pump' },
  { value: 'OTHER', label: 'Other' }
]

export const PART_CONDITIONS: { value: PartCondition; label: string }[] = [
  { value: 'NEW', label: 'New' },
  { value: 'EXCELLENT', label: 'Excellent' },
  { value: 'GOOD', label: 'Good' },
  { value: 'FAIR', label: 'Fair' },
  { value: 'POOR', label: 'Poor' }
]
