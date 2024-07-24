import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/location/backoffice/district';

export async function getDistricts(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}
