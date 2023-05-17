import { Address } from '@/modules/address/models/AddressModel';

export async function createUserAddress(address: Address) {
  const response = await fetch(`/api/customer/storefront/user-address`, {
    method: 'POST',
    headers: { 'Content-type': 'application/json' },
    body: JSON.stringify(address),
  });
  return response.json();
}

export async function getUserAddress() {
  const response = await fetch(`/api/customer/storefront/user-address`);
  return response.json();
}

export async function getUserAddressDefault(): Promise<Address> {
  const response = await fetch(`/api/customer/storefront/user-address/default-address`);
  if (response.status >= 200 && response.status < 300) return response.json();
  return Promise.reject(response);
}

export async function deleteUserAddress(id: number) {
  const response = await fetch(`/api/customer/storefront/user-address/${id}`, {
    method: 'DELETE',
  });
  return await response;
}

export async function chooseDefaultAddress(id: number) {
  const response = await fetch(`/api/customer/storefront/user-address/${id}`, {
    method: 'PUT',
  });
  return await response;
}
