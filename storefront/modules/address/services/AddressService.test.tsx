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
import { createAddress, deleteAddress, getAddress, updateAddress } from './AddressService';

describe('AddressService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('createAddress should post payload and return json', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      json: vi.fn().mockResolvedValue({ id: 1 }),
    } as unknown as Response);

    const result = await createAddress({ name: 'home' } as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/location/storefront/addresses',
      JSON.stringify({ name: 'home' })
    );
    expect(result).toEqual({ id: 1 });
  });

  test('updateAddress should call put and return response', async () => {
    const response = { status: 204 } as Response;
    vi.mocked(apiClientService.put).mockResolvedValue(response);

    const result = await updateAddress('12', { city: 'HN' } as never);

    expect(apiClientService.put).toHaveBeenCalledWith(
      '/api/location/storefront/addresses/12',
      JSON.stringify({ city: 'HN' })
    );
    expect(result).toBe(response);
  });

  test('getAddress should throw YasError when response is not ok', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      ok: false,
      json: vi.fn().mockResolvedValue({ detail: 'not found' }),
    } as unknown as Response);

    await expect(getAddress('99')).rejects.toBeInstanceOf(YasError);
  });

  test('getAddress should return json result when response is ok', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue({ id: 99 }),
    } as unknown as Response);

    await expect(getAddress('99')).resolves.toEqual({ id: 99 });
  });

  test('deleteAddress should call delete endpoint', async () => {
    const response = { status: 204 } as Response;
    vi.mocked(apiClientService.delete).mockResolvedValue(response);

    const result = await deleteAddress(7);

    expect(apiClientService.delete).toHaveBeenCalledWith('/api/location/storefront/addresses/7');
    expect(result).toBe(response);
  });
});
