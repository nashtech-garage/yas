import { Category } from '../models/Category'

export async function getCategories(): Promise<Category[]> {
  const response = await fetch('http://storefront/api/product/storefront/categories');
  return await response.json();
}

export async function getCategory(id: number){
  const response = await fetch('http://storefront/api/product/storefront/categories/' + id);
  return await response.json();
}