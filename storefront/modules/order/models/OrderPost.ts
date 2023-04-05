import { OrderItemPost } from './OrderItemPost';

export type OrderPost = {
  phone: string;
  email: string;
  addressId: number;
  note?: string;
  tax?: number;
  discount?: number;
  numberItem: number;
  totalPrice: number;
  deliveryFee: number;
  couponCode?: string;
  deliveryMethod: number;
  deliveryStatus: number;
  paymentMethod: string;
  paymentStatus: string;
  orderItemPostVms: OrderItemPost[];
};
