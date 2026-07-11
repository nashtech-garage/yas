import {
  addCartItem,
  bulkDeleteCartItems,
  deleteCartItem,
  getDetailedCartItems,
  getNumberCartItems,
  updateCartItem,
} from './CartService';
import apiClientService from '@/common/services/ApiClientService';
import { getProductsByIds } from '@/modules/catalog/services/ProductService';
import { YasError } from '@/common/services/errors/YasError';

jest.mock('@/common/services/ApiClientService', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

jest.mock('@/modules/catalog/services/ProductService', () => ({
  getProductsByIds: jest.fn(),
}));

const mockedApiClient = apiClientService as jest.Mocked<typeof apiClientService>;
const mockedGetProductsByIds = getProductsByIds as jest.MockedFunction<typeof getProductsByIds>;

describe('CartService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('adds a cart item successfully', async () => {
    const payload = { productId: 1, quantity: 2 };
    const responseJson = { customerId: 'customer-1', productId: 1, quantity: 2 };
    mockedApiClient.post.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await addCartItem(payload);

    expect(mockedApiClient.post).toHaveBeenCalledWith(
      '/api/cart/storefront/cart/items',
      JSON.stringify(payload)
    );
    expect(result).toEqual(responseJson);
  });

  it('throws YasError when addCartItem fails', async () => {
    mockedApiClient.post.mockResolvedValue({
      ok: false,
      json: jest.fn().mockResolvedValue({
        status: 400,
        title: 'Bad Request',
        detail: 'Invalid cart item',
        fieldErrors: ['Invalid cart item'],
      }),
    } as unknown as Response);

    await expect(addCartItem({ productId: 1, quantity: 0 })).rejects.toBeInstanceOf(YasError);
  });

  it('maps cart items with matching products only', async () => {
    mockedApiClient.get.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue([
        { customerId: 'customer-1', productId: 1, quantity: 2 },
        { customerId: 'customer-1', productId: 99, quantity: 1 },
      ]),
    } as unknown as Response);
    mockedGetProductsByIds.mockResolvedValue([
      { id: 1, name: 'Phone', slug: 'phone', thumbnailUrl: '/phone.png', price: 200 },
    ]);

    const result = await getDetailedCartItems();

    expect(mockedGetProductsByIds).toHaveBeenCalledWith([1, 99]);
    expect(result).toEqual([
      {
        customerId: 'customer-1',
        productId: 1,
        quantity: 2,
        productName: 'Phone',
        slug: 'phone',
        thumbnailUrl: '/phone.png',
        price: 200,
      },
    ]);
  });

  it('calculates the total quantity of cart items', async () => {
    mockedApiClient.get.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue([
        { customerId: 'customer-1', productId: 1, quantity: 2 },
        { customerId: 'customer-1', productId: 2, quantity: 3 },
      ]),
    } as unknown as Response);

    const result = await getNumberCartItems();

    expect(result).toBe(5);
  });

  it('deletes a cart item successfully', async () => {
    mockedApiClient.delete.mockResolvedValue({
      ok: true,
    } as Response);

    await expect(deleteCartItem(5)).resolves.toBeUndefined();
    expect(mockedApiClient.delete).toHaveBeenCalledWith('/api/cart/storefront/cart/items/5');
  });

  it('bulk deletes cart items successfully', async () => {
    const payload = [{ productId: 1, quantity: 2 }];
    const responseJson = [{ customerId: 'customer-1', productId: 1, quantity: 2 }];
    mockedApiClient.post.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await bulkDeleteCartItems(payload);

    expect(mockedApiClient.post).toHaveBeenCalledWith(
      '/api/cart/storefront/cart/items/remove',
      JSON.stringify(payload)
    );
    expect(result).toEqual(responseJson);
  });

  it('updates a cart item successfully', async () => {
    const payload = { quantity: 10 };
    const responseJson = { customerId: 'customer-1', productId: 1, quantity: 10 };
    mockedApiClient.put.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await updateCartItem(1, payload);

    expect(mockedApiClient.put).toHaveBeenCalledWith(
      '/api/cart/storefront/cart/items/1',
      JSON.stringify(payload)
    );
    expect(result).toEqual(responseJson);
  });
});
