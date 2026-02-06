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
  <div class="card p-4" data-testid="part-card">
    <div class="flex justify-between items-start mb-3">
      <div class="flex items-center gap-2">
        <span class="text-2xl">{{ typeIcon }}</span>
        <div>
          <h3 class="font-semibold text-gray-900 line-clamp-1">{{ part.name }}</h3>
          <p class="text-sm text-gray-500">{{ part.type }}</p>
        </div>
      </div>
      <span :class="['badge', conditionColor]" data-testid="condition-badge">
        {{ part.condition }}
      </span>
    </div>

    <div class="space-y-2 text-sm text-gray-600 mb-4">
      <div class="flex items-center gap-2">
        <span>📍</span>
        <span>{{ part.location }}</span>
      </div>
      <div class="flex items-center gap-2">
        <span>📦</span>
        <span>Quantity: {{ part.quantity }}</span>
      </div>
      <p v-if="part.description" class="text-gray-500 line-clamp-2">
        {{ part.description }}
      </p>
    </div>

    <div class="flex gap-2 pt-3 border-t border-gray-100">
      <RouterLink
        :to="`/parts/${part.id}/edit`"
        class="btn-secondary text-sm flex-1 text-center"
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
  </div>
</template>
