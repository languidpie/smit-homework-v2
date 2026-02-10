import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { usePartsStore } from '@/stores/parts'
import type { Part } from '@/types/part'

const mockParts: Part[] = [
  {
    id: 1,
    name: 'Shimano Brake',
    description: 'High quality brake',
    type: 'BRAKE',
    location: 'Garage',
    quantity: 2,
    condition: 'NEW',
    notes: null,
    createdAt: '2024-01-01T10:00:00',
    updatedAt: '2024-01-01T10:00:00'
  },
  {
    id: 2,
    name: 'Carbon Frame',
    description: 'Lightweight frame',
    type: 'FRAME',
    location: 'Basement',
    quantity: 1,
    condition: 'EXCELLENT',
    notes: null,
    createdAt: '2024-01-02T10:00:00',
    updatedAt: '2024-01-02T10:00:00'
  }
]

describe('Parts Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.resetAllMocks()
  })

  it('initializes with empty parts', () => {
    const store = usePartsStore()
    expect(store.parts).toEqual([])
    expect(store.totalParts).toBe(0)
  })

  it('fetches all parts', async () => {
    vi.mocked(global.fetch).mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve({
        content: mockParts,
        pageNumber: 0,
        pageSize: 20,
        totalElements: 2,
        totalPages: 1
      })
    } as Response)

    const store = usePartsStore()
    await store.fetchAll()

    expect(store.parts).toHaveLength(2)
    expect(store.totalParts).toBe(2)
  })

  it('filters parts by type via server', async () => {
    vi.mocked(global.fetch).mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve([mockParts[0]])
    } as Response)

    const store = usePartsStore()
    store.setTypeFilter('BRAKE')

    await new Promise(resolve => setTimeout(resolve))

    expect(store.filteredParts).toHaveLength(1)
    expect(store.filteredParts[0].type).toBe('BRAKE')
  })

  it('filters parts by search query', async () => {
    vi.mocked(global.fetch).mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve([mockParts[0]])
    } as Response)

    const store = usePartsStore()
    store.setSearchQuery('shimano')
    await store.searchServer('shimano')

    expect(store.filteredParts).toHaveLength(1)
    expect(store.filteredParts[0].name).toContain('Shimano')
  })

  it('gets part by id', () => {
    const store = usePartsStore()
    store.parts = mockParts

    const part = store.getPartById(1)

    expect(part?.name).toBe('Shimano Brake')
  })

  it('handles fetch error', async () => {
    vi.mocked(global.fetch).mockRejectedValueOnce(new Error('Network error'))

    const store = usePartsStore()
    await store.fetchAll()

    expect(store.error).toBe('Failed to load parts: Network error')
  })
})
