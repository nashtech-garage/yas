import apiClientService from '@/common/services/ApiClientService';

import { getSuggestions, searchProducts } from '../SearchService';

jest.mock('@/common/services/ApiClientService', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
  },
}));

describe('SearchService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('getSuggestions returns data when response is successful', async () => {
    const json = jest.fn().mockResolvedValue({ products: ['iphone'] });
    (apiClientService.get as jest.Mock).mockResolvedValue({ status: 200, json });

    const result = await getSuggestions('iphone');

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/search/storefront/search_suggest?keyword=iphone'
    );
    expect(result).toEqual({ products: ['iphone'] });
  });

  it('getSuggestions throws when response is not successful', async () => {
    (apiClientService.get as jest.Mock).mockResolvedValue({
      status: 500,
      statusText: 'Server Error',
    });

    await expect(getSuggestions('iphone')).rejects.toThrow('Server Error');
  });

  it('searchProducts builds query from optional params', async () => {
    const json = jest.fn().mockResolvedValue({ products: [], totalElements: 0 });
    (apiClientService.get as jest.Mock).mockResolvedValue({ status: 200, json });

    await searchProducts({
      keyword: 'phone',
      category: 'mobile',
      brand: 'apple',
      attribute: 'ram:8gb',
      minPrice: 100,
      maxPrice: 200,
      sortType: 'PRICE_ASC' as any,
      page: 2,
      pageSize: 12,
    });

    expect(apiClientService.get).toHaveBeenCalledWith(
      'api/search/storefront/catalog-search?keyword=phone&category=mobile&brand=apple&attribute=ram:8gb&minPrice=100&maxPrice=200&sortType=PRICE_ASC&page=2&pageSize=12'
    );
  });

  it('searchProducts throws when response is not successful', async () => {
    (apiClientService.get as jest.Mock).mockResolvedValue({ status: 404, statusText: 'Not Found' });

    await expect(searchProducts({ keyword: 'missing' })).rejects.toThrow('Not Found');
  });
});
