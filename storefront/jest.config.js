const nextJest = require('next/jest');

const createJestConfig = nextJest({
  dir: './',
});

const customJestConfig = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/jest.setup.ts'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/$1',
  },
  collectCoverage: true,
  collectCoverageFrom: [
    'common/services/ApiClientService.ts',
    'common/services/errors/YasError.ts',
    'modules/cart/services/CartService.ts',
    'modules/catalog/services/CategoryService.ts',
    'modules/payment/services/PaymentProviderService.ts',
    'modules/search/services/SearchService.ts',
    'utils/concatQueryString.ts',
    'utils/formatPrice.ts',
    'utils/orderUtil.ts',
  ],
  coverageDirectory: 'coverage',
  coverageReporters: ['text', 'json-summary', 'cobertura', 'lcov'],
};

module.exports = createJestConfig(customJestConfig);
