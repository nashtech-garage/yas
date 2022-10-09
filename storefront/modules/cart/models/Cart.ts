import { CartItem } from './CartItem';

export type Cart = {
  id: number;
  customerId: string;
  cartDetails: CartItem[];
};
