import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Part, PartCreateRequest, PartUpdateRequest, PartType, Page } from '@/types/part'
import type { SortDirection } from '@/types/common'
import { partsApi } from '@/api/parts'
import { ApiException } from '@/api/client'

export const usePartsStore = defineStore('parts', () => {
  const parts = ref<Part[]>([])
  const searchResults = ref<Part[]>([])
  const typeFilterResults = ref<Part[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const selectedType = ref<PartType | null>(null)
  const searchQuery = ref('')

  // Pagination state
  const currentPage = ref(0)
  const pageSize = ref(20)
  const totalElements = ref(0)
  const totalPages = ref(0)

  // Sort state
  const sortField = ref<string | null>(null)
  const sortDirection = ref<SortDirection>('ASC')

  const filteredParts = computed(() => {
    if (searchQuery.value) {
      const source = searchResults.value
      if (selectedType.value) {
        return source.filter(p => p.type === selectedType.value)
      }
      return source
    }
    if (selectedType.value) {
      return typeFilterResults.value
    }
    return parts.value
  })

  const totalParts = computed(() => totalElements.value)

  const isPaginated = computed(() => !searchQuery.value && !selectedType.value)

  const hasPreviousPage = computed(() => currentPage.value > 0)
  const hasNextPage = computed(() => currentPage.value < totalPages.value - 1)

  async function fetchAll(page = 0) {
    isLoading.value = true
    error.value = null
    try {
      const response = await partsApi.getAll(
        page,
        pageSize.value,
        sortField.value ?? undefined,
        sortDirection.value
      )
      parts.value = response.content
      currentPage.value = response.pageNumber
      totalElements.value = response.totalElements
      totalPages.value = response.totalPages
    } catch (e) {
      if (e instanceof ApiException) {
        error.value = e.userMessage
      } else if (e instanceof Error) {
        error.value = `Failed to load parts: ${e.message}`
      } else {
        error.value = 'Failed to load parts. Please check your connection and try again.'
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

  async function fetchByType(type: PartType) {
    isLoading.value = true
    error.value = null
    try {
      typeFilterResults.value = await partsApi.getByType(type)
    } catch (e) {
      if (e instanceof ApiException) {
        error.value = e.userMessage
      } else if (e instanceof Error) {
        error.value = `Failed to load parts: ${e.message}`
      } else {
        error.value = 'Failed to load parts. Please check your connection and try again.'
      }
    } finally {
      isLoading.value = false
    }
  }

  async function searchServer(query: string) {
    isLoading.value = true
    error.value = null
    try {
      searchResults.value = await partsApi.search(query)
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

  async function createPart(data: PartCreateRequest): Promise<Part> {
    try {
      const newPart = await partsApi.create(data)
      await fetchAll(currentPage.value)
      return newPart
    } catch (e) {
      if (e instanceof ApiException) {
        throw e // Re-throw ApiException for form handling
      }
      throw new Error(`Failed to create part: ${e instanceof Error ? e.message : 'Unknown error'}`)
    }
  }

  async function updatePart(id: number, data: PartUpdateRequest): Promise<Part> {
    try {
      const updatedPart = await partsApi.update(id, data)
      const index = parts.value.findIndex(p => p.id === id)
      if (index !== -1) {
        parts.value[index] = updatedPart
      }
      return updatedPart
    } catch (e) {
      if (e instanceof ApiException) {
        throw e // Re-throw ApiException for form handling
      }
      throw new Error(`Failed to update part: ${e instanceof Error ? e.message : 'Unknown error'}`)
    }
  }

  async function deletePart(id: number): Promise<void> {
    try {
      await partsApi.delete(id)
      if (searchQuery.value) {
        await searchServer(searchQuery.value)
      } else if (selectedType.value) {
        await fetchByType(selectedType.value)
      } else {
        await fetchAll(currentPage.value)
      }
    } catch (e) {
      if (e instanceof ApiException) {
        throw e
      }
      throw new Error(`Failed to delete part: ${e instanceof Error ? e.message : 'Unknown error'}`)
    }
  }

  function setTypeFilter(type: PartType | null) {
    selectedType.value = type
    if (!searchQuery.value) {
      if (type) {
        fetchByType(type)
      } else {
        fetchAll(0)
      }
    }
  }

  function setSearchQuery(query: string) {
    searchQuery.value = query
  }

  function getPartById(id: number): Part | undefined {
    return parts.value.find(p => p.id === id)
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
    parts,
    isLoading,
    error,
    selectedType,
    searchQuery,
    filteredParts,
    totalParts,
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
    createPart,
    updatePart,
    deletePart,
    setTypeFilter,
    setSearchQuery,
    getPartById,
    clearError
  }
})
