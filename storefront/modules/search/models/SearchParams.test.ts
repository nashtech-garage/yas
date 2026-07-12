import { describe, it, expect } from 'vitest';
import type { SearchParams } from './SearchParams';
import { ESortType } from './SortType';

describe('SearchParams type', () => {
  it('should create a valid SearchParams object with only required keyword', () => {
    const params: SearchParams = {
      keyword: 'shoes',
    };

    expect(params.keyword).toBe('shoes');
    expect(params.brand).toBeUndefined();
    expect(params.category).toBeUndefined();
  });

  it('should create a valid SearchParams object with all optional fields', () => {
    const params: SearchParams = {
      keyword: 'shoes',
      brand: 'Nike',
      category: 'Running',
      attribute: 'waterproof',
      minPrice: 50,
      maxPrice: 200,
      sortType: ESortType.priceAsc,
      page: 1,
      pageSize: 20,
    };

    expect(params.keyword).toBe('shoes');
    expect(params.brand).toBe('Nike');
    expect(params.category).toBe('Running');
    expect(params.attribute).toBe('waterproof');
    expect(params.minPrice).toBe(50);
    expect(params.maxPrice).toBe(200);
    expect(params.sortType).toBe(ESortType.priceAsc);
    expect(params.page).toBe(1);
    expect(params.pageSize).toBe(20);
  });

  it('should allow empty string as keyword', () => {
    const params: SearchParams = {
      keyword: '',
    };

    expect(params.keyword).toBe('');
  });

  it('should allow partial optional fields', () => {
    const params: SearchParams = {
      keyword: 'laptop',
      brand: 'Apple',
      minPrice: 800,
    };

    expect(params.keyword).toBe('laptop');
    expect(params.brand).toBe('Apple');
    expect(params.minPrice).toBe(800);
    expect(params.category).toBeUndefined();
    expect(params.maxPrice).toBeUndefined();
  });

  it('should allow sorting by price ascending', () => {
    const params: SearchParams = {
      keyword: 'phones',
      sortType: ESortType.priceAsc,
    };

    expect(params.sortType).toBe(ESortType.priceAsc);
  });

  it('should allow sorting by price descending', () => {
    const params: SearchParams = {
      keyword: 'phones',
      sortType: ESortType.priceDesc,
    };

    expect(params.sortType).toBe(ESortType.priceDesc);
  });

  it('should allow default sort type', () => {
    const params: SearchParams = {
      keyword: 'phones',
      sortType: ESortType.default,
    };

    expect(params.sortType).toBe(ESortType.default);
  });

  it('should allow page and pageSize pagination parameters', () => {
    const params: SearchParams = {
      keyword: 'products',
      page: 3,
      pageSize: 50,
    };

    expect(params.page).toBe(3);
    expect(params.pageSize).toBe(50);
  });

  it('should allow zero as minPrice', () => {
    const params: SearchParams = {
      keyword: 'free',
      minPrice: 0,
    };

    expect(params.minPrice).toBe(0);
  });

  it('should allow large maxPrice values', () => {
    const params: SearchParams = {
      keyword: 'luxury',
      maxPrice: 999999.99,
    };

    expect(params.maxPrice).toBe(999999.99);
  });

  it('should allow category and brand filters together', () => {
    const params: SearchParams = {
      keyword: 'running',
      category: 'Shoes',
      brand: 'Nike',
    };

    expect(params.category).toBe('Shoes');
    expect(params.brand).toBe('Nike');
  });

  it('should allow attribute filter with special characters', () => {
    const params: SearchParams = {
      keyword: 'shoes',
      attribute: 'water-resistant & UV-blocking',
    };

    expect(params.attribute).toContain('water-resistant');
    expect(params.attribute).toContain('UV-blocking');
  });

  it('should construct query string from SearchParams', () => {
    const params: SearchParams = {
      keyword: 'shoes',
      brand: 'Nike',
      minPrice: 50,
      maxPrice: 200,
      page: 1,
    };

    const queryParams = new URLSearchParams();
    queryParams.append('keyword', params.keyword);
    if (params.brand) queryParams.append('brand', params.brand);
    if (params.minPrice !== undefined) queryParams.append('minPrice', params.minPrice.toString());
    if (params.maxPrice !== undefined) queryParams.append('maxPrice', params.maxPrice.toString());
    if (params.page !== undefined) queryParams.append('page', params.page.toString());

    expect(queryParams.get('keyword')).toBe('shoes');
    expect(queryParams.get('brand')).toBe('Nike');
    expect(queryParams.get('minPrice')).toBe('50');
    expect(queryParams.get('maxPrice')).toBe('200');
    expect(queryParams.get('page')).toBe('1');
  });

  it('should validate price range (minPrice <= maxPrice)', () => {
    const params: SearchParams = {
      keyword: 'products',
      minPrice: 100,
      maxPrice: 50,
    };

    const isValidRange = !params.maxPrice || !params.minPrice || params.minPrice <= params.maxPrice;
    expect(isValidRange).toBe(false); // This test validates the logic works
  });

  it('should allow page 1 as first page', () => {
    const params: SearchParams = {
      keyword: 'items',
      page: 1,
    };

    expect(params.page).toBe(1);
  });

  it('should allow large page numbers', () => {
    const params: SearchParams = {
      keyword: 'items',
      page: 9999,
      pageSize: 10,
    };

    expect(params.page).toBe(9999);
  });

  it('should allow multiple filter combinations', () => {
    const scenarios = [
      {
        keyword: 'shirt',
        category: 'Clothing',
        brand: 'Nike',
        minPrice: 20,
        sortType: ESortType.priceAsc,
      },
      {
        keyword: 'laptop',
        brand: 'Dell',
        maxPrice: 1500,
        sortType: ESortType.priceDesc,
      },
      {
        keyword: 'phone',
        brand: 'Apple',
        category: 'Electronics',
        attribute: 'wireless',
        page: 2,
      },
    ];

    scenarios.forEach((scenario) => {
      const params: SearchParams = scenario;
      expect(params.keyword).toBeTruthy();
    });
  });
});
