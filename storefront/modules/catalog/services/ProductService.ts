import { Product } from '../models/Product';
import { ProductFeature } from '../models/ProductFeature';

export async function getFeaturedProducts(pageNo: number): Promise<ProductFeature> {
  const response = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + `/product/storefront/products/featured?pageNo=${pageNo}`
  );
  return response.json();
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
