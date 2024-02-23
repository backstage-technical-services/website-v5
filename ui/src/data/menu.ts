import type { RouteLocationRaw } from 'vue-router'
import { mdiFormatQuoteOpen } from '@quasar/extras/mdi-v7'
import { can } from '@/helpers/auth'
import { permissions } from '@/config/auth'

export type MainMenuItem = {
  id?: string
  text?: string
  icon?: string
  link?: string | RouteLocationRaw
  children?: MainMenuItem[]
}

export type MainMenu = MainMenuItem[]

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

export const useMenu = () => {
  const members = buildMembers()

  return [
    ...(members ? [members] : []),
  ]
}
