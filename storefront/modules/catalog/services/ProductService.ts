import { Product } from '../models/Product';
import { ProductDetail } from '../models/ProductDetail';
import { ProductThumbnail } from '../models/ProductThumbnail';

export async function getFeaturedProducts(): Promise<ProductThumbnail[]> {
  const response = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + '/product/storefront/products/featured'
  );
  return await response.json();
}

export async function getProduct(slug: string): Promise<Product> {
  const response = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + '/product/storefront/products/' + slug
  );
  return response.json();
}

export function formatPrice(price: number): any {
  var formatter = new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  });

  return formatter.format(price);
}

export async function getProductDetail(slug: string): Promise<ProductDetail> {
  const response = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + '/product/storefront/product/' + slug
  );
  return response.json();
}