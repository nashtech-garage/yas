import { Product } from '@catalogModels/Product';
import { Variantion } from '@catalogModels/ProductVariation';
import { ProductPayload } from '../models/ProductPayload';
import { Products } from '../models/Products';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/product/backoffice';

export async function getProducts(
  pageNo: number,
  productName: string,
  brandName: string
): Promise<Products> {
  const url = `${baseUrl}/products?pageNo=${pageNo}&product-name=${productName}&brand-name=${brandName}`;
  return (await apiClientService.get(url)).json();
}

export async function exportProducts(productName: string, brandName: string) {
  const url = `${baseUrl}/export/products?product-name=${productName}&brand-name=${brandName}`;
  return (await apiClientService.get(url)).json();
}

export async function getProduct(id: number) {
  const url = `${baseUrl}/products/${id}`;
  const response = await apiClientService.get(url);
  return (await apiClientService.get(url)).json();
}

export async function createProduct(product: ProductPayload) {
  const url = `${baseUrl}/products`;
  return await apiClientService.post(url, JSON.stringify(product));
}

export async function updateProduct(id: number, product: ProductPayload) {
  const url = `${baseUrl}/products/${id}`;
  return await apiClientService.put(url, JSON.stringify(product));
}

export async function deleteProduct(id: number) {
  const url = `${baseUrl}/products/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function getVariationsByProductId(productId: number): Promise<Variantion[]> {
  const url = `${baseUrl}/product-variations/${productId}`;
  const response = await apiClientService.get(url);
  if (response.status >= 200 && response.status < 300) return await response.json();
  return [];
}

export async function getRelatedProductByProductId(productId: number): Promise<Product[]> {
  const url = `${baseUrl}/products/related-products/${productId}`;
  const response = await apiClientService.get(url);
  if (response.status >= 200 && response.status < 300) return await response.json();
  return [];
}
