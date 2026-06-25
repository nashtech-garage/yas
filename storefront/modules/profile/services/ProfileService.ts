import { ProfileRequest } from '../models/ProfileRequest';
import apiClientService from '@/common/services/ApiClientService';

export async function getMyProfile() {
  const url = '/api/customer/storefront/customer/profile';
  const response = await apiClientService.get(url);
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }
  return Promise.reject(new Error(response.statusText));
}

export async function updateCustomer(profile: ProfileRequest) {
  const url = '/api/customer/';
  const response = await apiClientService.put(url, JSON.stringify(profile));
  if (response.status === 204) return response;
  else return await response.json();
}
