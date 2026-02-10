<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
import type { VinylRecord } from '@/types/record'
import { getRecordConditionColor } from '@/utils/condition-colors'

const props = defineProps<{
  record: VinylRecord
}>()

const emit = defineEmits<{
  delete: [id: number]
}>()

const expanded = ref(false)

const conditionColor = computed(() => getRecordConditionColor(props.record.condition))

const genreLabel = computed(() => {
  return props.record.genre.replaceAll('_', ' ')
})
</script>

<template>
  <tr class="hover:bg-gray-50 cursor-pointer" data-testid="record-list-item" @click="expanded = !expanded">
    <td class="px-4 py-3 whitespace-nowrap">
      <div class="flex items-center gap-2">
        <span class="text-gray-400 text-xs transition-transform duration-200" :class="expanded ? 'rotate-90' : ''">&#9654;</span>
        <div>
          <span class="font-medium text-gray-900">{{ record.title }}</span>
          <p class="text-sm text-gray-500">{{ record.artist }}</p>
        </div>
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
          @click.stop
        >
          Edit
        </RouterLink>
        <button
          class="btn-danger text-sm"
          data-testid="delete-btn"
          @click.stop="emit('delete', record.id)"
        >
          Delete
        </button>
      </div>
    </td>
  </tr>
  <tr v-if="expanded" class="bg-gray-50">
    <td colspan="5" class="px-4 py-3 text-sm text-gray-700">
      <div class="grid grid-cols-3 gap-4 max-w-2xl">
        <div>
          <span class="font-medium text-gray-500">Purchase source</span>
          <p>{{ record.purchaseSource || '—' }}</p>
        </div>
        <div>
          <span class="font-medium text-gray-500">Purchase date</span>
          <p>{{ record.purchaseDate ? new Date(record.purchaseDate).toLocaleDateString() : '—' }}</p>
        </div>
        <div>
          <span class="font-medium text-gray-500">Notes</span>
          <p>{{ record.notes || 'No notes' }}</p>
        </div>
      </div>
    </td>
  </tr>
</template>
