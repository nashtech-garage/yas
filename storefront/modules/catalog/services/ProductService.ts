import axiosClient from '../../../utils/axiosClient';
import { concatQueryString } from '../../../utils/concatQueryString';
import { Product } from '../models/Product';
import { ProductDetail } from '../models/ProductDetail';
import { ProductFeature, ProductAll } from '../models/ProductFeature';
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

export async function getProductByMultiParams(queryString: string): Promise<any> {
  const response = await axiosClient.get(`/product/storefront/products?${queryString}`);
  return response;
}
