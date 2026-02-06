import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: 'Home' }
  },
  {
    path: '/parts',
    name: 'parts',
    component: () => import('@/views/PartsView.vue'),
    meta: { title: 'Bicycle Parts' }
  },
  {
    path: '/parts/new',
    name: 'parts-new',
    component: () => import('@/views/PartFormView.vue'),
    meta: { title: 'Add Part' }
  },
  {
    path: '/parts/:id/edit',
    name: 'parts-edit',
    component: () => import('@/views/PartFormView.vue'),
    meta: { title: 'Edit Part' }
  },
  {
    path: '/records',
    name: 'records',
    component: () => import('@/views/RecordsView.vue'),
    meta: { title: 'Vinyl Records' }
  },
  {
    path: '/records/new',
    name: 'records-new',
    component: () => import('@/views/RecordFormView.vue'),
    meta: { title: 'Add Record' }
  },
  {
    path: '/records/:id/edit',
    name: 'records-edit',
    component: () => import('@/views/RecordFormView.vue'),
    meta: { title: 'Edit Record' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || 'Page'} | Inventory System`
  next()
})

export default router
