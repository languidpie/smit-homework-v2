import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, vi } from 'vitest'

beforeEach(() => {
  setActivePinia(createPinia())
})

// Mock fetch globally
global.fetch = vi.fn()
