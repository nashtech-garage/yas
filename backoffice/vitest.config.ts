import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  resolve: { 
    alias: {
      '@catalogServices': path.resolve(__dirname, './modules/catalog/services'),
      '@commonItems': path.resolve(__dirname, './common/items'),
      '@commonServices': path.resolve(__dirname, './common/services'),
      '@locationServices': path.resolve(__dirname, './modules/location/services'),
      '@taxServices': path.resolve(__dirname, './modules/tax/services'),
      '@webhookServices': path.resolve(__dirname, './modules/webhook/services'), 
      '@webhookModels': path.resolve(__dirname, './modules/webhook/models'),
      '@constants': path.resolve(__dirname, './constants'),
      '@inventoryServices': path.resolve(__dirname, './modules/inventory/services'),
      'common': path.resolve(__dirname, './common'),
      '@webhookComponents': path.resolve(__dirname, './modules/webhook/components'),
      '@catalogComponents': path.resolve(__dirname, './modules/catalog/components'),
    }
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./vitest.setup.ts'],
    include: [
      'test/**/*.{test,spec}.{js,jsx,ts,tsx}',
      'common/**/*.{test,spec}.{js,jsx,ts,tsx}',
      'modules/**/*.{test,spec}.{js,jsx,ts,tsx}',
      'pages/**/*.{test,spec}.{js,jsx,ts,tsx}',
      'utils/**/*.{test,spec}.{js,jsx,ts,tsx}'
    ],
    exclude: [
      'node_modules/**',
      '.next/**',
      'public/**',
      'styles/**',
      'constants/**'
    ],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'text-summary', 'json', 'json-summary', 'html', 'lcov'], 
      reportsDirectory: './coverage',
      include: [
        'common/**/*.{js,jsx,ts,tsx}',
        'modules/**/services/**/*.{test,spec}.{js,jsx,ts,tsx}',
        'utils/**/*.{js,jsx,ts,tsx}'
      ],
      exclude: [
        'test/**',
        '**/*.{test,spec}.{js,jsx,ts,tsx}',
        '**/node_modules/**',
        '**/.next/**',
        '**/constants/**',
        '**/public/**',
        '**/styles/**'
      ]
    },
    globals: true,
    deps: {
      inline: ['next/router', 'next/link'], 
    }
  }
})