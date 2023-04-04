export type OrderItemPost = {
  productId: number;
  productName: string;
  quantity: number;
  productPrice: number;
  note?: string;
  discountAmount?: number;
  taxAmount?: number;
  taxPercent?: number;
};
