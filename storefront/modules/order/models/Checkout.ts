import { CheckoutItem } from './CheckoutItem';

export type Checkout = {
  id?: string;
  email: string;
  note?: string;
  couponCode?: string;
  totalAmount: number;
  totalDiscountAmount: number;
  checkoutItemPostVms: CheckoutItem[];
};
