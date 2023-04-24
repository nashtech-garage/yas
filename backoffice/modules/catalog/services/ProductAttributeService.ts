import { ProductAttribute } from '@catalogModels/ProductAttribute';
import { ProductAttributeForm } from '@catalogModels/ProductAttributeForm';

export async function getProductAttributes(): Promise<ProductAttribute[]> {
  const response = await fetch('/api/product/backoffice/product-attribute');
  return await response.json();
}

export async function getPageableProductAttributes(pageNo: number, pageSize: number) {
  const url = `/api/product/backoffice/product-attribute/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  const response = await fetch(url);
  return await response.json();
}

export async function createProductAttribute(productAttributePost: ProductAttributeForm) {
  const response = await fetch('/api/product/backoffice/product-attribute', {
    method: 'POST',
    body: JSON.stringify(productAttributePost),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status >= 200 && response.status < 300) return response;
  else return Promise.reject();
}

export async function updateProductAttribute(
  id: number,
  productAttributePut: ProductAttributeForm
) {
  const response = await fetch('/api/product/backoffice/product-attribute/' + id, {
    method: 'PUT',
    body: JSON.stringify(productAttributePut),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}

export async function getProductAttribute(id: number): Promise<ProductAttribute> {
  const response = await fetch('/api/product/backoffice/product-attribute/' + id);
  if (response.status >= 200 && response.status < 300) return response.json();
  else return Promise.reject();
}

export async function deleteProductAttribute(id: number) {
  const response = await fetch('/api/product/backoffice/product-attribute/' + id, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json; charset=utf-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}
