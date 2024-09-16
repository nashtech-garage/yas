import apiClientService from '@commonServices/ApiClientService';
import { PromotionListRequest } from '../models/Promotion';

const baseUrl = '/api/promotion/backoffice/promotions';

export async function getPromotions(request: PromotionListRequest) {
  const url = `${baseUrl}?${createRequestFromObject(request)}`;
  return (await apiClientService.get(url)).json();
}

function createRequestFromObject(request: any): string {
  return Object.keys(request)
    .map(function (key) {
      return key + '=' + request[key];
    })
    .join('&');
}
