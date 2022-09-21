import { Category } from '../models/Category'

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
    headers: {"Content-type": "application/json; charset=UTF-8"}
  })
  return await response.json();
}
export async function updateCategory(id: number, category: Category){
  const response = await fetch('/api/product/backoffice/categories/'+id, {
    method: 'PUT',
    body: JSON.stringify(category),
    headers: {"Content-type": "application/json; charset=UTF-8"}
  })
  if(response.status ===204) return await response;
  else return await response.json();
}