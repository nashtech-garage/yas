import { CheckoutItem } from './CheckoutItem';

export type Checkout = {
  id?: string;
  email: string;
  note?: string;
  couponCode?: string;
  checkoutItemPostVms: CheckoutItem[];
};
