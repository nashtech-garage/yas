import { TaxClass } from '../models/TaxClass';
import apiClientService from '@commonServices/ApiClientService';
import { TAX_BACKOFFICE_TAX_CLASSES_ENDPOINT } from '@constants/Endpoints';

const baseUrl = TAX_BACKOFFICE_TAX_CLASSES_ENDPOINT;

export async function getTaxClasses(): Promise<TaxClass[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableTaxClasses(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function createTaxClass(taxClass: TaxClass) {
  return apiClientService.post(baseUrl, JSON.stringify(taxClass));
}
export async function getTaxClass(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteTaxClass(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editTaxClass(id: number, taxClass: TaxClass) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(taxClass));
  if (response.status === 204) return response;
  else return await response.json();
}
