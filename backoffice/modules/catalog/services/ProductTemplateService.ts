import { ProductTemplate } from '@catalogModels/ProductTemplate';
import { FromProductTemplate } from '@catalogModels/FormProductTemplate';
import apiClientService from '@commonServices/ApiClientService';
import { PRODUCT_BACKOFFICE_TEMPLATE_ENDPOINT } from '@constants/Endpoints';

const baseUrl = PRODUCT_BACKOFFICE_TEMPLATE_ENDPOINT;

export async function getProductTemplates(): Promise<ProductTemplate[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableProductTemplates(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function createProductTemplate(fromProductTemplate: FromProductTemplate) {
  return await apiClientService.post(baseUrl, JSON.stringify(fromProductTemplate));
}
export async function getProductTemplate(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function updateProductTemplate(id: number, fromProductTemplate: FromProductTemplate) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(fromProductTemplate));
  if (response.status === 204) return response;
  else return await response.json();
}
