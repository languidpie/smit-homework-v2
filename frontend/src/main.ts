import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/main.css'
import { setAuthHeaderProvider } from './api/client'
import { useAuthStore } from './stores/auth'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Initialize auth
const authStore = useAuthStore()

// Set up auth header provider for API client
setAuthHeaderProvider(() => authStore.getAuthHeader())

// Listen for unauthorized events
window.addEventListener('auth:unauthorized', () => {
  authStore.logout()
  router.push('/login')
})

// Try to restore session before mounting
authStore.restoreSession().finally(() => {
  app.mount('#app')
})
