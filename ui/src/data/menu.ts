import type { RouteLocationRaw } from 'vue-router'
import { mdiDatabase, mdiFormatQuoteOpen, mdiTableMultiple } from '@quasar/extras/mdi-v7'
import { can } from '@/helpers/auth'
import { permissions } from '@/config/auth'
import { assetRegister, patDatabase } from '@/data/links'

export type MainMenuItem = {
  id?: string
  text?: string
  icon?: string
  link?: RouteLocationRaw
  href?: string
  children?: MainMenuItem[]
}

const buildSection = (props: Omit<MainMenuItem, 'children'>, childrenFn: () => MainMenuItem[]): MainMenuItem | null => {
  const children = childrenFn()

  return children.length ? {
    ...props,
    children,
  } satisfies MainMenuItem : null
}

const buildMembers = () => buildSection({
  id: 'members',
  text: 'Members',
}, () => {
  const items: MainMenuItem[] = []

  if (can(permissions.quotes.list)) {
    items.push({
      id: 'members-quotesboard',
      text: 'Quotes Board',
      link: { name: 'quotesboard' },
      icon: mdiFormatQuoteOpen,
    })
  }

  return items
})

const buildEquipment = () => buildSection({
  id: 'equipment',
  text: 'Equipment',
}, () => {
  const items: MainMenuItem[] = []

  if(can(permissions.equipment.assets.view)) {
    items.push({
      id: 'equipment-asset-register',
      text: 'Asset Register',
      href: assetRegister,
      icon: mdiTableMultiple,
    })
  }

  if(can(permissions.equipment.patDatabase.view)) {
    items.push({
      id: 'equipment-pat-database',
      text: 'PAT Database',
      href: patDatabase,
      icon: mdiDatabase,
    })
  }

  return items
})

export const useMenu = () => {
  const members = buildMembers()
  const equipment = buildEquipment()

  return [
    ...(members ? [members] : []),
    ...(equipment ? [equipment] : []),
  ]
}
