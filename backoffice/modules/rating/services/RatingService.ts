import { Rating } from '../models/Rating';

export async function getRatings(
  params: string
): Promise<{ ratingList: Rating[]; totalPages: number; totalElements: number }> {
  const url = `/api/rating/backoffice/ratings`;

  const response = await fetch(url + `?${params}`);
  return await response.json();
}

export async function deleteRatingById(id: number) {
  const url = `/api/rating/backoffice/ratings/${id}`;

  const response = await fetch(url, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
  });
  return await response.json();
}
