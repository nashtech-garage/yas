import apiClientService from '@/common/services/ApiClientService';
import { YasError } from '@/common/services/errors/YasError';

import {
  getProductOptionValueByProductId,
  getProductOptionValues,
  getProductSlug,
  getProductsByIds,
} from '../ProductService';

jest.mock('@/common/services/ApiClientService', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
  },
}));

describe('ProductService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('getProductOptionValues returns json on success and throws on error', async () => {
    const json = jest.fn().mockResolvedValue(['value1']);
    (apiClientService.get as jest.Mock)
      .mockResolvedValueOnce({ status: 200, json })
      .mockResolvedValueOnce({ status: 400, json: jest.fn().mockResolvedValue('bad') });

    await expect(getProductOptionValues(10)).resolves.toEqual(['value1']);
    await expect(getProductOptionValues(10)).rejects.toThrow('bad');
  });

  it('getProductSlug uses expected endpoint and handles non-2xx response', async () => {
    const json = jest.fn().mockResolvedValue({ slug: 'abc' });
    (apiClientService.get as jest.Mock)
      .mockResolvedValueOnce({ status: 200, json })
      .mockResolvedValueOnce({ status: 500, json: jest.fn().mockResolvedValue('error') });

    await expect(getProductSlug(99)).resolves.toEqual({ slug: 'abc' });
    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/product/storefront/productions/99/slug'
    );
    await expect(getProductSlug(99)).rejects.toThrow('error');
  });

  it('getProductsByIds throws YasError when response is not ok', async () => {
    const payload = { status: 422, title: 'Validation', detail: 'invalid' };
    (apiClientService.get as jest.Mock)
      .mockResolvedValueOnce({ ok: true, json: jest.fn().mockResolvedValue([{ id: 1 }]) })
      .mockResolvedValueOnce({ ok: false, json: jest.fn().mockResolvedValue(payload) });

    await expect(getProductsByIds([1])).resolves.toEqual([{ id: 1 }]);
    await expect(getProductsByIds([2])).rejects.toBeInstanceOf(YasError);
  });

  it('getProductOptionValueByProductId rejects with status text on failure', async () => {
    (apiClientService.get as jest.Mock)
      .mockResolvedValueOnce({ status: 200, json: jest.fn().mockResolvedValue([{ id: 1 }]) })
      .mockResolvedValueOnce({ status: 404, statusText: 'Not Found' });

    await expect(getProductOptionValueByProductId(1)).resolves.toEqual([{ id: 1 }]);
    await expect(getProductOptionValueByProductId(2)).rejects.toThrow('Not Found');
  });
});
