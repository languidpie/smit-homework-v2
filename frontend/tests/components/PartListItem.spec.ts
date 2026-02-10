import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import PartListItem from '@/components/parts/PartListItem.vue'
import type { Part } from '@/types/part'

const mockPart: Part = {
  id: 1,
  name: 'Shimano Brake',
  description: 'High quality brake',
  type: 'BRAKE',
  location: 'Garage',
  quantity: 2,
  condition: 'NEW',
  notes: 'Test notes',
  createdAt: '2024-01-01T10:00:00',
  updatedAt: '2024-01-01T10:00:00'
}

const router = createRouter({
  history: createMemoryHistory(),
  routes: [{ path: '/', component: { template: '<div />' } }, { path: '/parts/:id/edit', component: { template: '<div />' } }]
})

function mountItem(part: Part = mockPart) {
  return mount(PartListItem, {
    props: { part },
    global: { plugins: [router] },
    attachTo: document.createElement('tbody')
  })
}

describe('PartListItem', () => {
  it('renders part name', () => {
    const wrapper = mountItem()
    expect(wrapper.text()).toContain('Shimano Brake')
  })

  it('renders part type', () => {
    const wrapper = mountItem()
    expect(wrapper.text()).toContain('BRAKE')
  })

  it('renders quantity', () => {
    const wrapper = mountItem()
    expect(wrapper.text()).toContain('2')
  })

  it('renders condition badge', () => {
    const wrapper = mountItem()
    const badge = wrapper.find('[data-testid="condition-badge"]')
    expect(badge.text()).toBe('NEW')
  })

  it('emits delete event with part id', async () => {
    const wrapper = mountItem()
    await wrapper.find('[data-testid="delete-btn"]').trigger('click')
    expect(wrapper.emitted('delete')).toEqual([[1]])
  })

  it('has edit link pointing to correct route', () => {
    const wrapper = mountItem()
    const editLink = wrapper.find('[data-testid="edit-btn"]')
    expect(editLink.attributes('href')).toBe('/parts/1/edit')
  })

  it('expands to show details on row click', async () => {
    const wrapper = mountItem()
    expect(wrapper.text()).not.toContain('Test notes')
    await wrapper.find('[data-testid="part-list-item"]').trigger('click')
    expect(wrapper.text()).toContain('Test notes')
  })

  it('shows "No description" when description is null', async () => {
    const wrapper = mountItem({ ...mockPart, description: null })
    await wrapper.find('[data-testid="part-list-item"]').trigger('click')
    expect(wrapper.text()).toContain('No description')
  })
})
