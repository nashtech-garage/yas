import apiClientService from '@commonServices/ApiClientService';
import { PromotionDto, PromotionListRequest } from '../models/Promotion';

const baseUrl = '/api/promotion/backoffice/promotions';

export async function getPromotions(request: PromotionListRequest) {
  const url = `${baseUrl}?${createRequestFromObject(request)}`;
  return (await apiClientService.get(url)).json();
}

export async function createPromotion(promotion: PromotionDto) {
  const url = `${baseUrl}`;
  return (await apiClientService.post(url, JSON.stringify(promotion))).json();
}

export async function updatePromotion(promotion: PromotionDto) {
  const url = `${baseUrl}/${promotion.id}`;
  return (await apiClientService.put(url, promotion)).json();
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
