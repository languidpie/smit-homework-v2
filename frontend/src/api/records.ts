import { apiGet, apiPost, apiPut, apiDelete } from './client'
import type { VinylRecord, RecordCreateRequest, RecordUpdateRequest, Page } from '@/types/record'

export const recordsApi = {
  getAll(page = 0, size = 20): Promise<Page<VinylRecord>> {
    return apiGet<Page<VinylRecord>>(`/records?page=${page}&size=${size}`)
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
