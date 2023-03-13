import { Category } from '../models/Category';
import { ProductThumbnails } from '../models/ProductThumbnails';

export async function getCategories(): Promise<Category[]> {
  const response = await fetch('/api/product/backoffice/categories');
  return await response.json();
}

export async function getCategory(id: number): Promise<Category> {
  const response = await fetch('/api/product/backoffice/categories/' + id);
  return await response.json();
}

export async function createCategory(category: Category): Promise<Category> {
  const response = await fetch('/api/product/backoffice/categories', {
    method: 'POST',
    body: JSON.stringify(category),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 201) return await response;
  else return await response.json();
}
export async function updateCategory(id: number, category: Category) {
  const response = await fetch('/api/product/backoffice/categories/' + id, {
    method: 'PUT',
    body: JSON.stringify(category),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return await response;
  else return await response.json();
}
export async function deleteCategory(id: number) {
  const response = await fetch('/api/product/backoffice/categories/' + id, {
    method: 'DELETE',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return await response;
  else return await response.json();
}

export async function getProductsByCategory(
  pageNo: number,
  categorySlug: string
): Promise<ProductThumbnails> {
  const url = `/api/product/storefront/category/${categorySlug}/products?pageNo=${pageNo}`;
  const response = await fetch(url);
  return await response.json();
}
