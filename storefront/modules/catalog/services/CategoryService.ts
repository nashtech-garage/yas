import { Category } from '../models/Category'

export async function getCategories(): Promise<Category[]> {
  const response = await fetch('/api/product/categories');
  return await response.json();
}

export async function getCategory(id: number){
  const response = await fetch('/api/product/categories/' + id);
  return await response.json();
}