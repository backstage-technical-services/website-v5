/// <reference types="vitest" />

import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import eslint from 'vite-plugin-eslint'
import { quasar, transformAssetUrls } from '@quasar/vite-plugin'
import unhead from '@unhead/addons/vite'
import istanbul from 'vite-plugin-istanbul'
import { configDefaults } from 'vitest/config'

export default defineConfig({
  build: {
    sourcemap: 'inline',
  },
  plugins: [
    vue({
      template: {
        transformAssetUrls,
      },
    }),
    eslint(),
    quasar(),
    unhead(),
    istanbul({
      include: 'src/*',
      exclude: ['node_modules'],
      extension: ['.ts', '.vue'],
      cypress: true,
      requireEnv: true,
      forceBuildInstrument: true,
    }),
  ],
  server: {
    port: 8000,
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    exclude: [...configDefaults.exclude, 'e2e/*'],
    root: fileURLToPath(new URL('./', import.meta.url)),
    // setupFiles: './tests/unit/setup.ts',
    coverage: {
      clean: true,
      cleanOnRerun: true,
      provider: 'v8',
      reporter: ['text', 'lcov'],
      reportsDirectory: './coverage/unit',
    },
  },
})
