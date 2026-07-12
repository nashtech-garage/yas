import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { getCategories, getCategoriesSuggestions, getCategory } from './CategoryService';

describe('CategoryService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getCategories should call correct endpoint and return json', async () => {
    const json = vi.fn().mockResolvedValue([{ id: 1, name: 'Phone' }]);
    vi.mocked(apiClientService.get).mockResolvedValue({ json } as unknown as Response);

    const result = await getCategories();

    expect(apiClientService.get).toHaveBeenCalledWith('/api/product/storefront/categories');
    expect(result).toEqual([{ id: 1, name: 'Phone' }]);
  });

  test('getCategory should call category by id endpoint', async () => {
    const json = vi.fn().mockResolvedValue({ id: 10, name: 'Laptop' });
    vi.mocked(apiClientService.get).mockResolvedValue({ json } as unknown as Response);

    const result = await getCategory(10);

    expect(apiClientService.get).toHaveBeenCalledWith('/api/product/storefront/categories/10');
    expect(result).toEqual({ id: 10, name: 'Laptop' });
  });

  test('getCategoriesSuggestions should call suggestions endpoint', async () => {
    const json = vi.fn().mockResolvedValue(['Phone', 'Laptop']);
    vi.mocked(apiClientService.get).mockResolvedValue({ json } as unknown as Response);

    const result = await getCategoriesSuggestions();

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/product/storefront/categories/suggestions'
    );
    expect(result).toEqual(['Phone', 'Laptop']);
  });
});
