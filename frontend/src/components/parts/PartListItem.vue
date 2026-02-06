<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import type { Part } from '@/types/part'

const props = defineProps<{
  part: Part
}>()

const emit = defineEmits<{
  delete: [id: number]
}>()

const conditionColor = computed(() => {
  const colors: Record<string, string> = {
    NEW: 'badge-green',
    EXCELLENT: 'badge-blue',
    GOOD: 'badge-yellow',
    FAIR: 'bg-orange-100 text-orange-800',
    POOR: 'badge-red'
  }
  return colors[props.part.condition] || 'badge-gray'
})

const typeIcon = computed(() => {
  const icons: Record<string, string> = {
    FRAME: '🖼️',
    BRAKE: '🛑',
    TIRE: '⭕',
    PUMP: '💨',
    OTHER: '🔧'
  }
  return icons[props.part.type] || '🔧'
})
</script>

<template>
  <tr class="hover:bg-gray-50" data-testid="part-list-item">
    <td class="px-4 py-3 whitespace-nowrap">
      <div class="flex items-center gap-2">
        <span class="text-lg">{{ typeIcon }}</span>
        <span class="font-medium text-gray-900">{{ part.name }}</span>
      </div>
    </td>
    <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-600">
      {{ part.type }}
    </td>
    <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-600">
      {{ part.location }}
    </td>
    <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-600 text-center">
      {{ part.quantity }}
    </td>
    <td class="px-4 py-3 whitespace-nowrap">
      <span :class="['badge', conditionColor]" data-testid="condition-badge">
        {{ part.condition }}
      </span>
    </td>
    <td class="px-4 py-3 whitespace-nowrap text-right">
      <div class="flex gap-2 justify-end">
        <RouterLink
          :to="`/parts/${part.id}/edit`"
          class="btn-secondary text-sm"
          data-testid="edit-btn"
        >
          Edit
        </RouterLink>
        <button
          class="btn-danger text-sm"
          data-testid="delete-btn"
          @click="emit('delete', part.id)"
        >
          Delete
        </button>
      </div>
    </td>
  </tr>
</template>
