import { stringify } from 'querystring';
import { ProductAttributeGroup } from '../models/ProductAttributeGroup';
import apiClientService from '@commonServices/ApiClientService';
import { PRODUCT_BACKOFFICE_ATTRIBUTE_GROUP_ENDPOINT } from '@constants/Endpoints';

const baseUrl = PRODUCT_BACKOFFICE_ATTRIBUTE_GROUP_ENDPOINT;

export async function getProductAttributeGroups(): Promise<ProductAttributeGroup[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableProductAttributeGroups(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function getProductAttributeGroup(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function createProductAttributeGroup(productAttributeGroup: ProductAttributeGroup) {
  return await apiClientService.post(baseUrl, JSON.stringify(productAttributeGroup));
}

export async function updateProductAttributeGroup(
  id: number,
  productAttributeGroup: ProductAttributeGroup
) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(productAttributeGroup));
  if (response.status === 204) return response;
  else return await response.json();
}

export async function deleteProductAttributeGroup(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}
