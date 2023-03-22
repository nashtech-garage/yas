export type AddToCartModel = {
  productId: number;
  quantity: number;
  parentProductId?: number | null;
};
