import { ProductSlug } from '../../cart/models/ProductSlug';
import { ProductDetail } from '../models/ProductDetail';
import { ProductAll, ProductFeature } from '../models/ProductFeature';
import { ProductOptionValueGet } from '../models/ProductOptionValueGet';
import { ProductVariation } from '../models/ProductVariation';
import { ProductsGet } from '../models/ProductsGet';
import apiClientService from '@/common/services/ApiClientService';

const baseUrl = 'api/product/storefront';

export async function getFeaturedProducts(pageNo: number): Promise<ProductFeature> {
  const response = await apiClientService.get(`${baseUrl}/products/featured?pageNo=${pageNo}`);
  return response.json();
}

export async function getProductDetail(slug: string): Promise<ProductDetail> {
  const response = await apiClientService.get(`${baseUrl}/product/${slug}`);
  return response.json();
}

export async function getProductOptionValues(productId: number): Promise<ProductOptionValueGet[]> {
  const res = await apiClientService.get(
    `${baseUrl}/product-option-combinations/${productId}/values`
  );
  if (res.status >= 200 && res.status < 300) return res.json();
  throw new Error(await res.json());
}

export async function getProductByMultiParams(queryString: string): Promise<ProductAll> {
  const res = await apiClientService.get(`${baseUrl}/products?${queryString}`);
  return res.json();
}

export async function getProductVariationsByParentId(
  parentId: number
): Promise<ProductVariation[]> {
  const res = await apiClientService.get(`${baseUrl}/product-variations/${parentId}`);
  if (res.status >= 200 && res.status < 300) return res.json();
  throw res;
}

export async function getProductSlug(productId: number): Promise<ProductSlug> {
  const res = await apiClientService.get(`${baseUrl}/productions/${productId}/slug`);
  if (res.status >= 200 && res.status < 300) return res.json();
  throw new Error(await res.json());
}

export async function getRelatedProductsByProductId(productId: number): Promise<ProductsGet> {
  const res = await apiClientService.get(`${baseUrl}/products/related-products/${productId}`);
  if (res.status >= 200 && res.status < 300) return res.json();
  throw new Error(await res.json());
}
