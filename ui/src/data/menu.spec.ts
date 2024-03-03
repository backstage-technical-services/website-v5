import { describe, expect, it, beforeEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { type MainMenuItem, useMenu } from './menu'
import { useAuthStore } from '../stores'
import { permissions } from '../config/auth'

const createAuthStore = (...permissions: string[]) => {
  const authStore = useAuthStore()
  authStore.isAuthenticated = true
  authStore.permissions = permissions
}

const assertMenuContains = (menu: MainMenuItem[], sectionId: string, itemIds: string[]) => {
  const menuSection = menu.find(item => item.id === sectionId)
  expect(menuSection, 'menu section should exist').not.toBeUndefined()

  expect(menuSection?.children?.map(item => item.id) ?? []).toStrictEqual(itemIds)
}

describe('building the main menu', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('no permissions => empty menu', () => {
    const menu = useMenu()

    expect(menu).toHaveLength(0)
  })

  it('should add the assets register with the correct role', () => {
    createAuthStore(permissions.equipment.assets.view)

    const menu = useMenu()

    assertMenuContains(menu, 'equipment', ['equipment-asset-register'])
  })

  it('should add the quotes board with the correct role', () => {
    createAuthStore(permissions.quotes.list)

    const menu = useMenu()

    assertMenuContains(menu, 'members', ['members-quotesboard'])
  })
})
