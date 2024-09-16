import { PaymentProvider } from '../models/PaymentProvider';
import apiClientService from '@/common/services/ApiClientService';

export const getEnabledPaymentProviders = async (): Promise<PaymentProvider[]> => {
  const response = await apiClientService.get('/api/payment/storefront/payment-providers');
  return response.json();
};
