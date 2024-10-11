import { Order } from '../models/Order';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/order/backoffice/orders';

export async function getOrders(
  params: string
): Promise<{ orderList: Order[]; totalPages: number; totalElements: number }> {
  const url = `${baseUrl}?${params}`;
  return (await apiClientService.get(url)).json();
}

export async function getLatestOrders(count: number): Promise<Order[]> {
  const url = `${baseUrl}/latest/${count}`;
  const response = await apiClientService.get(url);
  if (response.status >= 200 && response.status < 300) return await response.json();
  return Promise.reject(new Error(response.statusText));
}

export async function getOrderById(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}
