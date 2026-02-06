import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import PartCard from '@/components/parts/PartCard.vue'
import type { Part } from '@/types/part'

const mockPart: Part = {
  id: 1,
  name: 'Shimano Brake',
  description: 'High quality disc brake',
  type: 'BRAKE',
  location: 'Garage shelf A2',
  quantity: 2,
  condition: 'NEW',
  notes: 'Premium item',
  createdAt: '2024-01-01T10:00:00',
  updatedAt: '2024-01-01T10:00:00'
}

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/parts/:id/edit', name: 'parts-edit', component: { template: '<div />' } }]
})

describe('PartCard', () => {
  it('renders part name', () => {
    const wrapper = mount(PartCard, {
      props: { part: mockPart },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('Shimano Brake')
  })

  it('renders part type', () => {
    const wrapper = mount(PartCard, {
      props: { part: mockPart },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('BRAKE')
  })

  it('renders location', () => {
    const wrapper = mount(PartCard, {
      props: { part: mockPart },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('Garage shelf A2')
  })

  it('renders quantity', () => {
    const wrapper = mount(PartCard, {
      props: { part: mockPart },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('Quantity: 2')
  })

  it('shows condition badge', () => {
    const wrapper = mount(PartCard, {
      props: { part: mockPart },
      global: { plugins: [router] }
    })

    const badge = wrapper.find('[data-testid="condition-badge"]')
    expect(badge.text()).toBe('NEW')
  })

  it('emits delete event when delete button clicked', async () => {
    const wrapper = mount(PartCard, {
      props: { part: mockPart },
      global: { plugins: [router] }
    })

    await wrapper.find('[data-testid="delete-btn"]').trigger('click')

    expect(wrapper.emitted('delete')).toBeTruthy()
    expect(wrapper.emitted('delete')![0]).toEqual([1])
  })

  it('has edit link', () => {
    const wrapper = mount(PartCard, {
      props: { part: mockPart },
      global: { plugins: [router] }
    })

    const editLink = wrapper.find('[data-testid="edit-btn"]')
    expect(editLink.attributes('href')).toBe('/parts/1/edit')
  })
})
