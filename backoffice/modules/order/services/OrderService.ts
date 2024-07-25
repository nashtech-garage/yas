import { Order } from '../models/Order';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/order/backoffice/orders';

export async function getOrders(
  params: string
): Promise<{ orderList: Order[]; totalPages: number; totalElements: number }> {
  const url = `${baseUrl}?${params}`;
  const response = await apiClientService.get(url);
  if (response.status >= 200 && response.status < 300) return response.json();
  return Promise.reject(response);
}

export async function getOrderById(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.get(url);
  if (response.status >= 200 && response.status < 300) return response.json();
  return Promise.reject(response);
}
