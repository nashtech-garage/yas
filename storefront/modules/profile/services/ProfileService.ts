import { ProfileRequest } from '../models/ProfileRequest';

export async function getMyProfile() {
  const response = await fetch(`/api/customer/storefront/customer/profile`);
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
