import { Order } from '../models/Order';

export async function createOrder(order: Order): Promise<Order | null> {
  const response = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_PATH}/order/storefront/orders`, {
    method: 'POST',
    body: JSON.stringify(order),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }
  return Promise.reject(response.status);
}
