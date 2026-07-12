import { describe, it, expect } from 'vitest';
import type { ProductSearchResult } from './ProductSearchResult';

describe('ProductSearchResult type', () => {
  it('should create a valid ProductSearchResult object', () => {
    const result: ProductSearchResult = {
      id: 1,
      name: 'Test Product',
      slug: 'test-product',
      thumbnailId: 101,
      price: 99.99,
    };

    expect(result.id).toBe(1);
    expect(result.name).toBe('Test Product');
    expect(result.slug).toBe('test-product');
    expect(result.thumbnailId).toBe(101);
    expect(result.price).toBe(99.99);
  });

  it('should allow multiple ProductSearchResult objects in an array', () => {
    const results: ProductSearchResult[] = [
      {
        id: 1,
        name: 'Product 1',
        slug: 'product-1',
        thumbnailId: 101,
        price: 29.99,
      },
      {
        id: 2,
        name: 'Product 2',
        slug: 'product-2',
        thumbnailId: 102,
        price: 49.99,
      },
      {
        id: 3,
        name: 'Product 3',
        slug: 'product-3',
        thumbnailId: 103,
        price: 79.99,
      },
    ];

    expect(results).toHaveLength(3);
    expect(results[0].id).toBe(1);
    expect(results[1].id).toBe(2);
    expect(results[2].id).toBe(3);
  });

  it('should handle zero price products', () => {
    const result: ProductSearchResult = {
      id: 100,
      name: 'Free Product',
      slug: 'free-product',
      thumbnailId: 200,
      price: 0,
    };

    expect(result.price).toBe(0);
  });

  it('should handle large numeric IDs', () => {
    const result: ProductSearchResult = {
      id: 999999999,
      name: 'Big ID Product',
      slug: 'big-id-product',
      thumbnailId: 888888888,
      price: 199.99,
    };

    expect(result.id).toBe(999999999);
    expect(result.thumbnailId).toBe(888888888);
  });

  it('should handle special characters in name and slug', () => {
    const result: ProductSearchResult = {
      id: 5,
      name: 'Product with Special Characters & Symbols!',
      slug: 'product-with-special-characters-symbols',
      thumbnailId: 105,
      price: 59.99,
    };

    expect(result.name).toContain('Special Characters');
    expect(result.slug).toContain('symbols');
  });

  it('should allow decimal prices with high precision', () => {
    const result: ProductSearchResult = {
      id: 6,
      name: 'Premium Product',
      slug: 'premium-product',
      thumbnailId: 106,
      price: 199.9999,
    };

    expect(result.price).toBe(199.9999);
  });

  it('should correctly represent product with negative ID edge case', () => {
    const result: ProductSearchResult = {
      id: -1,
      name: 'Edge Case Product',
      slug: 'edge-case',
      thumbnailId: 1,
      price: 10,
    };

    expect(result.id).toBe(-1);
  });

  it('should allow filtering ProductSearchResult by price range', () => {
    const results: ProductSearchResult[] = [
      { id: 1, name: 'Cheap', slug: 'cheap', thumbnailId: 1, price: 10 },
      { id: 2, name: 'Mid', slug: 'mid', thumbnailId: 2, price: 50 },
      { id: 3, name: 'Expensive', slug: 'expensive', thumbnailId: 3, price: 200 },
    ];

    const inRange = results.filter((r) => r.price >= 30 && r.price <= 100);
    expect(inRange).toHaveLength(1);
    expect(inRange[0].name).toBe('Mid');
  });

  it('should allow sorting ProductSearchResult by price', () => {
    const results: ProductSearchResult[] = [
      { id: 3, name: 'Expensive', slug: 'expensive', thumbnailId: 3, price: 200 },
      { id: 1, name: 'Cheap', slug: 'cheap', thumbnailId: 1, price: 10 },
      { id: 2, name: 'Mid', slug: 'mid', thumbnailId: 2, price: 50 },
    ];

    const sorted = [...results].sort((a, b) => a.price - b.price);
    expect(sorted[0].price).toBe(10);
    expect(sorted[1].price).toBe(50);
    expect(sorted[2].price).toBe(200);
  });
});
