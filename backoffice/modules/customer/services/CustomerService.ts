import { ProfileRequest } from '../../profile/models/ProfileRequest';
import { Customer } from '../models/Customer';
import { Customers } from '../models/Customers';

export async function getCustomers(pageNo: number): Promise<Customers> {
  const response = await fetch(`/api/customer/backoffice/customers?pageNo=${pageNo}`);
  return response.json();
}

export async function updateCustomer(profile: ProfileRequest) {
  const response = await fetch(`/api/customer/`, {
    method: 'PUT',
    body: JSON.stringify(profile),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return response;
}

export async function getMyProfile() {
  const response = await fetch(`/api/customer/storefront/customer/profile`);
  return response.json();
}
