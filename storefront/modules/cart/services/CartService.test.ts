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

vi.mock('@/modules/catalog/services/ProductService', () => ({
  getProductsByIds: vi.fn(),
}));

import apiClientService from '@/common/services/ApiClientService';
import { getProductsByIds } from '@/modules/catalog/services/ProductService';
import {
  addCartItem,
  bulkDeleteCartItems,
  deleteCartItem,
  getDetailedCartItems,
  getNumberCartItems,
  updateCartItem,
} from './CartService';

describe('CartService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('addCartItem should return json when response ok', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue({ productId: 1, quantity: 2 }),
    } as unknown as Response);

    const result = await addCartItem({ productId: 1, quantity: 2 } as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/cart/storefront/cart/items',
      JSON.stringify({ productId: 1, quantity: 2 })
    );
    expect(result).toEqual({ productId: 1, quantity: 2 });
  });

  test('addCartItem should throw YasError when response is not ok', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      ok: false,
      json: vi.fn().mockResolvedValue({ detail: 'invalid cart item' }),
    } as unknown as Response);

    await expect(addCartItem({} as never)).rejects.toBeInstanceOf(YasError);
  });

  test('getDetailedCartItems should map product info and skip missing products', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue([
        { productId: 1, quantity: 2 },
        { productId: 2, quantity: 1 },
      ]),
    } as unknown as Response);

    vi.mocked(getProductsByIds).mockResolvedValue([
      {
        id: 1,
        name: 'Phone A',
        slug: 'phone-a',
        thumbnailUrl: '/a.png',
        price: 100,
      },
    ] as never);

    const result = await getDetailedCartItems();

    expect(getProductsByIds).toHaveBeenCalledWith([1, 2]);
    expect(result).toEqual([
      {
        productId: 1,
        quantity: 2,
        productName: 'Phone A',
        slug: 'phone-a',
        thumbnailUrl: '/a.png',
        price: 100,
      },
    ]);
  });

  test('getDetailedCartItems should throw YasError when get cart items fails', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      ok: false,
      json: vi.fn().mockResolvedValue({ detail: 'cannot get cart' }),
    } as unknown as Response);

    await expect(getDetailedCartItems()).rejects.toBeInstanceOf(YasError);
  });

  test('getNumberCartItems should sum quantities', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue([
        { productId: 1, quantity: 2 },
        { productId: 2, quantity: 5 },
      ]),
    } as unknown as Response);

    const result = await getNumberCartItems();

    expect(result).toBe(7);
  });

  test('deleteCartItem should call delete endpoint', async () => {
    vi.mocked(apiClientService.delete).mockResolvedValue({ ok: true } as Response);

    await deleteCartItem(5);

    expect(apiClientService.delete).toHaveBeenCalledWith('/api/cart/storefront/cart/items/5');
  });

  test('deleteCartItem should throw YasError when delete fails', async () => {
    vi.mocked(apiClientService.delete).mockResolvedValue({
      ok: false,
      json: vi.fn().mockResolvedValue({ detail: 'cannot delete' }),
    } as unknown as Response);

    await expect(deleteCartItem(5)).rejects.toBeInstanceOf(YasError);
  });

  test('bulkDeleteCartItems should return json when ok', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue([{ productId: 1, quantity: 0 }]),
    } as unknown as Response);

    const payload = [{ productId: 1 }];
    const result = await bulkDeleteCartItems(payload as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/cart/storefront/cart/items/remove',
      JSON.stringify(payload)
    );
    expect(result).toEqual([{ productId: 1, quantity: 0 }]);
  });

  test('updateCartItem should call put and return json', async () => {
    vi.mocked(apiClientService.put).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue({ productId: 2, quantity: 4 }),
    } as unknown as Response);

    const payload = { quantity: 4 };
    const result = await updateCartItem(2, payload as never);

    expect(apiClientService.put).toHaveBeenCalledWith(
      '/api/cart/storefront/cart/items/2',
      JSON.stringify(payload)
    );
    expect(result).toEqual({ productId: 2, quantity: 4 });
  });

  test('updateCartItem should throw YasError when put fails', async () => {
    vi.mocked(apiClientService.put).mockResolvedValue({
      ok: false,
      json: vi.fn().mockResolvedValue({ detail: 'cannot update' }),
    } as unknown as Response);

    await expect(updateCartItem(2, {} as never)).rejects.toBeInstanceOf(YasError);
  });
});
