import { ProductAttributeValue } from '../models/ProductAttributeValue';
import { ProductAttributeValuePost } from '../models/ProductAttributeValuePost';
import apiClientService from '@commonServices/ApiClientService';
import { PRODUCT_BACKOFFICE_ATTRIBUTE_VALUE_ENDPOINT } from '@constants/Endpoints';

const baseUrl = PRODUCT_BACKOFFICE_ATTRIBUTE_VALUE_ENDPOINT;

export async function getAttributeValueOfProduct(
  productId: number
): Promise<ProductAttributeValue[]> {
  const url = `${baseUrl}/${productId}`;
  return (await apiClientService.get(url)).json();
}

export async function createProductAttributeValueOfProduct(
  productAttributeValuePost: ProductAttributeValuePost
): Promise<ProductAttributeValuePost> {
  return (await apiClientService.post(baseUrl, JSON.stringify(productAttributeValuePost))).json();
}

export async function updateProductAttributeValueOfProduct(
  id: number,
  productAttributeValuePost: ProductAttributeValuePost
): Promise<number> {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(productAttributeValuePost));
  return response.status;
}

export async function deleteProductAttributeValueOfProductById(id: number): Promise<number> {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  return response.status;
}
