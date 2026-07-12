import { describe, it, expect } from 'vitest';
import type { Aggregations } from './Aggregations';

describe('Aggregations type', () => {
  it('should allow empty aggregations object', () => {
    const aggregations: Aggregations = {};
    expect(aggregations).toEqual({});
  });

  it('should allow aggregations with brand data', () => {
    const aggregations: Aggregations = {
      brand: {
        Nike: 25,
        Adidas: 18,
        Puma: 12,
      },
    };
    expect(aggregations.brand).toBeDefined();
    expect(aggregations.brand['Nike']).toBe(25);
    expect(aggregations.brand['Adidas']).toBe(18);
    expect(aggregations.brand['Puma']).toBe(12);
  });

  it('should allow aggregations with category data', () => {
    const aggregations: Aggregations = {
      category: {
        Electronics: 45,
        Clothing: 32,
        Books: 18,
      },
    };
    expect(aggregations.category).toBeDefined();
    expect(aggregations.category['Electronics']).toBe(45);
  });

  it('should allow aggregations with multiple facets', () => {
    const aggregations: Aggregations = {
      brand: { Nike: 25 },
      category: { Shoes: 18 },
      price: { '100': 10 },
    };
    expect(Object.keys(aggregations)).toHaveLength(3);
    expect(aggregations.brand).toBeDefined();
    expect(aggregations.category).toBeDefined();
    expect(aggregations.price).toBeDefined();
  });

  it('should allow aggregations with numeric keys', () => {
    const aggregations: Aggregations = {
      sizes: {
        '5': 10,
        '6': 15,
        '7': 20,
        '8': 12,
      },
    };
    expect(aggregations.sizes['5']).toBe(10);
    expect(aggregations.sizes['8']).toBe(12);
  });

  it('should allow aggregations with zero counts', () => {
    const aggregations: Aggregations = {
      brand: {
        LimitedBrand: 0,
        PopularBrand: 50,
      },
    };
    expect(aggregations.brand['LimitedBrand']).toBe(0);
    expect(aggregations.brand['PopularBrand']).toBe(50);
  });

  it('should allow dynamic aggregations with any string keys', () => {
    const aggregations: Aggregations = {
      customFacet: {
        value1: 100,
        value2: 200,
        value3: 300,
      },
    };
    expect(aggregations.customFacet['value1']).toBe(100);
    expect(aggregations.customFacet['value2']).toBe(200);
    expect(aggregations.customFacet['value3']).toBe(300);
  });
});
