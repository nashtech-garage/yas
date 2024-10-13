import { Rating } from '../models/Rating';
import { RatingPost } from '../models/RatingPost';
import { concatQueryString } from '../../../utils/concatQueryString';
import apiClientService from '@/common/services/ApiClientService';

const baseUrl = '/api/rating/storefront';

export async function getRatingsByProductId(
  productId: number,
  pageNo?: number,
  pageSize?: number
): Promise<{ ratingList: Rating[]; totalPages: number; totalElements: number }> {
  const url = `${baseUrl}/ratings/products/${productId}`;
  const queryString = [];
  if (pageNo) {
    queryString.push(`pageNo=${pageNo}`);
  }
  if (pageSize) {
    queryString.push(`pageSize=${pageSize}`);
  }
  const final_url = concatQueryString(queryString, url);

  const response = await apiClientService.get(final_url);
  return await response.json();
}

export async function createRating(rating: RatingPost): Promise<Rating | null> {
  const response = await apiClientService.post('${baseUrl}/ratings', JSON.stringify(rating));
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }
  throw new Error(response.statusText);
}

export async function getAverageStarByProductId(productId: number): Promise<number> {
  const url = `${baseUrl}/ratings/product/${productId}/average-star`;

  const response = await apiClientService.get(url);

  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }

  throw new Error(response.statusText);
}
