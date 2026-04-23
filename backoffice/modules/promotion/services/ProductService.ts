import apiClientService from '@commonServices/ApiClientService';

const searchProductUrl = '/api/product/backoffice/products';
const searchCategoriesUrl = '/api/product/backoffice/categories';
const searchBrandsUrl = '/api/product/backoffice/brands';

export async function searchProducts(name: string) {
  const url = `${searchProductUrl}?product-name=${name}`;
  return (await apiClientService.get(url)).json();
}

export async function searchCategories(name: string) {
  const url = `${searchCategoriesUrl}?categoryName=${name}`;
  return (await apiClientService.get(url)).json();
}

export async function searchBrands(name: string) {
  const url = `${searchBrandsUrl}?brandName=${name}`;
  return (await apiClientService.get(url)).json();
}
