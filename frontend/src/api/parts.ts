import { apiGet, apiPost, apiPut, apiDelete } from './client'
import type { Part, PartCreateRequest, PartUpdateRequest, Page } from '@/types/part'

export const partsApi = {
  getAll(page = 0, size = 20): Promise<Page<Part>> {
    return apiGet<Page<Part>>(`/parts?page=${page}&size=${size}`)
  },

  search(query: string): Promise<Part[]> {
    return apiGet<Part[]>(`/parts/search?q=${encodeURIComponent(query)}`)
  },

  create(data: PartCreateRequest): Promise<Part> {
    return apiPost<PartCreateRequest, Part>('/parts', data)
  },

  update(id: number, data: PartUpdateRequest): Promise<Part> {
    return apiPut<PartUpdateRequest, Part>(`/parts/${id}`, data)
  },

  delete(id: number): Promise<void> {
    return apiDelete(`/parts/${id}`)
  }
}
