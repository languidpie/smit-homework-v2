<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
import type { Part } from '@/types/part'
import { getPartConditionColor } from '@/utils/condition-colors'

const props = defineProps<{
  part: Part
}>()

const emit = defineEmits<{
  delete: [id: number]
}>()

const expanded = ref(false)

const conditionColor = computed(() => getPartConditionColor(props.part.condition))

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
  <tr class="hover:bg-gray-50 cursor-pointer" data-testid="part-list-item" @click="expanded = !expanded">
    <td class="px-4 py-3 whitespace-nowrap">
      <div class="flex items-center gap-2">
        <span class="text-gray-400 text-xs transition-transform duration-200" :class="expanded ? 'rotate-90' : ''">&#9654;</span>
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
          @click.stop
        >
          Edit
        </RouterLink>
        <button
          class="btn-danger text-sm"
          data-testid="delete-btn"
          @click.stop="emit('delete', part.id)"
        >
          Delete
        </button>
      </div>
    </td>
  </tr>
  <tr v-if="expanded" class="bg-gray-50">
    <td colspan="6" class="px-4 py-3 text-sm text-gray-700">
      <div class="grid grid-cols-2 gap-4 max-w-xl">
        <div>
          <span class="font-medium text-gray-500">Description</span>
          <p>{{ part.description || 'No description' }}</p>
        </div>
        <div>
          <span class="font-medium text-gray-500">Notes</span>
          <p>{{ part.notes || 'No notes' }}</p>
        </div>
      </div>
    </td>
  </tr>
</template>
