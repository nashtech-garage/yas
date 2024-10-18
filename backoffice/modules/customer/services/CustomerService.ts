import { ProfileRequest } from '../../profile/models/ProfileRequest';
import { Customer, CustomerCreateVM } from '../models/Customer';
import { Customers } from '../models/Customers';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/customer';

export async function getCustomers(pageNo: number): Promise<Customers> {
  const url = `${baseUrl}/backoffice/customers?pageNo=${pageNo}`;
  return (await apiClientService.get(url)).json();
}

export async function createCustomer(customer: CustomerCreateVM) {
  const url = `${baseUrl}/backoffice/customers`;
  return await apiClientService.post(url, JSON.stringify(customer));
}

export async function updateCustomer(profile: ProfileRequest) {
  const url = `${baseUrl}/`;
  return await apiClientService.put(url, JSON.stringify(profile));
}

export async function getMyProfile() {
  const url = `${baseUrl}/storefront/customer/profile`;
  return (await apiClientService.get(url)).json();
}
