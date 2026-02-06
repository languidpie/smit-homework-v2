<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

function goBack() {
  if (authStore.canAccessParts) {
    router.push('/parts')
  } else if (authStore.canAccessRecords) {
    router.push('/records')
  } else {
    router.push('/')
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full text-center">
      <div class="text-6xl text-red-500 mb-4">⛔</div>
      <h1 class="text-3xl font-bold text-gray-900 mb-4">Access Denied</h1>
      <p class="text-gray-600 mb-8">
        You do not have permission to access this page.
      </p>
      <button
        @click="goBack"
        class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      >
        Go to your section
      </button>
    </div>
  </div>
</template>
