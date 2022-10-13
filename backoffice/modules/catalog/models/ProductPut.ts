export type ProductPut = {
  name?: string;
  slug?: string;
  shortDescription?: string;
  description?: string;
  specification?: string;
  sku?: string;
  gtin?: string;
  price?: number;
  isAllowedToOrder?: boolean;
  isPublished?: boolean;
  isFeatured?: boolean;
  brandId?: number;
  categoryIds?: number[];
  metaKeyword?: string;
  metaDescription?: string;
  thumbnailMediaId?: number;
  productImageIds?: number[];
};
