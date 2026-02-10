import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import RecordListItem from '@/components/records/RecordListItem.vue'
import type { VinylRecord } from '@/types/record'

const mockRecord: VinylRecord = {
  id: 1,
  title: 'Abbey Road',
  artist: 'The Beatles',
  releaseYear: 1969,
  genre: 'ROCK',
  purchaseSource: 'Local store',
  purchaseDate: '2024-01-15',
  condition: 'EXCELLENT',
  notes: 'Great find!',
  createdAt: '2024-01-15T10:00:00',
  updatedAt: '2024-01-15T10:00:00'
}

const router = createRouter({
  history: createMemoryHistory(),
  routes: [{ path: '/', component: { template: '<div />' } }, { path: '/records/:id/edit', component: { template: '<div />' } }]
})

function mountItem(record: VinylRecord = mockRecord) {
  return mount(RecordListItem, {
    props: { record },
    global: { plugins: [router] },
    attachTo: document.createElement('tbody')
  })
}

describe('RecordListItem', () => {
  it('renders title and artist', () => {
    const wrapper = mountItem()
    expect(wrapper.text()).toContain('Abbey Road')
    expect(wrapper.text()).toContain('The Beatles')
  })

  it('renders release year', () => {
    const wrapper = mountItem()
    expect(wrapper.text()).toContain('1969')
  })

  it('renders genre label with underscores replaced', () => {
    const wrapper = mountItem({ ...mockRecord, genre: 'HIP_HOP' })
    expect(wrapper.text()).toContain('HIP HOP')
  })

  it('renders condition badge', () => {
    const wrapper = mountItem()
    const badge = wrapper.find('[data-testid="condition-badge"]')
    expect(badge.text()).toBe('EXCELLENT')
  })

  it('emits delete event with record id', async () => {
    const wrapper = mountItem()
    await wrapper.find('[data-testid="delete-btn"]').trigger('click')
    expect(wrapper.emitted('delete')).toEqual([[1]])
  })

  it('has edit link pointing to correct route', () => {
    const wrapper = mountItem()
    const editLink = wrapper.find('[data-testid="edit-btn"]')
    expect(editLink.attributes('href')).toBe('/records/1/edit')
  })

  it('expands to show details on row click', async () => {
    const wrapper = mountItem()
    expect(wrapper.text()).not.toContain('Great find!')
    await wrapper.find('[data-testid="record-list-item"]').trigger('click')
    expect(wrapper.text()).toContain('Great find!')
    expect(wrapper.text()).toContain('Local store')
  })

  it('shows "No notes" when notes is null', async () => {
    const wrapper = mountItem({ ...mockRecord, notes: null })
    await wrapper.find('[data-testid="record-list-item"]').trigger('click')
    expect(wrapper.text()).toContain('No notes')
  })
})
