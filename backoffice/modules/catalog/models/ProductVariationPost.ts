export type ProductVariationPost = {
  name?: string;
  slug?: string;
  sku?: string;
  gtin?: string;
  price?: number;
  thumbnailMediaId?: number;
  productImageIds?: number[];
  optionValuesByOptionId?: Object;
};
