import { beforeEach, describe, expect, test, vi } from 'vitest';
import { EOrderStatus } from '../models/EOrderStatus';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { createCheckout, createOrder, getCheckoutById, getMyOrders } from './OrderService';

describe('OrderService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('createOrder should return json on success', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      status: 201,
      json: vi.fn().mockResolvedValue({ id: 1 }),
    } as unknown as Response);

    const result = await createOrder({} as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/order/storefront/orders',
      JSON.stringify({})
    );
    expect(result).toEqual({ id: 1 });
  });

  test('createOrder should throw on non-2xx', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      status: 500,
      statusText: 'Order failed',
    } as unknown as Response);

    await expect(createOrder({} as never)).rejects.toThrow('Order failed');
  });

  test('getMyOrders should include nullable orderStatus in URL', async () => {
    const response = {
      status: 200,
      json: vi.fn().mockResolvedValue([{ id: 9 }]),
    } as unknown as Response;
    vi.mocked(apiClientService.get).mockResolvedValue(response);

    const result = await getMyOrders('product-a', EOrderStatus.PENDING);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/order/storefront/orders/my-orders?productName=product-a&orderStatus=PENDING'
    );
    expect(result).toEqual([{ id: 9 }]);
  });

  test('getMyOrders should throw raw response on failure', async () => {
    const failedResponse = { status: 400 } as Response;
    vi.mocked(apiClientService.get).mockResolvedValue(failedResponse);

    await expect(getMyOrders('product-a', null)).rejects.toBe(failedResponse);
    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/order/storefront/orders/my-orders?productName=product-a&orderStatus='
    );
  });

  test('createCheckout should return json on success', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      status: 200,
      json: vi.fn().mockResolvedValue({ id: 'checkout-1' }),
    } as unknown as Response);

    const result = await createCheckout({} as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/order/storefront/checkouts',
      JSON.stringify({})
    );
    expect(result).toEqual({ id: 'checkout-1' });
  });

  test('getCheckoutById should throw Error when non-2xx', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 404,
      statusText: 'Not Found',
    } as unknown as Response);

    await expect(getCheckoutById('abc')).rejects.toThrow('Not Found');
  });
});
