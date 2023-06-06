import { Order } from '../models/Order';

export async function getOrders(
  params: string
): Promise<{ orderList: Order[]; totalPages: number; totalElements: number }> {
  const url = `/api/order/backoffice/orders`;

  const response = await fetch(url + `?${params}`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });
  if (response.status >= 200 && response.status < 300) return response.json();
  return Promise.reject(response);
}

export async function getOrderById(id: number) {
  const response = await fetch('/api/order/backoffice/orders/' + id);
  if (response.status >= 200 && response.status < 300) return response.json();
  return Promise.reject(response);
}

export async function exportCsvFile(params: string) {
  const response = await fetch('/api/order/backoffice/orders/csv-export?' + params);
  if (response.status >= 200 && response.status < 300) return response.json();
  return Promise.reject(response);
}
