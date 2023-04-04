import { Address } from '../models/AddressModel';

export async function createAddress(address: Address) {
  const response = await fetch(`/api/location/storefront/address`, {
    method: 'POST',
    headers: { 'Content-type': 'application/json' },
    body: JSON.stringify(address),
  });
  return response.json();
}

export async function updateAddress(id: string, address: Address) {
  const response = await fetch(`/api/location/storefront/address/${id}`, {
    method: 'PUT',
    headers: { 'Content-type': 'application/json' },
    body: JSON.stringify(address),
  });
  return response;
}

export async function getAddress(id: string): Promise<Address> {
  const response = await fetch(`/api/location/storefront/address/${id}`);
  return await response.json();
}

export async function getAddresses(ids: number[]) {
  const data = { ids };
  const response = await fetch(`/api/location/storefront/addresses`, {
    method: 'POST',
    headers: { 'Content-type': 'application/json' },
    body: JSON.stringify(data),
  });
  return await response.json();
}

export async function deleteAddress(id: number) {
  const response = await fetch(`/api/location/storefront/address/${id}`, {
    method: 'DELETE',
  });
  return response;
}
