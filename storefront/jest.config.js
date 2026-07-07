/** @type {import('ts-jest').JestConfigWithTsJest} */
module.exports = {
  testEnvironment: 'node',
  transform: {
    '^.+\\.tsx?$': ['ts-jest', {
      tsconfig: {
        module: 'commonjs',
        jsx: 'react',
      },
    }],
  },
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/$1',
  },
  testMatch: [
    '**/__tests__/**/*.test.ts',
    '**/__tests__/**/*.test.tsx',
  ],
  collectCoverageFrom: [
    'utils/**/*.{ts,tsx}',
    '!utils/**/*.d.ts',
    '!utils/**/__tests__/**',
    '!utils/useDebounce.ts',
    '!**/node_modules/**',
  ],
  coverageReporters: ['text', 'lcov', 'json'],
  coverageThreshold: {
    global: {
      lines: 70,
      functions: 70,
      statements: 70,
    },
  },
};
