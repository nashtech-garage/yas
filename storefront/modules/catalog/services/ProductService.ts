import { Product } from '../models/Product';
import { ProductDetail } from '../models/ProductDetail';
import { ProductFeature } from '../models/ProductFeature';
import { ProductOptionValueGet } from '../models/ProductOptionValueGet';

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
  const formatter = new Intl.NumberFormat('vi-VN', {
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

export async function getProductVariations(productId: number): Promise<ProductOptionValueGet[]> {
  const res = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + `/product/storefront/product-option-values/${productId}`
  );
  return res.json();
}
