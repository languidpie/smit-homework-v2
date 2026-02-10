export interface Page<T> {
  content: T[]
  pageNumber: number
  pageSize: number
  totalElements: number
  totalPages: number
}

export type SortDirection = 'ASC' | 'DESC'
