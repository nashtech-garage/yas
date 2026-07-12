import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    post: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { capturePaymentPaypal, initPaymentPaypal } from './PaymentPaypalService';

describe('PaymentPaypalService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('initPaymentPaypal should return json when response ok', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      ok: true,
      json: vi.fn().mockResolvedValue({ orderId: 'o1' }),
    } as unknown as Response);

    const request = { amount: 10 };
    const result = await initPaymentPaypal(request as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/payment/init',
      JSON.stringify(request)
    );
    expect(result).toEqual({ orderId: 'o1' });
  });

  test('initPaymentPaypal should throw when response not ok', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      ok: false,
      statusText: 'Bad Request',
    } as unknown as Response);

    await expect(initPaymentPaypal({} as never)).rejects.toThrow('Bad Request');
  });

  test('capturePaymentPaypal should throw when response not ok', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      ok: false,
      statusText: 'Capture Failed',
    } as unknown as Response);

    await expect(capturePaymentPaypal({} as never)).rejects.toThrow('Capture Failed');
  });
});
