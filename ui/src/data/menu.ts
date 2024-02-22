export type MainMenuItem = {
  id?: string
  text?: string
  icon?: string
  link?: string
  children?: MainMenuItem[]
}

export type MainMenu = MainMenuItem[]

const menu: MainMenu = []

export default menu
