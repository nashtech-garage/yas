import { Rating } from '../models/Rating';
import { concatQueryString } from '../../../utils/concatQueryString';

export async function getRatingsByProductId(
  productId: number,
  pageNo?: number,
  pageSize?: number
): Promise<{ ratingList: Rating[]; totalPages: number; totalElements: number }> {
  const url = `/api/rating/backoffice/ratings/products/${productId}`;
  const queryString = [];
  if (pageNo) {
    queryString.push(`pageNo=${pageNo}`);
  }
  if (pageSize) {
    queryString.push(`pageSize=${pageSize}`);
  }
  const final_url = concatQueryString(queryString, url);

  const response = await fetch(final_url);
  return await response.json();
}
