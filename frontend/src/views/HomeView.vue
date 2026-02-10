<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="text-center mb-12">
      <h1 class="text-4xl font-bold text-gray-900 mb-4">
        Welcome to Inventory System
      </h1>
      <p class="text-lg text-gray-600">
        Manage your bicycle parts and vinyl record collections
      </p>
    </div>

    <div class="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
      <!-- Bicycle Parts Card -->
      <div v-if="authStore.canAccessParts" class="card p-6">
        <div class="text-center">
          <div class="text-6xl mb-4">🚲</div>
          <h2 class="text-2xl font-bold text-gray-900 mb-2">Bicycle Parts</h2>
          <p class="text-gray-600 mb-6">
            Mart's bicycle parts inventory - frames, brakes, tires, pumps and more
          </p>
          <RouterLink to="/parts" class="btn-primary inline-block">
            View Parts
          </RouterLink>
        </div>
      </div>

      <!-- Vinyl Records Card -->
      <div v-if="authStore.canAccessRecords" class="card p-6">
        <div class="text-center">
          <div class="text-6xl mb-4">🎵</div>
          <h2 class="text-2xl font-bold text-gray-900 mb-2">Vinyl Records</h2>
          <p class="text-gray-600 mb-6">
            Katrin's vinyl collection - track your records, artists and memories
          </p>
          <RouterLink to="/records" class="btn-primary inline-block">
            View Records
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>
