export type CheckoutItem = {
  id?: number;
  productId: number;
  productName: string;
  quantity: number;
  productPrice: number;
  note?: string;
  discountAmount?: number;
  taxAmount?: number;
  taxPercent?: number;
  checkoutId?: string;
};
