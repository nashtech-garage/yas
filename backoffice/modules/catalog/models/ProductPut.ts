import { ProductVariationPost } from './ProductVariationPost';

export type ProductPut = {
  name?: string;
  slug?: string;
  brandId?: number;
  categoryIds?: number[];
  shortDescription?: string;
  description?: string;
  specification?: string;
  sku?: string;
  gtin?: string;
  price?: number;
  isAllowedToOrder?: boolean;
  isPublished?: boolean;
  isFeatured?: boolean;
  isVisibleIndividually?: boolean;
  metaTitle?: string;
  metaKeyword?: string;
  metaDescription?: string;
  thumbnailMediaId?: number;
  productImageIds?: number[];
  variations?: ProductVariationPost[];
};
