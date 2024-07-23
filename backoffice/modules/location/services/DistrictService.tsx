import apiClientService from '@commonServices/ApiClientService';
import { LOCATION_BACKOFFICE_DISTRICT_ENDPOINT } from '@constants/Endpoints';

const baseUrl = LOCATION_BACKOFFICE_DISTRICT_ENDPOINT;

export async function getDistricts(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}
