import { describe, it, expect } from 'vitest';
import type { SearchProductResponse } from './SearchProductResponse';
import type { ProductSearchResult } from './ProductSearchResult';
import type { Aggregations } from './Aggregations';

describe('SearchProductResponse type', () => {
  it('should create a valid SearchProductResponse with no products', () => {
    const response: SearchProductResponse = {
      products: [],
      pageNo: 1,
      pageSize: 20,
      totalElements: 0,
      totalPages: 0,
      isLast: true,
      aggregations: {},
    };

    expect(response.products).toEqual([]);
    expect(response.pageNo).toBe(1);
    expect(response.pageSize).toBe(20);
    expect(response.totalElements).toBe(0);
    expect(response.totalPages).toBe(0);
    expect(response.isLast).toBe(true);
    expect(response.aggregations).toEqual({});
  });

  it('should create a valid SearchProductResponse with products', () => {
    const response: SearchProductResponse = {
      products: [
        { id: 1, name: 'Product 1', slug: 'product-1', thumbnailId: 101, price: 29.99 },
        { id: 2, name: 'Product 2', slug: 'product-2', thumbnailId: 102, price: 49.99 },
      ],
      pageNo: 1,
      pageSize: 20,
      totalElements: 2,
      totalPages: 1,
      isLast: true,
      aggregations: {
        brand: { 'Brand A': 1, 'Brand B': 1 },
      },
    };

    expect(response.products).toHaveLength(2);
    expect(response.products[0].id).toBe(1);
    expect(response.products[1].id).toBe(2);
    expect(response.totalElements).toBe(2);
    expect(response.totalPages).toBe(1);
  });

  it('should allow pagination with pageNo and pageSize', () => {
    const response: SearchProductResponse = {
      products: Array.from({ length: 50 }, (_, i) => ({
        id: i + 1,
        name: `Product ${i + 1}`,
        slug: `product-${i + 1}`,
        thumbnailId: 1000 + i,
        price: 10 + i,
      })),
      pageNo: 2,
      pageSize: 50,
      totalElements: 200,
      totalPages: 4,
      isLast: false,
      aggregations: {},
    };

    expect(response.pageNo).toBe(2);
    expect(response.pageSize).toBe(50);
    expect(response.totalElements).toBe(200);
    expect(response.totalPages).toBe(4);
    expect(response.isLast).toBe(false);
  });

  it('should mark as last page when appropriate', () => {
    const response: SearchProductResponse = {
      products: [{ id: 1, name: 'Last Product', slug: 'last', thumbnailId: 1, price: 99 }],
      pageNo: 5,
      pageSize: 20,
      totalElements: 100,
      totalPages: 5,
      isLast: true,
      aggregations: {},
    };

    expect(response.isLast).toBe(true);
    expect(response.pageNo).toBe(response.totalPages);
  });

  it('should not mark as last page when more pages available', () => {
    const response: SearchProductResponse = {
      products: Array.from({ length: 20 }, (_, i) => ({
        id: i + 1,
        name: `Product ${i + 1}`,
        slug: `product-${i + 1}`,
        thumbnailId: 1000 + i,
        price: 50,
      })),
      pageNo: 1,
      pageSize: 20,
      totalElements: 100,
      totalPages: 5,
      isLast: false,
      aggregations: {},
    };

    expect(response.isLast).toBe(false);
    expect(response.pageNo).not.toBe(response.totalPages);
  });

  it('should include brand aggregations', () => {
    const response: SearchProductResponse = {
      products: [],
      pageNo: 1,
      pageSize: 20,
      totalElements: 50,
      totalPages: 3,
      isLast: false,
      aggregations: {
        brand: {
          Nike: 20,
          Adidas: 15,
          Puma: 15,
        },
      },
    };

    expect(response.aggregations.brand).toBeDefined();
    expect(response.aggregations.brand['Nike']).toBe(20);
    expect(response.aggregations.brand['Adidas']).toBe(15);
  });

  it('should include category aggregations', () => {
    const response: SearchProductResponse = {
      products: [],
      pageNo: 1,
      pageSize: 20,
      totalElements: 100,
      totalPages: 5,
      isLast: false,
      aggregations: {
        category: {
          Shoes: 40,
          Apparel: 35,
          Accessories: 25,
        },
      },
    };

    expect(response.aggregations.category).toBeDefined();
    expect(response.aggregations.category['Shoes']).toBe(40);
  });

  it('should include multiple aggregations', () => {
    const response: SearchProductResponse = {
      products: [],
      pageNo: 1,
      pageSize: 20,
      totalElements: 50,
      totalPages: 3,
      isLast: false,
      aggregations: {
        brand: { Nike: 20, Adidas: 30 },
        category: { Shoes: 50 },
        size: { M: 25, L: 25 },
      },
    };

    expect(Object.keys(response.aggregations)).toHaveLength(3);
    expect(response.aggregations.brand).toBeDefined();
    expect(response.aggregations.category).toBeDefined();
    expect(response.aggregations.size).toBeDefined();
  });

  it('should calculate if more products available', () => {
    const response: SearchProductResponse = {
      products: Array.from({ length: 20 }, (_, i) => ({
        id: i + 1,
        name: `Product ${i + 1}`,
        slug: `product-${i + 1}`,
        thumbnailId: 1000 + i,
        price: 50,
      })),
      pageNo: 1,
      pageSize: 20,
      totalElements: 100,
      totalPages: 5,
      isLast: false,
      aggregations: {},
    };

    const hasMoreProducts =
      response.pageNo < response.totalPages || response.products.length === response.pageSize;
    expect(hasMoreProducts).toBe(true);
  });

  it('should handle single product in response', () => {
    const response: SearchProductResponse = {
      products: [
        { id: 999, name: 'Unique Product', slug: 'unique', thumbnailId: 999, price: 999.99 },
      ],
      pageNo: 1,
      pageSize: 20,
      totalElements: 1,
      totalPages: 1,
      isLast: true,
      aggregations: {
        brand: { 'Unique Brand': 1 },
      },
    };

    expect(response.products).toHaveLength(1);
    expect(response.totalElements).toBe(1);
    expect(response.isLast).toBe(true);
  });

  it('should handle large pageSize values', () => {
    const response: SearchProductResponse = {
      products: Array.from({ length: 100 }, (_, i) => ({
        id: i + 1,
        name: `Product ${i + 1}`,
        slug: `product-${i + 1}`,
        thumbnailId: 1000 + i,
        price: i * 10,
      })),
      pageNo: 1,
      pageSize: 100,
      totalElements: 100,
      totalPages: 1,
      isLast: true,
      aggregations: {},
    };

    expect(response.pageSize).toBe(100);
    expect(response.products).toHaveLength(100);
  });

  it('should allow filtering products from response', () => {
    const response: SearchProductResponse = {
      products: [
        { id: 1, name: 'Cheap Item', slug: 'cheap', thumbnailId: 1, price: 10 },
        { id: 2, name: 'Mid Item', slug: 'mid', thumbnailId: 2, price: 50 },
        { id: 3, name: 'Expensive Item', slug: 'expensive', thumbnailId: 3, price: 100 },
      ],
      pageNo: 1,
      pageSize: 20,
      totalElements: 3,
      totalPages: 1,
      isLast: true,
      aggregations: {},
    };

    const affordable = response.products.filter((p) => p.price <= 60);
    expect(affordable).toHaveLength(2);
  });

  it('should preserve product order in response', () => {
    const products: ProductSearchResult[] = Array.from({ length: 5 }, (_, i) => ({
      id: i + 1,
      name: `Product ${i + 1}`,
      slug: `product-${i + 1}`,
      thumbnailId: 1000 + i,
      price: (i + 1) * 10,
    }));

    const response: SearchProductResponse = {
      products,
      pageNo: 1,
      pageSize: 20,
      totalElements: 5,
      totalPages: 1,
      isLast: true,
      aggregations: {},
    };

    expect(response.products[0].price).toBe(10);
    expect(response.products[4].price).toBe(50);
  });
});
