import { ProductOption } from '../models/ProductOption';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/product/backoffice/product-options';

export async function getProductOptions(): Promise<ProductOption[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableProductOptions(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function getProductOption(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function createProductOption(productOption: ProductOption) {
  return await apiClientService.post(baseUrl, JSON.stringify(productOption));
}
export async function updateProductOption(id: number, productOption: ProductOption) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(productOption));
  if (response.status === 204) return response;
  else return await response.json();
}

export async function deleteProductOption(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}
