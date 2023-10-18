import { ProductTemplate } from '@catalogModels/ProductTemplate';
import { FromProductTemplate } from '@catalogModels/FormProductTemplate';

export async function getProductTemplates(): Promise<ProductTemplate[]> {
  const response = await fetch('/api/product/backoffice/product-template');
  return await response.json();
}

export async function getPageableProductTemplates(pageNo: number, pageSize: number) {
  const response = await fetch(
    `/api/product/backoffice/product-template/paging?pageNo=${pageNo}&pageSize=${pageSize}`
  );
  return await response.json();
}

export async function createProductTemplate(fromProductTemplate: FromProductTemplate) {
  const response = await fetch(`/api/product/backoffice/product-template`, {
    method: 'POST',
    body: JSON.stringify(fromProductTemplate),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return response;
}
export async function getProductTemplate(id: number) {
  const response = await fetch('/api/product/backoffice/product-template/' + id);
  return await response.json();
}

export async function updateProductTemplate(id: number, fromProductTemplate: FromProductTemplate) {
  const response = await fetch('/api/product/backoffice/product-template/' + id, {
    method: 'PUT',
    body: JSON.stringify(fromProductTemplate),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}
