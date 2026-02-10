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
    return credentials.value ? `Bearer ${credentials.value}` : null
  }

  async function login(loginCredentials: LoginCredentials): Promise<boolean> {
    try {
      const loginResponse = await fetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: loginCredentials.username,
          password: loginCredentials.password
        })
      })

      if (!loginResponse.ok) {
        return false
      }

      const tokenData = await loginResponse.json()
      const accessToken = tokenData.access_token

      const meResponse = await fetch('/api/auth/me', {
        headers: { Authorization: `Bearer ${accessToken}` }
      })

      if (!meResponse.ok) {
        return false
      }

      const userInfo: User = await meResponse.json()
      user.value = userInfo
      credentials.value = accessToken
      sessionStorage.setItem(AUTH_STORAGE_KEY, accessToken)
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
          Authorization: `Bearer ${storedToken}`
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
