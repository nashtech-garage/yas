import { Rating } from '../models/Rating';

export async function getRatingsByProductId(productId: number): Promise<Rating[]> {
  const response = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + '/rating/storefront/ratings/products/' + productId
  );
  return await response.json();
}
