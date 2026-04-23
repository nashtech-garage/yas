export type PromotionVerifyRequest = {
  couponCode: string;
  orderPrice: number;
  productIds: number[];
};

export type PromotionVerifyResult = {
  isValid: boolean;
  productId: number;
  couponCode: string;
  discountType: string;
  discountValue: number;
};
