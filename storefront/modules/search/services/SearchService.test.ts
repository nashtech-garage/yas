import { getSuggestions, searchProducts } from './SearchService';
import apiClientService from '@/common/services/ApiClientService';

jest.mock('@/common/services/ApiClientService', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
  },
}));

const mockedApiClient = apiClientService as jest.Mocked<typeof apiClientService>;

describe('SearchService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('gets suggestions successfully', async () => {
    const responseJson = { products: ['iphone'] };
    mockedApiClient.get.mockResolvedValue({
      status: 200,
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await getSuggestions('iphone');

    expect(mockedApiClient.get).toHaveBeenCalledWith(
      '/api/search/storefront/search_suggest?keyword=iphone'
    );
    expect(result).toEqual(responseJson);
  });

  it('throws when suggestions request fails', async () => {
    mockedApiClient.get.mockResolvedValue({
      status: 500,
      statusText: 'Internal Server Error',
    } as Response);

    await expect(getSuggestions('iphone')).rejects.toThrow('Internal Server Error');
  });

  it('builds a search URL with all supported filters', async () => {
    const responseJson = { products: [], total: 0 };
    mockedApiClient.get.mockResolvedValue({
      status: 200,
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await searchProducts({
      keyword: 'iphone',
      category: 'phones',
      brand: 'apple',
      attribute: 'color:black',
      minPrice: 100,
      maxPrice: 1000,
      sortType: 'price_desc',
      page: 2,
      pageSize: 20,
    });

    expect(mockedApiClient.get).toHaveBeenCalledWith(
      'api/search/storefront/catalog-search?keyword=iphone&category=phones&brand=apple&attribute=color:black&minPrice=100&maxPrice=1000&sortType=price_desc&page=2&pageSize=20'
    );
    expect(result).toEqual(responseJson);
  });

  it('throws when product search fails', async () => {
    mockedApiClient.get.mockResolvedValue({
      status: 404,
      statusText: 'Not Found',
    } as Response);

    await expect(searchProducts({ keyword: 'missing-product' })).rejects.toThrow('Not Found');
  });
});
