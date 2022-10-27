import { ProductOptionValuePost } from './ProductOptionValuePost';
import { ProductVariation } from './ProductVariation';
import { ProductAttributeValue } from './ProductAttributeValue';

export type ProductPost = {
  name?: string;
  slug?: string;
  description?: string;
  shortDescription?: string;
  specification?: string;
  sku?: string;
  gtin?: string;
  price?: number;
  isAllowedToOrder?: boolean;
  isPublished?: boolean;
  isFeatured?: boolean;
  isVisibleIndividually?: boolean;
  brandId?: number;
  thumbnail?: File;
  productImages?: FileList;
  metaTitle?: string;
  metaKeyword?: string;
  metaDescription?: string;
  relateProduct?: number[]; // product id
  crossSell?: number[]; // product id
  categoryIds?: number[]; // category id
  productAttributes?: ProductAttributeValue[];
  productVariations?: ProductVariation[];
  productOptions?: ProductOptionValuePost[];
  thumbnailMediaId?: number;
  productImageIds?: number[];
};
