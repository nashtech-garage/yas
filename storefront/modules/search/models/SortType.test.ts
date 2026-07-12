import { describe, it, expect } from 'vitest';
import { SortType, ESortType } from './SortType';

describe('SortType', () => {
  it('should export SortType with correct default value', () => {
    expect(SortType.DEFAULT).toBe('default');
  });

  it('should export SortType with correct price ascending value', () => {
    expect(SortType.PRICE_ASC).toBe('priceAsc');
  });

  it('should export SortType with correct price descending value', () => {
    expect(SortType.PRICE_DESC).toBe('priceDesc');
  });

  it('should have three properties in SortType', () => {
    expect(Object.keys(SortType)).toHaveLength(3);
    expect(Object.keys(SortType)).toContain('DEFAULT');
    expect(Object.keys(SortType)).toContain('PRICE_ASC');
    expect(Object.keys(SortType)).toContain('PRICE_DESC');
  });
});

describe('ESortType', () => {
  it('should export ESortType enum with default value', () => {
    expect(ESortType.default).toBe('DEFAULT');
  });

  it('should export ESortType enum with price ascending value', () => {
    expect(ESortType.priceAsc).toBe('PRICE_ASC');
  });

  it('should export ESortType enum with price descending value', () => {
    expect(ESortType.priceDesc).toBe('PRICE_DESC');
  });

  it('should have three enum members', () => {
    // String enums only have forward mapping (names to values)
    expect(Object.keys(ESortType)).toHaveLength(3);
  });

  it('should correctly map enum names to values', () => {
    expect(ESortType['default']).toBe('DEFAULT');
    expect(ESortType['priceAsc']).toBe('PRICE_ASC');
    expect(ESortType['priceDesc']).toBe('PRICE_DESC');
  });

  it('should have uppercase string values', () => {
    const defaultValue = ESortType.default;
    const ascValue = ESortType.priceAsc;
    const descValue = ESortType.priceDesc;

    expect(defaultValue).toBe('DEFAULT');
    expect(ascValue).toBe('PRICE_ASC');
    expect(descValue).toBe('PRICE_DESC');
  });
});

describe('SortType and ESortType relationship', () => {
  it('should have matching default and priceAsc, priceDesc values', () => {
    // SortType uses camelCase while ESortType uses UPPERCASE_SNAKE_CASE
    expect(SortType.DEFAULT).toBe('default');
    expect(SortType.PRICE_ASC).toBe('priceAsc');
    expect(SortType.PRICE_DESC).toBe('priceDesc');
  });

  it('should have distinct values between SortType and ESortType', () => {
    // They serve different purposes - SortType for API, ESortType for enum typing
    expect(SortType.DEFAULT).not.toBe(ESortType.default);
    expect(SortType.PRICE_ASC).not.toBe(ESortType.priceAsc);
    expect(SortType.PRICE_DESC).not.toBe(ESortType.priceDesc);
  });
});
