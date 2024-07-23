import { TaxRate } from '../models/TaxRate';
import apiClientService from '@commonServices/ApiClientService';
import { TAX_BACKOFFICE_TAX_RATES_ENDPOINT } from '@constants/Endpoints';

const baseUrl = TAX_BACKOFFICE_TAX_RATES_ENDPOINT;

export async function getTaxRates(): Promise<TaxRate[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableTaxRates(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function createTaxRate(taxRate: TaxRate) {
  return apiClientService.post(baseUrl, JSON.stringify(taxRate));
}
export async function getTaxRate(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteTaxRate(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editTaxRate(id: number, taxRate: TaxRate) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(taxRate));
  if (response.status === 204) return response;
  else return await response.json();
}
