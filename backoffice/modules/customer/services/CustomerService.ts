import { ProfileRequest } from '../../profile/models/ProfileRequest';
import { Customer } from '../models/Customer';
import { Customers } from '../models/Customers';
import apiClientService from '@commonServices/ApiClientService';
import { CUSTOMER_ENDPOINT } from '@constants/Endpoints';

const baseUrl = CUSTOMER_ENDPOINT;

export async function getCustomers(pageNo: number): Promise<Customers> {
  const url = `${baseUrl}/backoffice/customers?pageNo=${pageNo}`;
  return (await apiClientService.get(url)).json();
}

export async function updateCustomer(profile: ProfileRequest) {
  const url = `${baseUrl}/`;
  return await apiClientService.put(url, JSON.stringify(profile));
}

export async function getMyProfile() {
  const url = `${baseUrl}/storefront/customer/profile`;
  return (await apiClientService.get(url)).json();
}
