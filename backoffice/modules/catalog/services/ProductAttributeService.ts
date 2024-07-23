import { ProductAttribute } from '../models/ProductAttribute';
import apiClientService from '@commonServices/ApiClientService';
import { PRODUCT_BACKOFFICE_ATTRIBUTE_ENDPOINT } from '@constants/Endpoints';

const baseUrl = PRODUCT_BACKOFFICE_ATTRIBUTE_ENDPOINT;

interface ProductAttributeId {
  name: string;
  productAttributeGroupId: string;
}
export async function getProductAttributes(): Promise<ProductAttribute[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableProductAttributes(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function createProductAttribute(productAttributePost: ProductAttributeId) {
  return await apiClientService.post(baseUrl, JSON.stringify(productAttributePost));
}

export async function updateProductAttribute(id: number, productAttributeId: ProductAttributeId) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(productAttributeId));
  if (response.status === 204) return response;
  else return await response.json();
}

export async function getProductAttribute(id: number): Promise<ProductAttribute> {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteProductAttribute(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}
