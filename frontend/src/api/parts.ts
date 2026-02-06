import { apiGet, apiPost, apiPut, apiDelete } from './client'
import type { Part, PartCreateRequest, PartUpdateRequest, PartType } from '@/types/part'

export const partsApi = {
  getAll(): Promise<Part[]> {
    return apiGet<Part[]>('/parts')
  },

  getById(id: number): Promise<Part> {
    return apiGet<Part>(`/parts/${id}`)
  },

  getByType(type: PartType): Promise<Part[]> {
    return apiGet<Part[]>(`/parts/type/${type}`)
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
