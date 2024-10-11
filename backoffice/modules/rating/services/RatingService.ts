import { Rating } from '../models/Rating';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/rating/backoffice/ratings';

export async function getRatings(
  params: string
): Promise<{ ratingList: Rating[]; totalPages: number; totalElements: number }> {
  const url = `${baseUrl}?${params}`;
  return (await apiClientService.get(url)).json();
}

export async function getLatestRatings(count: number): Promise<Rating[]> {
  const url = `${baseUrl}/latest/${count}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteRatingById(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.delete(url)).json();
}
