import { Rating } from '../models/Rating';
import { concatQueryString } from '../../../utils/concatQueryString';

export async function getRatings(
  productName?: string,
  customerName?: string,
  pageNo?: number,
  pageSize?: number
): Promise<{ ratingList: Rating[]; totalPages: number; totalElements: number }> {
  const url = `/api/rating/backoffice/ratings`;
  const queryString = [];
  if (pageNo) {
    queryString.push(`pageNo=${pageNo}`);
  }
  if (pageSize) {
    queryString.push(`pageSize=${pageSize}`);
  }
  if (productName) {
    queryString.push(`productName=${productName}`);
  }
  const final_url = concatQueryString(queryString, url);

  const response = await fetch(
    final_url + `&createdFrom=1970-01-01T00:00:01.968Z&createdTo=2023-03-28T05:17:18.438Z`
  );
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
