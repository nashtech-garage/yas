import { ProductThumbnail } from '../models/ProductThumbnail'

export async function getFeaturedProducts(): Promise<ProductThumbnail[]> {
  const response = await fetch('http://storefront/api/product/storefront/products/featured');
  return await response.json();
}