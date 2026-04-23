import { YasError } from '@/common/services/errors/YasError';
import { Address } from '../models/AddressModel';
import apiClientService from '@/common/services/ApiClientService';

const baseUrl = '/api/location/storefront/addresses';

export async function createAddress(address: Address) {
  const response = await apiClientService.post(baseUrl, JSON.stringify(address));
  return response.json();
}

export async function updateAddress(id: string, address: Address) {
  const response = await apiClientService.put(`${baseUrl}/${id}`, JSON.stringify(address));
  return response;
}

export async function getAddress(id: string) {
  const response = await apiClientService.get(`${baseUrl}/${id}`);
  const jsonResult = await response.json();
  if (!response.ok) {
    throw new YasError(jsonResult);
  }
  return jsonResult;
}

export async function deleteAddress(id: number) {
  const response = await apiClientService.delete(`${baseUrl}/${id}`);
  return response;
}
