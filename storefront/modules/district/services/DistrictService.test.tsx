import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { getDistricts } from './DistrictService';

describe('DistrictService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getDistricts should call endpoint and return json', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue([{ id: 1 }]),
    } as unknown as Response);

    const result = await getDistricts(11);

    expect(apiClientService.get).toHaveBeenCalledWith('/api/location/storefront/district/11');
    expect(result).toEqual([{ id: 1 }]);
  });
});
