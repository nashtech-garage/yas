import { PaymentProvider } from '../models/PaymentProvider';

export const getEnabledPaymentProviders = async (): Promise<PaymentProvider> => {
  const response = await fetch('/payment/payment-providers', {
    method: 'GET',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });

  if (response.status >= 200 && response.status < 300) return await response.json();
  return Promise.reject(response);
};
