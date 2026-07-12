import { beforeEach, describe, expect, test, vi } from 'vitest';
import { YasError } from '@/common/services/errors/YasError';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import {
  getFeaturedProducts,
  getProductByMultiParams,
  getProductDetail,
  getProductOptionValueByProductId,
  getProductOptionValues,
  getProductSlug,
  getProductVariationsByParentId,
  getProductsByIds,
  getRelatedProductsByProductId,
  getSimilarProductsByProductId,
} from './ProductService';

describe('ProductService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getFeaturedProducts should build URL with pageNo', async () => {
    const payload = { data: [] };
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue(payload),
    } as unknown as Response);

    const result = await getFeaturedProducts(2);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/product/storefront/products/featured?pageNo=2'
    );
    expect(result).toEqual(payload);
  });

  test('getProductDetail should call serverSideRenderUrl endpoint', async () => {
    const payload = { id: 1, name: 'p1' };
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue(payload),
    } as unknown as Response);

    const result = await getProductDetail('slug-1');

    expect(apiClientService.get).toHaveBeenCalledWith(
      `${process.env.API_BASE_PATH}/product/storefront/product/slug-1`
    );
    expect(result).toEqual(payload);
  });

  test('getProductOptionValues should return json on 2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 200,
      json: vi.fn().mockResolvedValue([{ id: 1 }]),
    } as unknown as Response);

    await expect(getProductOptionValues(5)).resolves.toEqual([{ id: 1 }]);
    expect(apiClientService.get).toHaveBeenCalledWith(
      `${process.env.API_BASE_PATH}/product/storefront/product-option-combinations/5/values`
    );
  });

  test('getProductOptionValues should throw error on non-2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 400,
      json: vi.fn().mockResolvedValue('bad request'),
    } as unknown as Response);

    await expect(getProductOptionValues(5)).rejects.toThrow('bad request');
  });

  test('getProductByMultiParams should pass raw query string', async () => {
    const payload = { content: [] };
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue(payload),
    } as unknown as Response);

    const result = await getProductByMultiParams('keyword=abc&page=1');

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/product/storefront/products?keyword=abc&page=1'
    );
    expect(result).toEqual(payload);
  });

  test('getProductVariationsByParentId should throw on non-2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 500,
      json: vi.fn().mockResolvedValue('server fail'),
    } as unknown as Response);

    await expect(getProductVariationsByParentId(1)).rejects.toThrow('server fail');
  });

  test('getProductSlug should throw on non-2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 404,
      json: vi.fn().mockResolvedValue('not found'),
    } as unknown as Response);

    await expect(getProductSlug(10)).rejects.toThrow('not found');
  });

  test('getRelatedProductsByProductId should return json on 2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 201,
      json: vi.fn().mockResolvedValue({ products: [] }),
    } as unknown as Response);

    const result = await getRelatedProductsByProductId(3);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/product/storefront/products/related-products/3'
    );
    expect(result).toEqual({ products: [] });
  });

  test('getSimilarProductsByProductId should throw on non-2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 403,
      json: vi.fn().mockResolvedValue('forbidden'),
    } as unknown as Response);

    await expect(getSimilarProductsByProductId(9)).rejects.toThrow('forbidden');
  });

  test('getProductsByIds should throw YasError when response is not ok', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      ok: false,
      json: vi.fn().mockResolvedValue({ detail: 'invalid ids' }),
    } as unknown as Response);

    await expect(getProductsByIds([1, 2])).rejects.toBeInstanceOf(YasError);
  });

  test('getProductsByIds should return json when response is ok', async () => {
    const payload = [{ id: 1, name: 'p1' }];
    vi.mocked(apiClientService.get).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue(payload),
    } as unknown as Response);

    const result = await getProductsByIds([1]);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/product/storefront/products/list-featured?productId=1'
    );
    expect(result).toEqual(payload);
  });

  test('getProductOptionValueByProductId should reject when status is not 2xx', async () => {
    const logSpy = vi.spyOn(console, 'log').mockImplementation(() => undefined);
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 500,
      statusText: 'Server Error',
    } as unknown as Response);

    await expect(getProductOptionValueByProductId(7)).rejects.toThrow('Server Error');
    expect(logSpy).toHaveBeenCalledWith('/api/product/storefront/product-option-values/7');
  });

  test('getProductOptionValueByProductId should return json when status is 2xx', async () => {
    const logSpy = vi.spyOn(console, 'log').mockImplementation(() => undefined);
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 200,
      json: vi.fn().mockResolvedValue([{ id: 2 }]),
    } as unknown as Response);

    const result = await getProductOptionValueByProductId(7);

    expect(result).toEqual([{ id: 2 }]);
    expect(logSpy).toHaveBeenCalledWith('/api/product/storefront/product-option-values/7');
  });
});
