import apiClientService from '@/common/services/ApiClientService';

const baseUrl = '/api/payment';

export async function getPaymentById(id: string) {
  const response = await apiClientService.get(`${baseUrl}/storefront/payments/${id}`);
  if (response.status >= 200 && response.status < 300) return response.json();
  throw new Error(response.statusText);
}
