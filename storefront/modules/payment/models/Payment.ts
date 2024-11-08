import { EPaymentMethod } from '../../common/models/EPaymentMethod';
import { EPaymentStatus } from './EPaymentStatus';

export type Payment = {
  id?: number;
  orderId?: number;
  checkoutId: string;
  amount: number;
  paymentFee?: number;
  paymentMethod?: EPaymentMethod;
  paymentStatus?: EPaymentStatus;
  gatewayTransactionId?: string;
  failureMessage?: string;
  paymentProviderCheckoutId?: string;
  attributes?: string;
};
