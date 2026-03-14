import apiClientService from '@/common/services/ApiClientService';
import { EOrderStatus } from '@/modules/order/models/EOrderStatus';

import { createCheckout, createOrder, getCheckoutById, getMyOrders } from '../OrderService';

jest.mock('@/common/services/ApiClientService', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
  },
}));

describe('OrderService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('createOrder posts serialized order and returns json on success', async () => {
    const order = { id: 1, note: 'test' } as any;
    const json = jest.fn().mockResolvedValue({ id: 1 });
    (apiClientService.post as jest.Mock).mockResolvedValue({ status: 201, json });

    const result = await createOrder(order);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/order/storefront/orders',
      JSON.stringify(order)
    );
    expect(result).toEqual({ id: 1 });
  });

  it('createOrder throws when response is not successful', async () => {
    const order = { id: 1 } as any;
    (apiClientService.post as jest.Mock).mockResolvedValue({
      status: 400,
      statusText: 'Bad Request',
    });

    await expect(createOrder(order)).rejects.toThrow('Bad Request');
  });

  it('getMyOrders uses correct query and returns data on success', async () => {
    const json = jest.fn().mockResolvedValue([{ id: 10 }]);
    (apiClientService.get as jest.Mock).mockResolvedValue({ status: 200, json });

    const result = await getMyOrders('iphone', EOrderStatus.PENDING);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/order/storefront/orders/my-orders?productName=iphone&orderStatus=PENDING'
    );
    expect(result).toEqual([{ id: 10 }]);
  });

  it('getMyOrders throws raw response on failure', async () => {
    const response = { status: 500 };
    (apiClientService.get as jest.Mock).mockResolvedValue(response);

    await expect(getMyOrders('iphone', null)).rejects.toBe(response);
    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/order/storefront/orders/my-orders?productName=iphone&orderStatus='
    );
  });

  it('createCheckout posts payload and returns data on success', async () => {
    const checkout = { orderId: 1 } as any;
    const json = jest.fn().mockResolvedValue({ checkoutId: 'c1' });
    (apiClientService.post as jest.Mock).mockResolvedValue({ status: 200, json });

    const result = await createCheckout(checkout);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/order/storefront/checkouts',
      JSON.stringify(checkout)
    );
    expect(result).toEqual({ checkoutId: 'c1' });
  });

  it('getCheckoutById gets checkout and throws on fail', async () => {
    const json = jest.fn().mockResolvedValue({ id: 'abc' });
    (apiClientService.get as jest.Mock)
      .mockResolvedValueOnce({ status: 200, json })
      .mockResolvedValueOnce({ status: 404, statusText: 'Not Found' });

    await expect(getCheckoutById('abc')).resolves.toEqual({ id: 'abc' });
    await expect(getCheckoutById('missing')).rejects.toThrow('Not Found');
  });
});
