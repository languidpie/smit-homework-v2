import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    requiresAuth?: boolean
    requiredRole?: 'ROLE_PARTS' | 'ROLE_RECORDS'
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: 'Login', requiresAuth: false }
  },
  {
    path: '/unauthorized',
    name: 'unauthorized',
    component: () => import('@/views/UnauthorizedView.vue'),
    meta: { title: 'Unauthorized', requiresAuth: true }
  },
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: 'Home', requiresAuth: true }
  },
  {
    path: '/parts',
    name: 'parts',
    component: () => import('@/views/PartsView.vue'),
    meta: { title: 'Bicycle Parts', requiresAuth: true, requiredRole: 'ROLE_PARTS' }
  },
  {
    path: '/parts/new',
    name: 'parts-new',
    component: () => import('@/views/PartFormView.vue'),
    meta: { title: 'Add Part', requiresAuth: true, requiredRole: 'ROLE_PARTS' }
  },
  {
    path: '/parts/:id/edit',
    name: 'parts-edit',
    component: () => import('@/views/PartFormView.vue'),
    meta: { title: 'Edit Part', requiresAuth: true, requiredRole: 'ROLE_PARTS' }
  },
  {
    path: '/records',
    name: 'records',
    component: () => import('@/views/RecordsView.vue'),
    meta: { title: 'Vinyl Records', requiresAuth: true, requiredRole: 'ROLE_RECORDS' }
  },
  {
    path: '/records/new',
    name: 'records-new',
    component: () => import('@/views/RecordFormView.vue'),
    meta: { title: 'Add Record', requiresAuth: true, requiredRole: 'ROLE_RECORDS' }
  },
  {
    path: '/records/:id/edit',
    name: 'records-edit',
    component: () => import('@/views/RecordFormView.vue'),
    meta: { title: 'Edit Record', requiresAuth: true, requiredRole: 'ROLE_RECORDS' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || 'Page'} | Inventory System`

  const authStore = useAuthStore()

  // Allow access to login page for unauthenticated users
  if (to.meta.requiresAuth === false) {
    if (authStore.isAuthenticated && to.name === 'login') {
      // Redirect authenticated users away from login
      if (authStore.canAccessParts) {
        next({ name: 'parts' })
      } else if (authStore.canAccessRecords) {
        next({ name: 'records' })
      } else {
        next({ name: 'home' })
      }
      return
    }
    next()
    return
  }

  // Check authentication for protected routes
  if (!authStore.isAuthenticated) {
    next({ name: 'login' })
    return
  }

  // Check role-based access
  if (to.meta.requiredRole) {
    const hasAccess =
      (to.meta.requiredRole === 'ROLE_PARTS' && authStore.canAccessParts) ||
      (to.meta.requiredRole === 'ROLE_RECORDS' && authStore.canAccessRecords)

    if (!hasAccess) {
      next({ name: 'unauthorized' })
      return
    }
  }

  next()
})

export default router
