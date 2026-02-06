import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import RecordCard from '@/components/records/RecordCard.vue'
import type { VinylRecord } from '@/types/record'

const mockRecord: VinylRecord = {
  id: 1,
  title: 'Abbey Road',
  artist: 'The Beatles',
  releaseYear: 1969,
  genre: 'ROCK',
  purchaseSource: 'Local record store',
  purchaseDate: '2024-01-15',
  condition: 'EXCELLENT',
  notes: 'Great find at the flea market!',
  createdAt: '2024-01-15T10:00:00',
  updatedAt: '2024-01-15T10:00:00'
}

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/records/:id/edit', name: 'records-edit', component: { template: '<div />' } }]
})

describe('RecordCard', () => {
  it('renders record title', () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('Abbey Road')
  })

  it('renders artist name', () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('The Beatles')
  })

  it('renders release year', () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('1969')
  })

  it('renders genre', () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('ROCK')
  })

  it('shows condition badge', () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    const badge = wrapper.find('[data-testid="condition-badge"]')
    expect(badge.text()).toBe('EXCELLENT')
  })

  it('renders purchase source', () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('Local record store')
  })

  it('renders notes', () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    expect(wrapper.text()).toContain('Great find at the flea market!')
  })

  it('emits delete event when delete button clicked', async () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    await wrapper.find('[data-testid="delete-btn"]').trigger('click')

    expect(wrapper.emitted('delete')).toBeTruthy()
    expect(wrapper.emitted('delete')![0]).toEqual([1])
  })

  it('has edit link', () => {
    const wrapper = mount(RecordCard, {
      props: { record: mockRecord },
      global: { plugins: [router] }
    })

    const editLink = wrapper.find('[data-testid="edit-btn"]')
    expect(editLink.attributes('href')).toBe('/records/1/edit')
  })
})
