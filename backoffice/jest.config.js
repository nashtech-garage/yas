/** @type {import('jest').Config} */
const config = {
  preset: 'ts-jest',
  testEnvironment: 'jest-environment-jsdom',
  setupFilesAfterEnv: ['<rootDir>/jest.setup.ts'],
  moduleNameMapper: {
    '^@commonServices/(.*)$': '<rootDir>/common/services/$1',
    '^@commonItems/(.*)$': '<rootDir>/common/items/$1',
    '^@locationComponents/(.*)$': '<rootDir>/modules/location/components/$1',
    '^@locationModels/(.*)$': '<rootDir>/modules/location/models/$1',
    '^@locationServices/(.*)$': '<rootDir>/modules/location/services/$1',
    '^@taxServices/(.*)$': '<rootDir>/modules/tax/services/$1',
    '^@taxComponents/(.*)$': '<rootDir>/modules/tax/components/$1',
    '^@taxModels/(.*)$': '<rootDir>/modules/tax/models/$1',
    '^@constants/(.*)$': '<rootDir>/constants/$1',
    '^@catalogModels/(.*)$': '<rootDir>/modules/catalog/models/$1',
    '^@catalogServices/(.*)$': '<rootDir>/modules/catalog/services/$1',
    '^@catalogComponents/(.*)$': '<rootDir>/modules/catalog/components/$1',
    '^@inventoryServices/(.*)$': '<rootDir>/modules/inventory/services/$1',
    '^@inventoryModels/(.*)$': '<rootDir>/modules/inventory/models/$1',
    '^@inventoryComponents/(.*)$': '<rootDir>/modules/inventory/components/$1',
    '^@webhookComponents/(.*)$': '<rootDir>/modules/webhook/components/$1',
    '^@webhookServices/(.*)$': '<rootDir>/modules/webhook/services/$1',
    '^@webhookModels/(.*)$': '<rootDir>/modules/webhook/models/$1',
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
    '\\.(png|jpg|jpeg|gif|svg|webp)$': '<rootDir>/__mocks__/fileMock.js',
  },
  testMatch: ['**/__tests__/**/*.test.ts', '**/__tests__/**/*.test.tsx'],
  collectCoverageFrom: [
    'utils/**/*.{ts,tsx}',
    'common/services/**/*.{ts,tsx}',
    'constants/**/*.{ts,tsx}',
    '!**/*.d.ts',
    '!**/node_modules/**',
  ],
  coverageThreshold: {
    global: {
      lines: 70,
      functions: 70,
      branches: 70,
      statements: 70,
    },
  },
  coverageReporters: ['text', 'lcov', 'json-summary'],
  transform: {
    '^.+\\.(ts|tsx)$': [
      'ts-jest',
      {
        tsconfig: {
          jsx: 'react',
        },
      },
    ],
  },
};

module.exports = config;
