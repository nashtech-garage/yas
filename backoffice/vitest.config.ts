import { defineConfig } from 'vitest/config';
import path from 'path';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './vitest.setup.ts',
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: ['pages/**', '**/*.test.ts', '**/*.test.tsx', 'next.config.js', '**/models/**'],
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './'),
      '@commonServices': path.resolve(__dirname, './common/services'),
      '@taxModels': path.resolve(__dirname, './modules/tax/models'),
      '@webhookModels': path.resolve(__dirname, './modules/webhook/models'),
    },
  },
});
