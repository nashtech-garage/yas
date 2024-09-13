import { Address } from '@/modules/address/models/AddressModel';
import apiClientService from '@/common/services/ApiClientService';

const userAddressUrl = '/api/customer/storefront/user-address';

export async function createUserAddress(address: Address) {
  const response = await apiClientService.post(userAddressUrl, JSON.stringify(address));
  return response.json();
}

export async function getUserAddress() {
  const response = await apiClientService.get(userAddressUrl);
  return response.json();
}

export async function getUserAddressDefault(): Promise<Address> {
  const response = await apiClientService.get(`${userAddressUrl}/default-address`);
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }
  throw new Error(response.statusText);
}

export async function deleteUserAddress(id: number) {
  const response = await apiClientService.delete(`${userAddressUrl}/${id}`);
  return await response;
}

export async function chooseDefaultAddress(id: number) {
  const response = await apiClientService.put(`${userAddressUrl}/${id}`, null);
  return await response;
}
