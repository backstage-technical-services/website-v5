// Install Quasar custom commands
import { registerCommands } from '@quasar/quasar-app-extension-testing-e2e-cypress'
registerCommands()

// Code coverage
import '@cypress/code-coverage/support.js'

// Import custom commands
import './commands'
