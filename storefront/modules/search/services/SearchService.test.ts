import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { getSuggestions, searchProducts } from './SearchService';

describe('SearchService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getSuggestions should return json on success', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 200,
      json: vi.fn().mockResolvedValue({ data: ['iphone'] }),
    } as unknown as Response);

    const result = await getSuggestions('iphone');

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/search/storefront/search_suggest?keyword=iphone'
    );
    expect(result).toEqual({ data: ['iphone'] });
  });

  test('getSuggestions should throw Error on failure', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 500,
      statusText: 'Internal Server Error',
    } as unknown as Response);

    await expect(getSuggestions('iphone')).rejects.toThrow('Internal Server Error');
  });

  test('searchProducts should build all optional query params', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 201,
      json: vi.fn().mockResolvedValue({ content: [] }),
    } as unknown as Response);

    const params = {
      keyword: 'phone',
      category: 'mobile',
      brand: 'apple',
      attribute: 'ram-8',
      minPrice: 10,
      maxPrice: 100,
      sortType: 'price_asc',
      page: 2,
      pageSize: 12,
    };

    const result = await searchProducts(params);

    expect(apiClientService.get).toHaveBeenCalledWith(
      'api/search/storefront/catalog-search?keyword=phone&category=mobile&brand=apple&attribute=ram-8&minPrice=10&maxPrice=100&sortType=price_asc&page=2&pageSize=12'
    );
    expect(result).toEqual({ content: [] });
  });

  test('searchProducts should keep only required keyword when optional values are missing', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 200,
      json: vi.fn().mockResolvedValue({ content: [] }),
    } as unknown as Response);

    await searchProducts({ keyword: 'phone' });

    expect(apiClientService.get).toHaveBeenCalledWith(
      'api/search/storefront/catalog-search?keyword=phone'
    );
  });

  test('searchProducts should throw Error on non-2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 401,
      statusText: 'Unauthorized',
    } as unknown as Response);

    await expect(searchProducts({ keyword: 'abc' })).rejects.toThrow('Unauthorized');
  });
});
