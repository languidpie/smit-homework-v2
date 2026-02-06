import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { VinylRecord, RecordCreateRequest, RecordUpdateRequest, Genre } from '@/types/record'
import { recordsApi } from '@/api/records'
import { ApiException } from '@/api/client'

export const useRecordsStore = defineStore('records', () => {
  const records = ref<VinylRecord[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const selectedGenre = ref<Genre | null>(null)
  const searchQuery = ref('')

  const filteredRecords = computed(() => {
    let result = [...records.value]

    if (selectedGenre.value) {
      result = result.filter(r => r.genre === selectedGenre.value)
    }

    if (searchQuery.value) {
      const query = searchQuery.value.toLowerCase()
      result = result.filter(r =>
        r.title.toLowerCase().includes(query) ||
        r.artist.toLowerCase().includes(query) ||
        r.notes?.toLowerCase().includes(query)
      )
    }

    return result
  })

  const totalRecords = computed(() => records.value.length)

  const recordsByGenre = computed(() => {
    const counts: Record<string, number> = {}
    records.value.forEach(r => {
      counts[r.genre] = (counts[r.genre] || 0) + 1
    })
    return counts
  })

  async function fetchAll() {
    isLoading.value = true
    error.value = null
    try {
      records.value = await recordsApi.getAll()
    } catch (e) {
      if (e instanceof ApiException) {
        error.value = e.userMessage
      } else if (e instanceof Error) {
        error.value = `Failed to load records: ${e.message}`
      } else {
        error.value = 'Failed to load records. Please check your connection and try again.'
      }
    } finally {
      isLoading.value = false
    }
  }

  async function createRecord(data: RecordCreateRequest): Promise<VinylRecord> {
    try {
      const newRecord = await recordsApi.create(data)
      records.value.push(newRecord)
      return newRecord
    } catch (e) {
      if (e instanceof ApiException) {
        throw e // Re-throw ApiException for form handling
      }
      throw new Error(`Failed to create record: ${e instanceof Error ? e.message : 'Unknown error'}`)
    }
  }

  async function updateRecord(id: number, data: RecordUpdateRequest): Promise<VinylRecord> {
    try {
      const updatedRecord = await recordsApi.update(id, data)
      const index = records.value.findIndex(r => r.id === id)
      if (index !== -1) {
        records.value[index] = updatedRecord
      }
      return updatedRecord
    } catch (e) {
      if (e instanceof ApiException) {
        throw e // Re-throw ApiException for form handling
      }
      throw new Error(`Failed to update record: ${e instanceof Error ? e.message : 'Unknown error'}`)
    }
  }

  async function deleteRecord(id: number): Promise<void> {
    try {
      await recordsApi.delete(id)
      records.value = records.value.filter(r => r.id !== id)
    } catch (e) {
      if (e instanceof ApiException) {
        throw new Error(e.userMessage)
      }
      throw new Error(`Failed to delete record: ${e instanceof Error ? e.message : 'Unknown error'}`)
    }
  }

  function setGenreFilter(genre: Genre | null) {
    selectedGenre.value = genre
  }

  function setSearchQuery(query: string) {
    searchQuery.value = query
  }

  function getRecordById(id: number): VinylRecord | undefined {
    return records.value.find(r => r.id === id)
  }

  function clearError() {
    error.value = null
  }

  return {
    records,
    isLoading,
    error,
    selectedGenre,
    searchQuery,
    filteredRecords,
    totalRecords,
    recordsByGenre,
    fetchAll,
    createRecord,
    updateRecord,
    deleteRecord,
    setGenreFilter,
    setSearchQuery,
    getRecordById,
    clearError
  }
})
