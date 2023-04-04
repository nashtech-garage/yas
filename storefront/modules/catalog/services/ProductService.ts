import { ProductSlug } from '../../cart/models/ProductSlug';
import { ProductDetail } from '../models/ProductDetail';
import { ProductAll, ProductFeature } from '../models/ProductFeature';
import { ProductOptionValueGet } from '../models/ProductOptionValueGet';
import { ProductVariation } from '../models/ProductVariation';

export async function getFeaturedProducts(pageNo: number): Promise<ProductFeature> {
  const response = await fetch(`api/product/storefront/products/featured?pageNo=${pageNo}`);
  return response.json();
}

export async function getProductDetail(slug: string): Promise<ProductDetail> {
  const response = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + '/product/storefront/product/' + slug
  );
  return response.json();
}

export async function getProductOptionValues(productId: number): Promise<ProductOptionValueGet[]> {
  const res = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + `/product/storefront/product-option-values/${productId}`
  );
  if (res.status >= 200 && res.status < 300) return res.json();
  return Promise.reject(res);
}

export async function getProductByMultiParams(queryString: string): Promise<ProductAll> {
  const res = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + `/product/storefront/products?${queryString}`
  );
  return res.json();
}

export async function getProductVariationsByParentId(
  parentId: number
): Promise<ProductVariation[]> {
  const res = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + `/product/product-variations/${parentId}`
  );
  if (res.status >= 200 && res.status < 300) return res.json();
  return Promise.reject(res);
}

export async function getProductSlug(productId: number): Promise<ProductSlug> {
  const res = await fetch(
    process.env.NEXT_PUBLIC_API_BASE_PATH + `/product/storefront/productions/${productId}/slug`
  );
  if (res.status >= 200 && res.status < 300) return res.json();
  return Promise.reject(res);
}
