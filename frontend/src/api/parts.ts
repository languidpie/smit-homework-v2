import { apiGet, apiPost, apiPut, apiDelete } from './client'
import type { Part, PartCreateRequest, PartUpdateRequest } from '@/types/part'

export const partsApi = {
  getAll(): Promise<Part[]> {
    return apiGet<Part[]>('/parts')
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
