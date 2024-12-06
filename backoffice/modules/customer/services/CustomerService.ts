import { Customer, CustomerCreateVM, CustomerUpdateVM } from '../models/Customer';
import { Customers } from '../models/Customers';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/customer';

export async function getCustomers(pageNo: number, pageSize: number): Promise<Customers> {
  const url = `${baseUrl}/backoffice/customers?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function getCustomer(userId: string): Promise<Customer> {
  const url = `${baseUrl}/backoffice/customers/profile/${userId}`;
  return (await apiClientService.get(url)).json();
}

export async function createCustomer(customer: CustomerCreateVM) {
  const url = `${baseUrl}/backoffice/customers`;
  return await apiClientService.post(url, JSON.stringify(customer));
}

export async function updateCustomer(id: string, customer: CustomerUpdateVM) {
  const url = `${baseUrl}/backoffice/customers/profile/${id}`;
  return await apiClientService.put(url, JSON.stringify(customer));
}

export async function deleteCustomer(id: string) {
  const url = `${baseUrl}/backoffice/customers/profile/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function getMyProfile(): Promise<Customer> {
  const url = `${baseUrl}/storefront/customer/profile`;
  return (await apiClientService.get(url)).json();
}
