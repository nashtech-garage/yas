import { Country } from '../models/Country';
import apiClientService from '@/common/services/ApiClientService';

export async function getCountries(): Promise<Country[]> {
  const response = await apiClientService.get(`/api/location/storefront/countries`);
  return await response.json();
}
