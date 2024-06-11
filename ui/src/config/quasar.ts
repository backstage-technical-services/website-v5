import { Quasar, Notify, Dialog, LocalStorage } from 'quasar'

// Load the language
import langEnGb from 'quasar/lang/en-GB.js'

// Load the font
import '@quasar/extras/roboto-font/roboto-font.css'

// Load the icons
import iconSet from 'quasar/icon-set/mdi-v7'
import '@quasar/extras/mdi-v7/mdi-v7.css'

Notify.setDefaults({
  position: 'top',
})

export { Quasar }

export const config = {
  lang: langEnGb,
  config: {
    dark: true,
    supportTS: true,
  },
  iconSet,
  plugins: {
    Dialog,
    LocalStorage,
    Notify,
  },
}
