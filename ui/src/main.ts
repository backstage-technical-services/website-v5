import { createApp } from 'vue'
import { createPinia } from 'pinia'

import { Quasar, config as quasarConfig } from './config/quasar'

import App from './App.vue'
import router from './router'
import auth0 from './config/auth'
import unhead from './config/unhead'
import './assets/css/main.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Quasar, quasarConfig)
app.use(auth0)
app.use(unhead)

app.mount('#app')
