import { Rating } from '../models/Rating';
import { RatingPost } from '../models/RatingPost';
import { concatQueryString } from '../../../utils/concatQueryString';

export async function getRatingsByProductId(
  productId: number,
  pageNo?: number,
  pageSize?: number
): Promise<{ ratingList: Rating[]; totalPages: number; totalElements: number }> {
  const url = `${process.env.NEXT_PUBLIC_API_BASE_PATH}/rating/storefront/ratings/products/${productId}`;
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

export async function createRating(rating: RatingPost): Promise<Rating> {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_BASE_PATH}/rating/storefront/ratings`,
    {
      method: 'POST',
      body: JSON.stringify(rating),
      headers: { 'Content-type': 'application/json; charset=UTF-8' },
    }
  );
  return await response.json();
}
