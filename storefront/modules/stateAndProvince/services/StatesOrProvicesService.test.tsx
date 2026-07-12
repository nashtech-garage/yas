import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { getStatesOrProvinces } from './StatesOrProvicesService';

describe('StatesOrProvicesService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getStatesOrProvinces should call endpoint and return json', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue([{ id: 1 }]),
    } as unknown as Response);

    const result = await getStatesOrProvinces(84);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/location/storefront/state-or-provinces/84'
    );
    expect(result).toEqual([{ id: 1 }]);
  });
});
