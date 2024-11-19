import { Media } from './Media';
import { ProductAttributeValue } from './ProductAttributeValue';
import { ProductOptionValuePost } from './ProductOptionValuePost';
import { ProductVariation } from './ProductVariation';

export type FormProduct = {
  name?: string;
  slug?: string;
  brandId?: number;
  categoryIds?: number[];
  description?: string;
  shortDescription?: string;
  specification?: string;
  sku?: string;
  gtin?: string;
  weight?: number;
  dimensionUnit?: string;
  length?: number;
  width?: number;
  height?: number;
  price?: number;
  isAllowedToOrder?: boolean;
  isPublished?: boolean;
  isFeatured?: boolean;
  isVisibleIndividually?: boolean;
  stockTrackingEnabled?: boolean;
  taxIncluded?: boolean;
  thumbnailMedia?: Media;
  productImageMedias?: Media[];
  metaTitle?: string;
  metaKeyword?: string;
  metaDescription?: string;
  relateProduct?: number[]; // product id
  crossSell?: number[]; // product id
  productAttributes?: ProductAttributeValue[];
  productVariations?: ProductVariation[];
  productOptionValuePost?: ProductOptionValuePost[];
  taxClassId?: number;
};
