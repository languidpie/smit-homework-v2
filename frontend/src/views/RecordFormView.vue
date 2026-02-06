<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRecordsStore } from '@/stores/records'
import { GENRES, RECORD_CONDITIONS } from '@/types/record'
import type { Genre, RecordCondition } from '@/types/record'
import { validateRecord, getErrorMessage, getValidationErrors } from '@/utils/validation'
import { ApiException } from '@/api/client'

const route = useRoute()
const router = useRouter()
const store = useRecordsStore()

const isEditing = computed(() => !!route.params.id)
const recordId = computed(() => Number(route.params.id))
const currentYear = new Date().getFullYear()

const form = ref({
  title: '',
  artist: '',
  releaseYear: currentYear,
  genre: 'ROCK' as Genre,
  purchaseSource: '',
  purchaseDate: '',
  condition: 'EXCELLENT' as RecordCondition,
  notes: ''
})

const isSubmitting = ref(false)
const error = ref<string | null>(null)
const fieldErrors = ref<Record<string, string>>({})

onMounted(async () => {
  if (isEditing.value) {
    await store.fetchAll()
    const record = store.getRecordById(recordId.value)
    if (record) {
      form.value = {
        title: record.title,
        artist: record.artist,
        releaseYear: record.releaseYear,
        genre: record.genre,
        purchaseSource: record.purchaseSource || '',
        purchaseDate: record.purchaseDate || '',
        condition: record.condition,
        notes: record.notes || ''
      }
    } else {
      error.value = 'Record not found. It may have been deleted.'
    }
  }
})

function validateForm(): boolean {
  const result = validateRecord({
    title: form.value.title,
    artist: form.value.artist,
    releaseYear: form.value.releaseYear,
    genre: form.value.genre,
    condition: form.value.condition
  })
  fieldErrors.value = result.errors
  return result.valid
}

function clearFieldError(field: string) {
  if (fieldErrors.value[field]) {
    delete fieldErrors.value[field]
  }
}

