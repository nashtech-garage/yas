import { Country } from '../models/Country';
import apiClientService from '@commonServices/ApiClientService';
import { LOCATION_BACKOFFICE_COUNTRIES_ENDPOINT } from '@constants/WhitelistedEndpoints';

const baseUrl = LOCATION_BACKOFFICE_COUNTRIES_ENDPOINT;

export async function getCountries(): Promise<Country[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableCountries(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function createCountry(country: Country) {
  return apiClientService.post(baseUrl, JSON.stringify(country));
}
export async function getCountry(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteCountry(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editCountry(id: number, country: Country) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(country));
  if (response.status === 204) return response;
  else return await response.json();
}
