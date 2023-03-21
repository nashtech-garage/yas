export type ProductVariationPost = {
  name?: string;
  slug?: string;
  sku?: string;
  gtin?: string;
  price?: number;
  remainingQuantity?: number;
  thumbnailMediaId?: number;
  productImageIds?: number[];
};
