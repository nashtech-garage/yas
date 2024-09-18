import { Checkout } from '../models/Checkout';
import { EOrderStatus } from '../models/EOrderStatus';
import { Order } from '../models/Order';
import { OrderGetVm } from '../models/OrderGetVm';
import apiClientService from '@/common/services/ApiClientService';

const baseUrl = '/api/order/storefront';

export async function createOrder(order: Order): Promise<Order | null> {
  const response = await apiClientService.post(`${baseUrl}/orders`, JSON.stringify(order));
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }
  throw new Error(response.statusText);
}

export async function getMyOrders(
  productName: string,
  orderStatus: EOrderStatus | null
): Promise<OrderGetVm[]> {
  const res = await apiClientService.get(
    `${baseUrl}/orders/my-orders?productName=${productName}&orderStatus=${orderStatus ?? ''}`
  );
  if (res.status >= 200 && res.status < 300) return res.json();
  throw res;
}

export async function createCheckout(checkout: Checkout): Promise<Checkout | null> {
  const response = await apiClientService.post(`${baseUrl}/checkouts`, JSON.stringify(checkout));
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }
  throw new Error(response.statusText);
}

export async function getCheckoutById(id: string) {
  const response = await apiClientService.get(`${baseUrl}/checkouts/${id}`);
  if (response.status >= 200 && response.status < 300) return response.json();
  throw new Error(response.statusText);
}
