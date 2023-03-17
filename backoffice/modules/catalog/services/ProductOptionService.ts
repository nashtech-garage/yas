import { ProductOption } from '../models/ProductOption';

export async function getProductOptions(): Promise<ProductOption[]> {
  const response = await fetch('/api/product/backoffice/product-options');
  return await response.json();
}

export async function getProductOption(id: number) {
  const response = await fetch('/api/product/backoffice/product-options/' + id);
  return await response.json();
}
export async function createProductOption(productOption: ProductOption) {
  const response = await fetch('/api/product/backoffice/product-options', {
    method: 'POST',
    body: JSON.stringify(productOption),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return await response;
}
export async function updateProductOption(id: number, productOption: ProductOption) {
  const response = await fetch('/api/product/backoffice/product-options/' + id, {
    method: 'PUT',
    body: JSON.stringify(productOption),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return await response;
  else return await response.json();
}

export async function deleteProductOption(id: number) {
  const response = await fetch('/api/product/backoffice/product-options/' + id, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}
