import { PaymentProvider } from '../models/PaymentProvider';

export const getEnabledPaymentProviders = async (): Promise<PaymentProvider[]> => {
  const response = await fetch('/api/payment/storefront/payment-providers');
  return response.json();
};
