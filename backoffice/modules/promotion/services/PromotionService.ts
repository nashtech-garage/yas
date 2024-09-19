import apiClientService from '@commonServices/ApiClientService';
import { PromotionDto, PromotionListRequest } from '../models/Promotion';

const baseUrl = '/api/promotion/backoffice/promotions';

export async function getPromotions(request: PromotionListRequest) {
  const url = `${baseUrl}?${createRequestFromObject(request)}`;
  return (await apiClientService.get(url)).json();
}

export async function createPromotion(promotion: PromotionDto) {
  return await apiClientService.post(baseUrl, JSON.stringify(promotion));
}

export async function updatePromotion(promotion: PromotionDto) {
  return await apiClientService.put(baseUrl, JSON.stringify(promotion));
}

export async function getPromotion(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

function createRequestFromObject(request: any): string {
  return Object.keys(request)
    .map(function (key) {
      return key + '=' + request[key];
    })
    .join('&');
}

export const cancel = () => {
  window.location.href = '/promotion/manager-promotion';
};
