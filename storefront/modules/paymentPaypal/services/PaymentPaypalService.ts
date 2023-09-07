import { InitPaymentPaypalRequest } from '@/modules/paymentPaypal/models/InitPaymentPaypalRequest';
import { InitPaymentPaypalResponse } from '@/modules/paymentPaypal/models/InitPaymentPaypalResponse';
import { CapturePaymentPaypalResponse } from '@/modules/paymentPaypal/models/CapturePaymentPaypalResponse';

export async function initPaymentPaypal(
  paymentPaypalRequest: InitPaymentPaypalRequest
): Promise<InitPaymentPaypalResponse> {
  const res = await fetch('/api/payment-paypal/init', {
    method: 'POST',
    body: JSON.stringify(paymentPaypalRequest),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (res.ok) {
    return res.json();
  }
  return Promise.reject(res.status);
}

export async function capturePaymentPaypal(token?: string): Promise<CapturePaymentPaypalResponse> {
  const res = await fetch(`/api/payment-paypal/capture?token=${token}`);
  if (res.ok) {
    return res.json();
  }
  return Promise.reject(res.status);
}
