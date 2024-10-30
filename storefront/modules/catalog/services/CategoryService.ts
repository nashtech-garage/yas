import { Category } from '../models/Category';
import apiClientService from '@/common/services/ApiClientService';

const baseUrl = '/api/product/storefront/categories';

export async function getCategories(): Promise<Category[]> {
  const response = await apiClientService.get(baseUrl);
  return await response.json();
}

export async function getCategory(id: number) {
  const response = await apiClientService.get(`${baseUrl}/${id}`);
  return await response.json();
}

export async function getCategoriesSuggestions(): Promise<string[]> {
  const response = await apiClientService.get(`${baseUrl}/suggestions`);
  return await response.json();
}
