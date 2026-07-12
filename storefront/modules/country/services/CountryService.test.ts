import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { getCountries } from './CountryService';

describe('CountryService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getCountries should call countries endpoint and return json', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue([{ id: 1, name: 'VN' }]),
    } as unknown as Response);

    const result = await getCountries();

    expect(apiClientService.get).toHaveBeenCalledWith('/api/location/storefront/countries');
    expect(result).toEqual([{ id: 1, name: 'VN' }]);
  });
});
