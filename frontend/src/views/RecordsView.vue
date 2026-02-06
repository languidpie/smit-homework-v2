<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useRecordsStore } from '@/stores/records'
import { GENRES } from '@/types/record'
import type { Genre } from '@/types/record'
import RecordListItem from '@/components/records/RecordListItem.vue'
import { debounce } from '@/utils/debounce'

const store = useRecordsStore()
const searchInput = ref('')
const deleteError = ref<string | null>(null)

onMounted(() => {
  store.fetchAll()
})

const debouncedSearch = debounce((query: string) => {
  store.setSearchQuery(query)
  if (query) {
    store.searchServer(query)
  } else {
    store.fetchAll()
  }
}, 300)

function handleSearch() {
  debouncedSearch(searchInput.value)
}

function handleGenreFilter(genre: Genre | null) {
  store.setGenreFilter(genre)
}

async function handleDelete(id: number) {
  const record = store.getRecordById(id)
  const recordTitle = record ? `"${record.title}" by ${record.artist}` : 'this record'

  if (confirm(`Are you sure you want to delete ${recordTitle}? This action cannot be undone.`)) {
    deleteError.value = null
    try {
      await store.deleteRecord(id)
    } catch (e) {
      deleteError.value = e instanceof Error ? e.message : 'Failed to delete record'
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

const sortableColumns = [
  { field: 'title', label: 'Title / Artist', align: 'text-left' },
  { field: 'releaseYear', label: 'Year', align: 'text-center' },
  { field: 'genre', label: 'Genre', align: 'text-left' },
  { field: 'condition', label: 'Condition', align: 'text-left' }
]

const isSearching = computed(() => !!store.searchQuery)

function handleSort(field: string) {
  if (!isSearching.value) {
    store.toggleSort(field)
  }
}
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Vinyl Records</h1>
      <RouterLink to="/records/new" class="btn-primary">
        Add Record
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
          placeholder="Search by title or artist..."
          class="form-input"
          @input="handleSearch"
        />
      </div>
      <div>
        <select
          class="form-select"
          :value="store.selectedGenre || ''"
          @change="handleGenreFilter(($event.target as HTMLSelectElement).value as Genre || null)"
        >
          <option value="">All Genres</option>
          <option v-for="genre in GENRES" :key="genre.value" :value="genre.value">
            {{ genre.label }}
          </option>
        </select>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="store.isLoading" class="text-center py-12">
      <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto mb-4" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      <p class="text-gray-500">Loading records...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="store.error" class="text-center py-12">
      <div class="max-w-md mx-auto p-6 bg-red-50 border border-red-200 rounded-lg">
        <svg class="h-12 w-12 text-red-400 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <h3 class="text-lg font-medium text-red-800 mb-2">Failed to Load Records</h3>
        <p class="text-red-700 mb-4">{{ store.error }}</p>
        <button @click="retryFetch" class="btn-primary">
          Try Again
        </button>
      </div>
    </div>

    <!-- Empty state -->
    <div v-else-if="store.filteredRecords.length === 0" class="text-center py-12">
      <div class="text-6xl mb-4">🎵</div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">No records found</h3>
      <p class="text-gray-500 mb-4">
        {{ store.searchQuery || store.selectedGenre
          ? 'Try adjusting your search or filter'
          : 'Start building your collection by adding your first vinyl' }}
      </p>
      <RouterLink v-if="!store.searchQuery && !store.selectedGenre" to="/records/new" class="btn-primary">
        Add Record
      </RouterLink>
    </div>

    <!-- Records table -->
    <template v-else>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 bg-white rounded-lg shadow">
          <thead class="bg-gray-50">
            <tr>
              <th
                v-for="col in sortableColumns"
                :key="col.field"
                class="px-4 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider select-none"
                :class="[col.align, isSearching ? '' : 'cursor-pointer hover:text-gray-700']"
                @click="handleSort(col.field)"
              >
                <span class="inline-flex items-center gap-1">
                  {{ col.label }}
                  <template v-if="!isSearching">
                    <svg v-if="store.sortField === col.field && store.sortDirection === 'ASC'" class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20"><path d="M5 13l5-5 5 5H5z"/></svg>
                    <svg v-else-if="store.sortField === col.field && store.sortDirection === 'DESC'" class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20"><path d="M5 7l5 5 5-5H5z"/></svg>
                    <svg v-else class="w-3 h-3 opacity-30" fill="currentColor" viewBox="0 0 20 20"><path d="M7 8l3-3 3 3H7zm0 4l3 3 3-3H7z"/></svg>
                  </template>
                </span>
              </th>
              <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <RecordListItem
              v-for="record in store.filteredRecords"
              :key="record.id"
              :record="record"
              @delete="handleDelete"
            />
          </tbody>
        </table>
      </div>

      <!-- Pagination controls -->
      <div v-if="store.totalPages > 1" class="mt-6 flex items-center justify-between border-t border-gray-200 pt-4">
        <div class="text-sm text-gray-700">
          Page {{ store.currentPage + 1 }} of {{ store.totalPages }}
          <span class="ml-2 text-gray-500">({{ store.totalRecords }} total items)</span>
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
