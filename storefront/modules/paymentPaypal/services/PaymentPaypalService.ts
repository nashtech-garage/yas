import { InitPaymentPaypalRequest } from '@/modules/paymentPaypal/models/InitPaymentPaypalRequest';
import { InitPaymentPaypalResponse } from '@/modules/paymentPaypal/models/InitPaymentPaypalResponse';
import { CapturePaymentPaypalResponse } from '@/modules/paymentPaypal/models/CapturePaymentPaypalResponse';
import apiClientService from '@/common/services/ApiClientService';

const baseUrl = '/api/payment-paypal';

export async function initPaymentPaypal(
  paymentPaypalRequest: InitPaymentPaypalRequest
): Promise<InitPaymentPaypalResponse> {
  const res = await apiClientService.post(`${baseUrl}/init`, JSON.stringify(paymentPaypalRequest));
  if (res.ok) {
    return res.json();
  }
  throw new Error(res.statusText);
}

export async function capturePaymentPaypal(token?: string): Promise<CapturePaymentPaypalResponse> {
  const res = await apiClientService.get(`${baseUrl}/capture?token=${token}`);
  if (res.ok) {
    return res.json();
  }
  throw new Error(res.statusText);
}
