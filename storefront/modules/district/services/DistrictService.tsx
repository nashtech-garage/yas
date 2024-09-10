import apiClientService from '@/common/services/ApiClientService';

export async function getDistricts(id: number) {
  const response = await apiClientService.get(`/api/location/storefront/district/${id}`);
  return response.json();
}
