import {
  getCategories,
  getCategoriesSuggestions,
  getCategory,
} from '@/modules/catalog/services/CategoryService';
import apiClientService from '@/common/services/ApiClientService';

jest.mock('@/common/services/ApiClientService', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
  },
}));

const mockedApiClient = apiClientService as jest.Mocked<typeof apiClientService>;

describe('CategoryService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('gets all categories', async () => {
    const responseJson = [{ id: 1, name: 'Phones' }];
    mockedApiClient.get.mockResolvedValue({
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await getCategories();

    expect(mockedApiClient.get).toHaveBeenCalledWith('/api/product/storefront/categories');
    expect(result).toEqual(responseJson);
  });

  it('gets a category by id', async () => {
    const responseJson = { id: 2, name: 'Laptops' };
    mockedApiClient.get.mockResolvedValue({
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await getCategory(2);

    expect(mockedApiClient.get).toHaveBeenCalledWith('/api/product/storefront/categories/2');
    expect(result).toEqual(responseJson);
  });

  it('gets category suggestions', async () => {
    const responseJson = ['Phones', 'Laptops'];
    mockedApiClient.get.mockResolvedValue({
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await getCategoriesSuggestions();

    expect(mockedApiClient.get).toHaveBeenCalledWith(
      '/api/product/storefront/categories/suggestions'
    );
    expect(result).toEqual(responseJson);
  });
});
