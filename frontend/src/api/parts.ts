import { apiGet, apiPost, apiPut, apiDelete } from './client'
import type { Part, PartCreateRequest, PartUpdateRequest, Page } from '@/types/part'

export const partsApi = {
  getAll(page = 0, size = 20, sort?: string, direction?: string): Promise<Page<Part>> {
    let url = `/parts?page=${page}&size=${size}`
    if (sort) {
      url += `&sort=${sort}&direction=${direction}`
    }
    return apiGet<Page<Part>>(url)
  },

  getById(id: number): Promise<Part> {
    return apiGet<Part>(`/parts/${id}`)
  },

  getByType(type: string): Promise<Part[]> {
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
