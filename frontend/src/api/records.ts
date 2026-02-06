import { apiGet, apiPost, apiPut, apiDelete } from './client'
import type { VinylRecord, RecordCreateRequest, RecordUpdateRequest, Genre } from '@/types/record'

export const recordsApi = {
  getAll(): Promise<VinylRecord[]> {
    return apiGet<VinylRecord[]>('/records')
  },

  getById(id: number): Promise<VinylRecord> {
    return apiGet<VinylRecord>(`/records/${id}`)
  },

  getByGenre(genre: Genre): Promise<VinylRecord[]> {
    return apiGet<VinylRecord[]>(`/records/genre/${genre}`)
  },

  search(query: string): Promise<VinylRecord[]> {
    return apiGet<VinylRecord[]>(`/records/search?q=${encodeURIComponent(query)}`)
  },

  create(data: RecordCreateRequest): Promise<VinylRecord> {
    return apiPost<RecordCreateRequest, VinylRecord>('/records', data)
  },

  update(id: number, data: RecordUpdateRequest): Promise<VinylRecord> {
    return apiPut<RecordUpdateRequest, VinylRecord>(`/records/${id}`, data)
  },

  delete(id: number): Promise<void> {
    return apiDelete(`/records/${id}`)
  }
}
