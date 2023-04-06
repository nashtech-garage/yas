import { ProductSearchSuggestions } from './../../rating/models/ProductSearchSuggestions';

export async function getSuggestions(keyword: string): Promise<ProductSearchSuggestions> {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_BASE_PATH}/search/storefront/search_suggest?keyword=${keyword}`,
    {
      method: 'GET',
      headers: { 'Content-type': 'application/json; charset=UTF-8' },
    }
  );
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }

  return Promise.reject(response.status);
}
