import { EPaymentMethod } from '@/modules/common/models/EPaymentMethod';
import { CheckoutItem } from './CheckoutItem';
import { ECheckoutState } from './ECheckoutState';
import { ECheckoutProgress } from './ECheckoutProgress';

export type Checkout = {
  id?: string;
  email: string;
  note?: string;
  couponCode?: string;
  checkoutState?: ECheckoutState;
  progress?: ECheckoutProgress;
  paymentMethodId?: EPaymentMethod;
  attributes?: string;
  lastError?: string;
  totalAmount: number;
  totalDiscountAmount: number;
  checkoutItemVms: CheckoutItem[];
};
