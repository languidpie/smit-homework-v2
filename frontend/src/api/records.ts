import { apiGet, apiPost, apiPut, apiDelete } from './client'
import type { VinylRecord, RecordCreateRequest, RecordUpdateRequest, Page } from '@/types/record'

export const recordsApi = {
  getAll(page = 0, size = 20, sort?: string, direction?: string): Promise<Page<VinylRecord>> {
    let url = `/records?page=${page}&size=${size}`
    if (sort) {
      url += `&sort=${sort}&direction=${direction}`
    }
    return apiGet<Page<VinylRecord>>(url)
  },

  getById(id: number): Promise<VinylRecord> {
    return apiGet<VinylRecord>(`/records/${id}`)
  },

  getByGenre(genre: string): Promise<VinylRecord[]> {
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
