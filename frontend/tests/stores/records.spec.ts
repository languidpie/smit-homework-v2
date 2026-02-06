import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useRecordsStore } from '@/stores/records'
import type { VinylRecord } from '@/types/record'

const mockRecords: VinylRecord[] = [
  {
    id: 1,
    title: 'Abbey Road',
    artist: 'The Beatles',
    releaseYear: 1969,
    genre: 'ROCK',
    purchaseSource: 'Local store',
    purchaseDate: '2024-01-15',
    condition: 'EXCELLENT',
    notes: 'Great find!',
    createdAt: '2024-01-15T10:00:00',
    updatedAt: '2024-01-15T10:00:00'
  },
  {
    id: 2,
    title: 'Kind of Blue',
    artist: 'Miles Davis',
    releaseYear: 1959,
    genre: 'JAZZ',
    purchaseSource: 'Online',
    purchaseDate: '2024-02-01',
    condition: 'MINT',
    notes: null,
    createdAt: '2024-02-01T10:00:00',
    updatedAt: '2024-02-01T10:00:00'
  }
]

describe('Records Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.resetAllMocks()
  })

  it('initializes with empty records', () => {
    const store = useRecordsStore()
    expect(store.records).toEqual([])
    expect(store.totalRecords).toBe(0)
  })

  it('fetches all records', async () => {
    vi.mocked(global.fetch).mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve({
        content: mockRecords,
        pageNumber: 0,
        pageSize: 20,
        totalElements: 2,
        totalPages: 1
      })
    } as Response)

    const store = useRecordsStore()
    await store.fetchAll()

    expect(store.records).toHaveLength(2)
    expect(store.totalRecords).toBe(2)
  })

  it('filters records by genre', async () => {
    const store = useRecordsStore()
    store.records = mockRecords

    store.setGenreFilter('ROCK')

    expect(store.filteredRecords).toHaveLength(1)
    expect(store.filteredRecords[0].genre).toBe('ROCK')
  })

  it('filters records by search query', async () => {
    vi.mocked(global.fetch).mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve([mockRecords[0]])
    } as Response)

    const store = useRecordsStore()
    store.setSearchQuery('beatles')
    await store.searchServer('beatles')

    expect(store.filteredRecords).toHaveLength(1)
    expect(store.filteredRecords[0].artist).toContain('Beatles')
  })

  it('counts records by genre', () => {
    const store = useRecordsStore()
    store.records = mockRecords

    expect(store.recordsByGenre).toEqual({
      ROCK: 1,
      JAZZ: 1
    })
  })

  it('gets record by id', () => {
    const store = useRecordsStore()
    store.records = mockRecords

    const record = store.getRecordById(1)

    expect(record?.title).toBe('Abbey Road')
  })

  it('handles fetch error', async () => {
    vi.mocked(global.fetch).mockRejectedValueOnce(new Error('Network error'))

    const store = useRecordsStore()
    await store.fetchAll()

    expect(store.error).toBe('Failed to load records: Network error')
  })
})
