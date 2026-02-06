import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Part, PartCreateRequest, PartUpdateRequest, PartType, Page } from '@/types/part'
import { partsApi } from '@/api/parts'
import { ApiException } from '@/api/client'

export const usePartsStore = defineStore('parts', () => {
  const parts = ref<Part[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const selectedType = ref<PartType | null>(null)
  const searchQuery = ref('')

  // Pagination state
  const currentPage = ref(0)
  const pageSize = ref(20)
  const totalElements = ref(0)
  const totalPages = ref(0)

  const filteredParts = computed(() => {
    let result = [...parts.value]

    if (selectedType.value) {
      result = result.filter(p => p.type === selectedType.value)
    }

    if (searchQuery.value) {
      const query = searchQuery.value.toLowerCase()
      result = result.filter(p =>
        p.name.toLowerCase().includes(query) ||
        p.description?.toLowerCase().includes(query) ||
        p.location.toLowerCase().includes(query)
      )
    }

    return result
  })

  const totalParts = computed(() => totalElements.value)

  const partsByType = computed(() => {
    const counts: Record<string, number> = {}
    parts.value.forEach(p => {
      counts[p.type] = (counts[p.type] || 0) + 1
    })
    return counts
  })

  const hasPreviousPage = computed(() => currentPage.value > 0)
  const hasNextPage = computed(() => currentPage.value < totalPages.value - 1)

  async function fetchAll(page = 0) {
    isLoading.value = true
    error.value = null
    try {
      const response = await partsApi.getAll(page, pageSize.value)
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

  async function createPart(data: PartCreateRequest): Promise<Part> {
    try {
      const newPart = await partsApi.create(data)
      parts.value.push(newPart)
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
      parts.value = parts.value.filter(p => p.id !== id)
    } catch (e) {
      if (e instanceof ApiException) {
        throw new Error(e.userMessage)
      }
      throw new Error(`Failed to delete part: ${e instanceof Error ? e.message : 'Unknown error'}`)
    }
  }

  function setTypeFilter(type: PartType | null) {
    selectedType.value = type
  }

  function setSearchQuery(query: string) {
    searchQuery.value = query
  }

  function getPartById(id: number): Part | undefined {
    return parts.value.find(p => p.id === id)
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
    partsByType,
    currentPage,
    totalPages,
    hasPreviousPage,
    hasNextPage,
    fetchAll,
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
