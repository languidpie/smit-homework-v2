<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePartsStore } from '@/stores/parts'
import { partsApi } from '@/api/parts'
import { PART_TYPES, PART_CONDITIONS } from '@/types/part'
import type { PartType, PartCondition } from '@/types/part'
import { validatePart, getErrorMessage, getValidationErrors } from '@/utils/validation'
import { ApiException } from '@/api/client'

const route = useRoute()
const router = useRouter()
const store = usePartsStore()

const isEditing = computed(() => !!route.params.id)
const partId = computed(() => Number(route.params.id))

const form = ref({
  name: '',
  description: '',
  type: 'OTHER' as PartType,
  location: '',
  quantity: 1,
  condition: 'NEW' as PartCondition,
  notes: ''
})

const isSubmitting = ref(false)
const error = ref<string | null>(null)
const fieldErrors = ref<Record<string, string>>({})

onMounted(async () => {
  if (isEditing.value) {
    try {
      const part = await partsApi.getById(partId.value)
      form.value = {
        name: part.name,
        description: part.description || '',
        type: part.type,
        location: part.location,
        quantity: part.quantity,
        condition: part.condition,
        notes: part.notes || ''
      }
    } catch (e) {
      if (e instanceof ApiException && e.status === 404) {
        error.value = 'Part not found. It may have been deleted.'
      } else {
        error.value = getErrorMessage(e)
      }
    }
  }
})

function validateForm(): boolean {
  const result = validatePart({
    name: form.value.name,
    type: form.value.type,
    location: form.value.location,
    quantity: form.value.quantity,
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
      description: form.value.description || undefined,
      notes: form.value.notes || undefined
    }

    if (isEditing.value) {
      await store.updatePart(partId.value, data)
    } else {
      await store.createPart(data)
    }
    router.push('/parts')
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
        {{ isEditing ? 'Edit Part' : 'Add New Part' }}
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
        <!-- Name -->
        <div>
          <label for="name" class="form-label">
            Name <span class="text-red-500">*</span>
          </label>
          <input
            id="name"
            v-model="form.name"
            type="text"
            class="form-input"
            :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.name }"
            placeholder="e.g., Shimano Ultegra Brake"
            @input="clearFieldError('name')"
          />
          <p v-if="fieldErrors.name" class="mt-1 text-sm text-red-600">
            {{ fieldErrors.name }}
          </p>
        </div>

        <!-- Type -->
        <div>
          <label for="type" class="form-label">
            Type <span class="text-red-500">*</span>
          </label>
          <select
            id="type"
            v-model="form.type"
            class="form-select"
            :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.type }"
            @change="clearFieldError('type')"
          >
            <option v-for="type in PART_TYPES" :key="type.value" :value="type.value">
              {{ type.label }}
            </option>
          </select>
          <p v-if="fieldErrors.type" class="mt-1 text-sm text-red-600">
            {{ fieldErrors.type }}
          </p>
        </div>

        <!-- Location -->
        <div>
          <label for="location" class="form-label">
            Location <span class="text-red-500">*</span>
          </label>
          <input
            id="location"
            v-model="form.location"
            type="text"
            class="form-input"
            :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.location }"
            placeholder="e.g., Garage shelf A2"
            @input="clearFieldError('location')"
          />
          <p v-if="fieldErrors.location" class="mt-1 text-sm text-red-600">
            {{ fieldErrors.location }}
          </p>
          <p class="mt-1 text-sm text-gray-500">Where is this part stored?</p>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <!-- Quantity -->
          <div>
            <label for="quantity" class="form-label">
              Quantity <span class="text-red-500">*</span>
            </label>
            <input
              id="quantity"
              v-model.number="form.quantity"
              type="number"
              min="1"
              step="1"
              class="form-input"
              :class="{ 'border-red-500 focus:ring-red-500': fieldErrors.quantity }"
              @input="clearFieldError('quantity')"
            />
            <p v-if="fieldErrors.quantity" class="mt-1 text-sm text-red-600">
              {{ fieldErrors.quantity }}
            </p>
          </div>

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
              <option v-for="cond in PART_CONDITIONS" :key="cond.value" :value="cond.value">
                {{ cond.label }}
              </option>
            </select>
            <p v-if="fieldErrors.condition" class="mt-1 text-sm text-red-600">
              {{ fieldErrors.condition }}
            </p>
          </div>
        </div>

        <!-- Description -->
        <div>
          <label for="description" class="form-label">Description</label>
          <textarea
            id="description"
            v-model="form.description"
            rows="2"
            class="form-textarea"
            placeholder="Brief description of the part (optional)"
          ></textarea>
        </div>

        <!-- Notes -->
        <div>
          <label for="notes" class="form-label">Notes</label>
          <textarea
            id="notes"
            v-model="form.notes"
            rows="3"
            class="form-textarea"
            placeholder="Any additional notes... (optional)"
          ></textarea>
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
            <span v-else>{{ isEditing ? 'Update Part' : 'Add Part' }}</span>
          </button>
          <button
            type="button"
            class="btn-secondary"
            @click="router.push('/parts')"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
