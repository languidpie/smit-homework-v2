import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { VinylRecord, RecordCreateRequest, RecordUpdateRequest, Genre, Page } from '@/types/record'
import { recordsApi } from '@/api/records'
import { ApiException } from '@/api/client'

export const useRecordsStore = defineStore('records', () => {
  const records = ref<VinylRecord[]>([])
  const searchResults = ref<VinylRecord[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const selectedGenre = ref<Genre | null>(null)
  const searchQuery = ref('')

  // Pagination state
  const currentPage = ref(0)
  const pageSize = ref(20)
  const totalElements = ref(0)
  const totalPages = ref(0)

  const filteredRecords = computed(() => {
    const source = searchQuery.value ? searchResults.value : records.value

    if (selectedGenre.value) {
      return source.filter(r => r.genre === selectedGenre.value)
    }

    return source
  })

  const totalRecords = computed(() => totalElements.value)

  const recordsByGenre = computed(() => {
    const counts: Record<string, number> = {}
    records.value.forEach(r => {
      counts[r.genre] = (counts[r.genre] || 0) + 1
    })
    return counts
  })

  const hasPreviousPage = computed(() => currentPage.value > 0)
  const hasNextPage = computed(() => currentPage.value < totalPages.value - 1)

  async function fetchAll(page = 0) {
    isLoading.value = true
    error.value = null
    try {
      const response = await recordsApi.getAll(page, pageSize.value)
      records.value = response.content
      currentPage.value = response.pageNumber
      totalElements.value = response.totalElements
      totalPages.value = response.totalPages
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

  async function nextPage() {
    if (hasNextPage.value) {
      await fetchAll(currentPage.value + 1)
    }
  }

  async function previousPage() {
    if (hasPreviousPage.value) {
      await fetchAll(currentPage.value - 1)
    }
  }

  async function searchServer(query: string) {
    isLoading.value = true
    error.value = null
    try {
      searchResults.value = await recordsApi.search(query)
    } catch (e) {
      if (e instanceof ApiException) {
        error.value = e.userMessage
      } else if (e instanceof Error) {
        error.value = `Search failed: ${e.message}`
      } else {
        error.value = 'Search failed. Please try again.'
      }
    } finally {
      isLoading.value = false
    }
  }

  async function createRecord(data: RecordCreateRequest): Promise<VinylRecord> {
    try {
      const newRecord = await recordsApi.create(data)
      await fetchAll(currentPage.value)
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
      await fetchAll(currentPage.value)
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
    currentPage,
    totalPages,
    hasPreviousPage,
    hasNextPage,
    fetchAll,
    searchServer,
    nextPage,
    previousPage,
    createRecord,
    updateRecord,
    deleteRecord,
    setGenreFilter,
    setSearchQuery,
    getRecordById,
    clearError
  }
})
