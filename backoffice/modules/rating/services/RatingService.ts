import { Rating } from '../models/Rating';
import apiClientService from '@commonServices/ApiClientService';
import { RATING_BACKOFFICE_ENDPOINT } from '@constants/Endpoints';

const baseUrl = RATING_BACKOFFICE_ENDPOINT;

export async function getRatings(
  params: string
): Promise<{ ratingList: Rating[]; totalPages: number; totalElements: number }> {
  const url = `${baseUrl}?${params}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteRatingById(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.delete(url)).json();
}
