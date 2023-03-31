import { OrderItemPost } from './OrderItemPost';

export type OrderPost = {
  phone: string;
  address: string;
  note?: string;
  tax?: number;
  discount?: number;
  numberItem: number;
  totalPrice: number;
  deliveryFee: number;
  deliveryMethod: number;
  deliveryStatus: number;
  paymentMethod: string;
  orderItemPostVms: OrderItemPost[];

  lastName: string;
  firstName: string;
  country: string;
  district: string;
  stateOrProvince: string;
  postalCode: string;
  email: string;
};
