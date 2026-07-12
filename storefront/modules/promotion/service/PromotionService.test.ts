import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    post: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { verifyPromotion } from './PromotionService';

describe('PromotionService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('verifyPromotion should post payload and return json', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      json: vi.fn().mockResolvedValue({ valid: true }),
    } as unknown as Response);

    const payload = { code: 'PROMO10' };
    const result = await verifyPromotion(payload as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/promotion/storefront/promotions/verify',
      JSON.stringify(payload)
    );
    expect(result).toEqual({ valid: true });
  });
});
