import { StateOrProvince } from '../models/StateOrProvince';
import apiClientService from '@commonServices/ApiClientService';
import { LOCATION_BACKOFFICE_STATE_OR_PROVINCES_ENDPOINT } from '@constants/WhitelistedEndpoints';

const baseUrl = LOCATION_BACKOFFICE_STATE_OR_PROVINCES_ENDPOINT;

export async function getStateOrProvincesByCountry(countryId: number): Promise<StateOrProvince[]> {
  const url = `${baseUrl}?countryId=${countryId}`;
  return (await apiClientService.get(url)).json();
}

export async function getPageableStateOrProvinces(
  pageNo: number,
  pageSize: number,
  countryId: number
) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}&countryId=${countryId}`;
  return (await apiClientService.get(url)).json();
}

export async function createStateOrProvince(stateOrProvince: StateOrProvince) {
  return apiClientService.post(baseUrl, JSON.stringify(stateOrProvince));
}
export async function getStateOrProvince(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteStateOrProvince(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editStateOrProvince(id: number, stateOrProvince: StateOrProvince) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(stateOrProvince));
  if (response.status === 204) return response;
  else return await response.json();
}

export async function getStatesOrProvinces(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}
