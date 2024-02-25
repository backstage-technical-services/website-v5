import { defineConfig } from 'cypress'
import * as dotenv from 'dotenv'

dotenv.config({
  path: '.env.cypress',
})

export default defineConfig({
  e2e: {
    setupNodeEvents(on, config) {
      // See: https://github.com/cypress-io/code-coverage/issues/782
      // require('@cypress/code-coverage/task')(on, config)

      return config
    },
    specPattern: 'cypress/e2e/**/*.{cy,spec}.{js,jsx,ts,tsx}',
    baseUrl: 'http://localhost:8000',
    viewportWidth: 1920,
    viewportHeight: 1080,
  },
  env: {
    AUTH0_DOMAIN: 'backstage-dev.uk.auth0.com',
    AUTH0_USERNAME: process.env.AUTH0_USERNAME,
    AUTH0_PASSWORD: process.env.AUTH0_PASSWORD,
  },
  video: true,
})
