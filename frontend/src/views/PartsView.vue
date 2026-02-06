<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { usePartsStore } from '@/stores/parts'
import { PART_TYPES } from '@/types/part'
import type { PartType } from '@/types/part'
import PartListItem from '@/components/parts/PartListItem.vue'

const store = usePartsStore()
const searchInput = ref('')
const deleteError = ref<string | null>(null)

onMounted(() => {
  store.fetchAll()
})

function handleSearch() {
  store.setSearchQuery(searchInput.value)
}

function handleTypeFilter(type: PartType | null) {
  store.setTypeFilter(type)
}

async function handleDelete(id: number) {
  const part = store.getPartById(id)
  const partName = part?.name || 'this part'

  if (confirm(`Are you sure you want to delete "${partName}"? This action cannot be undone.`)) {
    deleteError.value = null
    try {
      await store.deletePart(id)
    } catch (e) {
      deleteError.value = e instanceof Error ? e.message : 'Failed to delete part'
    }
  }
}

function dismissDeleteError() {
  deleteError.value = null
}

function retryFetch() {
  store.clearError()
  store.fetchAll()
}
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Bicycle Parts</h1>
      <RouterLink to="/parts/new" class="btn-primary">
        Add Part
      </RouterLink>
    </div>

    <!-- Delete Error Alert -->
    <div v-if="deleteError" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
      <div class="flex justify-between">
        <div class="flex">
          <div class="flex-shrink-0">
            <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
          </div>
          <div class="ml-3">
            <h3 class="text-sm font-medium text-red-800">Delete Failed</h3>
            <p class="mt-1 text-sm text-red-700">{{ deleteError }}</p>
          </div>
        </div>
        <button @click="dismissDeleteError" class="text-red-500 hover:text-red-700">
          <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>
    </div>

    <!-- Filters -->
    <div class="mb-6 flex flex-wrap gap-4">
      <div class="flex-1 min-w-[200px]">
        <input
          v-model="searchInput"
          type="text"
          placeholder="Search parts..."
          class="form-input"
          @input="handleSearch"
        />
      </div>
      <div class="flex gap-2 flex-wrap">
        <button
          class="btn-secondary text-sm"
          :class="{ 'ring-2 ring-blue-500': !store.selectedType }"
          @click="handleTypeFilter(null)"
        >
          All
        </button>
        <button
          v-for="type in PART_TYPES"
          :key="type.value"
          class="btn-secondary text-sm"
          :class="{ 'ring-2 ring-blue-500': store.selectedType === type.value }"
          @click="handleTypeFilter(type.value)"
        >
          {{ type.label }}
        </button>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="store.isLoading" class="text-center py-12">
      <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto mb-4" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      <p class="text-gray-500">Loading parts...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="store.error" class="text-center py-12">
      <div class="max-w-md mx-auto p-6 bg-red-50 border border-red-200 rounded-lg">
        <svg class="h-12 w-12 text-red-400 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <h3 class="text-lg font-medium text-red-800 mb-2">Failed to Load Parts</h3>
        <p class="text-red-700 mb-4">{{ store.error }}</p>
        <button @click="retryFetch" class="btn-primary">
          Try Again
        </button>
      </div>
    </div>

    <!-- Empty state -->
    <div v-else-if="store.filteredParts.length === 0" class="text-center py-12">
      <div class="text-6xl mb-4">🔧</div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">No parts found</h3>
      <p class="text-gray-500 mb-4">
        {{ store.searchQuery || store.selectedType
          ? 'Try adjusting your search or filter'
          : 'Get started by adding your first bicycle part' }}
      </p>
      <RouterLink v-if="!store.searchQuery && !store.selectedType" to="/parts/new" class="btn-primary">
        Add Part
      </RouterLink>
    </div>

    <!-- Parts table -->
    <template v-else>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 bg-white rounded-lg shadow">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Location</th>
              <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Qty</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Condition</th>
              <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <PartListItem
              v-for="part in store.filteredParts"
              :key="part.id"
              :part="part"
              @delete="handleDelete"
            />
          </tbody>
        </table>
      </div>

      <!-- Pagination controls -->
      <div v-if="store.totalPages > 1" class="mt-6 flex items-center justify-between border-t border-gray-200 pt-4">
        <div class="text-sm text-gray-700">
          Page {{ store.currentPage + 1 }} of {{ store.totalPages }}
          <span class="ml-2 text-gray-500">({{ store.totalParts }} total items)</span>
        </div>
        <div class="flex gap-2">
          <button
            class="btn-secondary text-sm"
            :disabled="!store.hasPreviousPage"
            :class="{ 'opacity-50 cursor-not-allowed': !store.hasPreviousPage }"
            @click="store.previousPage()"
          >
            Previous
          </button>
          <button
            class="btn-secondary text-sm"
            :disabled="!store.hasNextPage"
            :class="{ 'opacity-50 cursor-not-allowed': !store.hasNextPage }"
            @click="store.nextPage()"
          >
            Next
          </button>
        </div>
      </div>
    </template>
  </div>
</template>
