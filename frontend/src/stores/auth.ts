import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginCredentials } from '@/types/auth'

const AUTH_STORAGE_KEY = 'auth_session'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const credentials = ref<string | null>(null)
  const sessionChecked = ref(false)

  const isAuthenticated = computed(() => !!user.value)
  const canAccessParts = computed(() => user.value?.role === 'ROLE_PARTS')
  const canAccessRecords = computed(() => user.value?.role === 'ROLE_RECORDS')

  function getAuthHeader(): string | null {
    return credentials.value ? `Basic ${credentials.value}` : null
  }

  async function login(loginCredentials: LoginCredentials): Promise<boolean> {
    const token = btoa(`${loginCredentials.username}:${loginCredentials.password}`)

    try {
      const response = await fetch('/api/auth/me', {
        headers: {
          Authorization: `Basic ${token}`
        }
      })

      if (!response.ok) {
        return false
      }

      const userInfo: User = await response.json()
      user.value = userInfo
      credentials.value = token
      sessionStorage.setItem(AUTH_STORAGE_KEY, token)
      return true
    } catch {
      return false
    }
  }

  function logout(): void {
    user.value = null
    credentials.value = null
    sessionStorage.removeItem(AUTH_STORAGE_KEY)
  }

  async function restoreSession(): Promise<boolean> {
    const storedToken = sessionStorage.getItem(AUTH_STORAGE_KEY)
    if (!storedToken) {
      sessionChecked.value = true
      return false
    }

    try {
      const response = await fetch('/api/auth/me', {
        headers: {
          Authorization: `Basic ${storedToken}`
        }
      })

      if (!response.ok) {
        sessionStorage.removeItem(AUTH_STORAGE_KEY)
        sessionChecked.value = true
        return false
      }

      const userInfo: User = await response.json()
      user.value = userInfo
      credentials.value = storedToken
      sessionChecked.value = true
      return true
    } catch {
      sessionStorage.removeItem(AUTH_STORAGE_KEY)
      sessionChecked.value = true
      return false
    }
  }

  return {
    user,
    sessionChecked,
    isAuthenticated,
    canAccessParts,
    canAccessRecords,
    getAuthHeader,
    login,
    logout,
    restoreSession
  }
})
