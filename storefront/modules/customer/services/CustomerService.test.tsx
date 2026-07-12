import { beforeEach, describe, expect, test, vi } from 'vitest';
import { YasError } from '@/common/services/errors/YasError';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import {
  chooseDefaultAddress,
  createUserAddress,
  deleteUserAddress,
  getUserAddress,
  getUserAddressDefault,
} from './CustomerService';

describe('CustomerService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('createUserAddress should return json when ok', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue({ id: 1 }),
    } as unknown as Response);

    const result = await createUserAddress({ city: 'HN' } as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/customer/storefront/user-address',
      JSON.stringify({ city: 'HN' })
    );
    expect(result).toEqual({ id: 1 });
  });

  test('createUserAddress should throw YasError when response not ok', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      ok: false,
      json: vi.fn().mockResolvedValue({ detail: 'invalid address' }),
    } as unknown as Response);

    await expect(createUserAddress({} as never)).rejects.toBeInstanceOf(YasError);
  });

  test('getUserAddress should return response json', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue([{ id: 10 }]),
    } as unknown as Response);

    await expect(getUserAddress()).resolves.toEqual([{ id: 10 }]);
    expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/storefront/user-address');
  });

  test('getUserAddressDefault should throw on non-2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 404,
      statusText: 'Not Found',
    } as unknown as Response);

    await expect(getUserAddressDefault()).rejects.toThrow('Not Found');
  });

  test('deleteUserAddress should call delete endpoint and return response', async () => {
    const response = { status: 204 } as Response;
    vi.mocked(apiClientService.delete).mockResolvedValue(response);

    const result = await deleteUserAddress(2);

    expect(apiClientService.delete).toHaveBeenCalledWith('/api/customer/storefront/user-address/2');
    expect(result).toBe(response);
  });

  test('chooseDefaultAddress should call put with null payload', async () => {
    const response = { status: 204 } as Response;
    vi.mocked(apiClientService.put).mockResolvedValue(response);

    const result = await chooseDefaultAddress(3);

    expect(apiClientService.put).toHaveBeenCalledWith(
      '/api/customer/storefront/user-address/3',
      null
    );
    expect(result).toBe(response);
  });
});
