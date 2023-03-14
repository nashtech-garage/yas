import { ProductAttribute } from '../models/ProductAttribute';

interface ProductAttributeId {
  name: string;
  productAttributeGroupId: string;
}
export async function getProductAttributes(): Promise<ProductAttribute[]> {
  const response = await fetch('/api/product/backoffice/product-attribute');
  return await response.json();
}

export function createProductAttribute(productAttributePost: ProductAttributeId) {
  return fetch('/api/product/backoffice/product-attribute', {
    method: 'POST',
    body: JSON.stringify(productAttributePost),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
}

export async function updateProductAttribute(
  id: number,
  productAttributeId: ProductAttributeId
): Promise<Number> {
  const res = await fetch('/api/product/backoffice/product-attribute/' + id, {
    method: 'PUT',
    body: JSON.stringify(productAttributeId),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return res.status;
}

export async function getProductAttribute(id: number): Promise<ProductAttribute> {
  const response = await fetch('/api/product/backoffice/product-attribute/' + id);
  return await response.json();
}

export async function deleteProductAttribute(id: number) {
  const response = await fetch('/api/product/backoffice/product-attribute/' + id, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json; charset=utf-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}
