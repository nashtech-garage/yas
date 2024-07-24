import { Brand } from '../models/Brand';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/product/backoffice/brands';

export async function getBrands(): Promise<Brand[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableBrands(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function createBrand(brand: Brand) {
  return await apiClientService.post(baseUrl, JSON.stringify(brand));
}
export async function getBrand(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteBrand(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editBrand(id: number, brand: Brand) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(brand));
  if (response.status === 204) return response;
  else return await response.json();
}
