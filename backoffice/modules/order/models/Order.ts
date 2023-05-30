import { OrderItem } from './OrderItem';
import { OrderAddress } from './OrderAddress';
export type Order = {
  id?: number;
  email: string;
  note?: string;
  tax?: number;
  discount?: number;
  numberItem: number;
  totalPrice: number;
  deliveryFee?: number | 0;
  couponCode?: string;
  deliveryMethod: string;
  deliveryStatus?: string;
  paymentMethod: string;
  paymentStatus: string;
  createdOn: Date;
  orderStatus: string;
  orderItemPostVms: OrderItem[];
  shippingAddressVm: OrderAddress;
  billingAddressVm: OrderAddress;

  checkoutId?: string;
};
