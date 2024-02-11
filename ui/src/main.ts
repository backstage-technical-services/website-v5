import { createApp } from 'vue'
import { createPinia } from 'pinia'

import type { QuasarPluginOptions } from 'quasar'
import { Quasar, config as quasarConfig } from './config/quasar'

import App from './App.vue'
import router from './router'
import auth0 from './config/auth'
import './config/unhead'
import './assets/css/main.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Quasar, quasarConfig as Partial<QuasarPluginOptions>)
app.use(auth0)

app.mount('#app')
