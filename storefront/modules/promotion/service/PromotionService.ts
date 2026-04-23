import apiClientService from '@/common/services/ApiClientService';
import { PromotionVerifyRequest, PromotionVerifyResult } from '../model/Promotion';

export async function verifyPromotion(
  request: PromotionVerifyRequest
): Promise<PromotionVerifyResult> {
  return (
    await apiClientService.post(
      '/api/promotion/storefront/promotions/verify',
      JSON.stringify(request)
    )
  ).json();
}
