import { ProfileRequest } from '../models/ProfileRequest';
import apiClientService from '@/common/services/ApiClientService';

export async function getMyProfile() {
  // const response = await fetch(`/api/customer/storefront/customer/profile`);
  // if (response.status >= 200 && response.status < 300) {
  //   return await response.json();
  // }
  // return Promise.reject(response.status);
  const url = '/api/customer/storefront/customer/profile';
  return (await apiClientService.get(url)).json();
}

export async function updateCustomer(profile: ProfileRequest) {
  // const response = await fetch(`/api/customer/`, {
  //   method: 'PUT',
  //   body: JSON.stringify(profile),
  //   headers: { 'Content-type': 'application/json; charset=UTF-8' },
  // });

  // if (response.status >= 200 && response.status < 300) {
  //   const contentType = response.headers.get('content-type');
  //   if (contentType?.includes('application/json')) {
  //     return await response.json();
  //   } else {
  //     return;
  //   }
  // }

  // return Promise.reject(response.status);
  const url = '/api/customer/';
  const response = await apiClientService.put(url, JSON.stringify(profile));
  if (response.status === 204) return response;
  else return await response.json();
}
