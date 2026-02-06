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
  <tr class="hover:bg-gray-50" data-testid="record-list-item">
    <td class="px-4 py-3 whitespace-nowrap">
      <div>
        <span class="font-medium text-gray-900">{{ record.title }}</span>
        <p class="text-sm text-gray-500">{{ record.artist }}</p>
      </div>
    </td>
    <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-600 text-center">
      {{ record.releaseYear }}
    </td>
    <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-600">
      {{ genreLabel }}
    </td>
    <td class="px-4 py-3 whitespace-nowrap">
      <span :class="['badge', conditionColor]" data-testid="condition-badge">
        {{ record.condition.replace('_', ' ') }}
      </span>
    </td>
    <td class="px-4 py-3 whitespace-nowrap text-right">
      <div class="flex gap-2 justify-end">
        <RouterLink
          :to="`/records/${record.id}/edit`"
          class="btn-secondary text-sm"
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
    </td>
  </tr>
</template>
