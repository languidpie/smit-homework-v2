import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { VinylRecord, RecordCreateRequest, RecordUpdateRequest, Genre, Page } from '@/types/record'
import type { SortDirection } from '@/types/common'
import { recordsApi } from '@/api/records'
import { ApiException } from '@/api/client'

export const useRecordsStore = defineStore('records', () => {
  const records = ref<VinylRecord[]>([])
  const searchResults = ref<VinylRecord[]>([])
  const genreFilterResults = ref<VinylRecord[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const selectedGenre = ref<Genre | null>(null)
  const searchQuery = ref('')

  // Pagination state
  const currentPage = ref(0)
  const pageSize = ref(20)
  const totalElements = ref(0)
  const totalPages = ref(0)

  // Sort state
  const sortField = ref<string | null>(null)
  const sortDirection = ref<SortDirection>('ASC')

  const filteredRecords = computed(() => {
    if (searchQuery.value) {
      const source = searchResults.value
      if (selectedGenre.value) {
        return source.filter(r => r.genre === selectedGenre.value)
      }
      return source
    }
    if (selectedGenre.value) {
      return genreFilterResults.value
    }
    return records.value
  })

  const totalRecords = computed(() => totalElements.value)

  const isPaginated = computed(() => !searchQuery.value && !selectedGenre.value)

  const hasPreviousPage = computed(() => currentPage.value > 0)
  const hasNextPage = computed(() => currentPage.value < totalPages.value - 1)

  async function fetchAll(page = 0) {
    isLoading.value = true
    error.value = null
    try {
      const response = await recordsApi.getAll(
        page,
        pageSize.value,
        sortField.value ?? undefined,
        sortDirection.value
      )
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

  async function fetchByGenre(genre: Genre) {
    isLoading.value = true
    error.value = null
    try {
      genreFilterResults.value = await recordsApi.getByGenre(genre)
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
      if (searchQuery.value) {
        await searchServer(searchQuery.value)
      } else if (selectedGenre.value) {
        await fetchByGenre(selectedGenre.value)
      } else {
        await fetchAll(currentPage.value)
      }
    } catch (e) {
      if (e instanceof ApiException) {
        throw new Error(e.userMessage)
      }
      throw new Error(`Failed to delete record: ${e instanceof Error ? e.message : 'Unknown error'}`)
    }
  }

  function setGenreFilter(genre: Genre | null) {
    selectedGenre.value = genre
    if (!searchQuery.value) {
      if (genre) {
        fetchByGenre(genre)
      } else {
        fetchAll(0)
      }
    }
  }

  function setSearchQuery(query: string) {
    searchQuery.value = query
  }

  function getRecordById(id: number): VinylRecord | undefined {
    return records.value.find(r => r.id === id)
  }

  function toggleSort(field: string) {
    if (sortField.value === field) {
      if (sortDirection.value === 'ASC') {
        sortDirection.value = 'DESC'
      } else {
        sortField.value = null
        sortDirection.value = 'ASC'
      }
    } else {
      sortField.value = field
      sortDirection.value = 'ASC'
    }
    fetchAll(0)
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
    isPaginated,
    currentPage,
    totalPages,
    hasPreviousPage,
    hasNextPage,
    sortField,
    sortDirection,
    fetchAll,
    toggleSort,
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
