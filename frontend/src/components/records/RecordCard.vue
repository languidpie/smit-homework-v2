<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import type { VinylRecord } from '@/types/record'

const props = defineProps<{
  record: VinylRecord
}>()

const emit = defineEmits<{
  delete: [id: number]
}>()

const conditionColor = computed(() => {
  const colors: Record<string, string> = {
    MINT: 'badge-green',
    NEAR_MINT: 'bg-emerald-100 text-emerald-800',
    EXCELLENT: 'badge-blue',
    VERY_GOOD: 'bg-purple-100 text-purple-800',
    GOOD: 'badge-yellow',
    FAIR: 'bg-orange-100 text-orange-800',
    POOR: 'badge-red'
  }
  return colors[props.record.condition] || 'badge-gray'
})

const genreLabel = computed(() => {
  return props.record.genre.replace('_', ' ')
})
</script>

<template>
  <div class="card p-4" data-testid="record-card">
    <div class="flex justify-between items-start mb-3">
      <div>
        <h3 class="font-semibold text-gray-900 line-clamp-1">{{ record.title }}</h3>
        <p class="text-gray-600">{{ record.artist }}</p>
      </div>
      <span :class="['badge', conditionColor]" data-testid="condition-badge">
        {{ record.condition.replace('_', ' ') }}
      </span>
    </div>

    <div class="flex items-center gap-3 text-sm text-gray-500 mb-3">
      <span class="flex items-center gap-1">
        📅 {{ record.releaseYear }}
      </span>
      <span class="badge-gray">{{ genreLabel }}</span>
    </div>

    <div v-if="record.purchaseSource" class="text-sm text-gray-500 mb-2">
      <span>🛒 {{ record.purchaseSource }}</span>
    </div>

    <p v-if="record.notes" class="text-sm text-gray-500 line-clamp-2 mb-3">
      {{ record.notes }}
    </p>

    <div class="flex gap-2 pt-3 border-t border-gray-100">
      <RouterLink
        :to="`/records/${record.id}/edit`"
        class="btn-secondary text-sm flex-1 text-center"
        data-testid="edit-btn"
      >
        Edit
      </RouterLink>
      <button
        class="btn-danger text-sm"
        data-testid="delete-btn"
        @click="emit('delete', record.id)"
      >
        Delete
      </button>
    </div>
  </div>
</template>
