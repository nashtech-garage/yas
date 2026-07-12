import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    put: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { getMyProfile, updateCustomer } from './ProfileService';

describe('ProfileService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getMyProfile should return profile json', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue({ id: 1, name: 'Yas' }),
    } as unknown as Response);

    const result = await getMyProfile();

    expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/storefront/customer/profile');
    expect(result).toEqual({ id: 1, name: 'Yas' });
  });

  test('updateCustomer should return response when status is 204', async () => {
    const response = { status: 204 } as Response;
    vi.mocked(apiClientService.put).mockResolvedValue(response);

    const result = await updateCustomer({ name: 'New Name' } as never);

    expect(result).toBe(response);
  });

  test('updateCustomer should return json when status is not 204', async () => {
    vi.mocked(apiClientService.put).mockResolvedValue({
      status: 200,
      json: vi.fn().mockResolvedValue({ id: 1, name: 'Updated' }),
    } as unknown as Response);

    const result = await updateCustomer({ name: 'Updated' } as never);

    expect(result).toEqual({ id: 1, name: 'Updated' });
  });
});
