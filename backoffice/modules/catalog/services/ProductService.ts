import { Variantion } from '@catalogModels/ProductVariation';
import { ProductPayload } from '../models/ProductPayload';
import { Products } from '../models/Products';

export async function getProducts(
  pageNo: number,
  productName: string,
  brandName: string
): Promise<Products> {
  const url = `/api/product/backoffice/products?pageNo=${pageNo}&product-name=${productName}&brand-name=${brandName}`;
  const response = await fetch(url);
  return await response.json();
}

export async function exportProducts(productName: string, brandName: string) {
  const url = `/api/product/backoffice/export/products?product-name=${productName}&brand-name=${brandName}`;
  const response = await fetch(url);
  return await response.json();
}

export async function getProduct(id: number) {
  const response = await fetch('/api/product/backoffice/products/' + id);
  return await response.json();
}

export async function createProduct(product: ProductPayload) {
  const response = await fetch('/api/product/backoffice/products', {
    method: 'POST',
    body: JSON.stringify(product),
    headers: { 'Content-Type': 'application/json' },
  });

  return response;
}

export async function updateProduct(id: number, product: ProductPayload) {
  const response = await fetch('/api/product/backoffice/products/' + id, {
    method: 'PUT',
    body: JSON.stringify(product),
    headers: { 'Content-Type': 'application/json' },
  });
  return response;
}

export async function deleteProduct(id: number) {
  const response = await fetch('/api/product/backoffice/products/' + id, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}

export async function getVariationsByProductId(productId: number): Promise<Variantion[]> {
  const res = await fetch(`/api/product/product-variations/${productId}`);
  if (res.status >= 200 && res.status < 300) return res.json();
  return Promise.reject(res);
}