async function handleSubmit() {
  error.value = null

  // Client-side validation
  if (!validateForm()) {
    error.value = 'Please fix the errors below before submitting.'
    return
  }

  isSubmitting.value = true

  try {
    const data = {
      ...form.value,
      purchaseSource: form.value.purchaseSource || undefined,
      purchaseDate: form.value.purchaseDate || undefined,
      notes: form.value.notes || undefined
    }

    if (isEditing.value) {
      await store.updateRecord(recordId.value, data)
    } else {
      await store.createRecord(data)
    }
    router.push('/records')
  } catch (e) {
    // Handle API errors
    if (e instanceof ApiException) {
      error.value = e.userMessage

      // Merge server validation errors with field errors
      const serverErrors = getValidationErrors(e)
      if (serverErrors) {
        fieldErrors.value = { ...fieldErrors.value, ...serverErrors }
      }
    } else {
      error.value = getErrorMessage(e)
    }
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="max-w-2xl mx-auto">
      <h1 class="text-2xl font-bold text-gray-900 mb-6">
        {{ isEditing ? 'Edit Record' : 'Add New Record' }}
      </h1>

      <!-- Error Alert -->
      <div v-if="error" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
        <div class="flex">
          <div class="flex-shrink-0">
            <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
          </div>
          <div class="ml-3">
            <h3 class="text-sm font-medium text-red-800">Error</h3>
            <p class="mt-1 text-sm text-red-700">{{ error }}</p>
          </div>
        </div>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- Title -->
        <div>
          <label for="title" class="form-label">
            Title <span class="text-red-500">*</span>
          </label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            class="form-input"
            :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.title }"
            placeholder="e.g., Abbey Road"
            @input="clearFieldError('title')"
          />
          <p v-if="fieldErrors.title" class="mt-1 text-sm text-red-600">
            {{ fieldErrors.title }}
          </p>
        </div>

        <!-- Artist -->
        <div>
          <label for="artist" class="form-label">
            Artist <span class="text-red-500">*</span>
          </label>
          <input
            id="artist"
            v-model="form.artist"
            type="text"
            class="form-input"
            :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.artist }"
            placeholder="e.g., The Beatles"
            @input="clearFieldError('artist')"
          />
          <p v-if="fieldErrors.artist" class="mt-1 text-sm text-red-600">
            {{ fieldErrors.artist }}
          </p>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <!-- Release Year -->
          <div>
            <label for="releaseYear" class="form-label">
              Release Year <span class="text-red-500">*</span>
            </label>
            <input
              id="releaseYear"
              v-model.number="form.releaseYear"
              type="number"
              min="1900"
              :max="currentYear"
              step="1"
              class="form-input"
              :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.releaseYear }"
              @input="clearFieldError('releaseYear')"
            />
            <p v-if="fieldErrors.releaseYear" class="mt-1 text-sm text-red-600">
              {{ fieldErrors.releaseYear }}
            </p>
            <p v-else class="mt-1 text-sm text-gray-500">Between 1900 and {{ currentYear }}</p>
          </div>

          <!-- Genre -->
          <div>
            <label for="genre" class="form-label">
              Genre <span class="text-red-500">*</span>
            </label>
            <select
              id="genre"
              v-model="form.genre"
              class="form-select"
              :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.genre }"
              @change="clearFieldError('genre')"
            >
              <option v-for="genre in GENRES" :key="genre.value" :value="genre.value">
                {{ genre.label }}
              </option>
            </select>
            <p v-if="fieldErrors.genre" class="mt-1 text-sm text-red-600">
              {{ fieldErrors.genre }}
            </p>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <!-- Condition -->
          <div>
            <label for="condition" class="form-label">
              Condition <span class="text-red-500">*</span>
            </label>
            <select
              id="condition"
              v-model="form.condition"
              class="form-select"
              :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.condition }"
              @change="clearFieldError('condition')"
            >
              <option v-for="cond in RECORD_CONDITIONS" :key="cond.value" :value="cond.value">
                {{ cond.label }}
              </option>
            </select>
            <p v-if="fieldErrors.condition" class="mt-1 text-sm text-red-600">
              {{ fieldErrors.condition }}
            </p>
          </div>

          <!-- Purchase Date -->
          <div>
            <label for="purchaseDate" class="form-label">Purchase Date</label>
            <input
              id="purchaseDate"
              v-model="form.purchaseDate"
              type="date"
              class="form-input"
            />
            <p class="mt-1 text-sm text-gray-500">When did you get this record?</p>
          </div>
        </div>

        <!-- Purchase Source -->
        <div>
          <label for="purchaseSource" class="form-label">Where did you get it?</label>
          <input
            id="purchaseSource"
            v-model="form.purchaseSource"
            type="text"
            class="form-input"
            placeholder="e.g., Local flea market, Discogs, Gift from friend"
          />
          <p class="mt-1 text-sm text-gray-500">The story of how you found this record</p>
        </div>

        <!-- Notes -->
        <div>
          <label for="notes" class="form-label">Notes & Memories</label>
          <textarea
            id="notes"
            v-model="form.notes"
            rows="4"
            class="form-textarea"
            placeholder="Any memories, thoughts, or notes about this record..."
          ></textarea>
          <p class="mt-1 text-sm text-gray-500">What makes this record special to you?</p>
        </div>

        <!-- Buttons -->
        <div class="flex gap-4 pt-4">
          <button
            type="submit"
            :disabled="isSubmitting"
            class="btn-primary flex-1"
          >
            <span v-if="isSubmitting" class="flex items-center justify-center">
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Saving...
            </span>
            <span v-else>{{ isEditing ? 'Update Record' : 'Add Record' }}</span>
          </button>
          <button
            type="button"
            class="btn-secondary"
            @click="router.push('/records')"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
