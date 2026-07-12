import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { getEnabledPaymentProviders } from './PaymentProviderService';

describe('PaymentProviderService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getEnabledPaymentProviders should call endpoint and return json', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue([{ id: 'paypal' }]),
    } as unknown as Response);

    const result = await getEnabledPaymentProviders();

    expect(apiClientService.get).toHaveBeenCalledWith('/api/payment/storefront/payment-providers');
    expect(result).toEqual([{ id: 'paypal' }]);
  });
});
