import slugify from 'slugify';

import { FormProduct } from './FormProduct';
import { ProductOptionValuePost } from './ProductOptionValuePost';
import { ProductVariationPost } from './ProductVariationPost';

export type ProductPayload = {
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
  remainingQuantity?: number;
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
  productOptionValues?: ProductOptionValuePost[];
};

export function mapFormProductToProductPayload(data: FormProduct): ProductPayload {
  return {
    name: data.name,
    slug: data.slug,
    brandId: data.brandId,
    categoryIds: data.categoryIds,
    shortDescription: data.shortDescription,
    description: data.description,
    specification: data.specification,
    sku: data.sku,
    gtin: data.gtin,
    price: data.price,
    isAllowedToOrder: data.isAllowedToOrder,
    isPublished: data.isPublished,
    isFeatured: data.isFeatured,
    isVisibleIndividually: data.isVisibleIndividually,
    metaTitle: data.metaTitle,
    metaKeyword: data.metaKeyword,
    metaDescription: data.metaDescription,
    thumbnailMediaId: data.thumbnailMedia?.id,
    productImageIds: data.productImageMedias?.map((image) => image.id),
    remainingQuantity: data?.remainingQuantity,
    variations: data.productVariations
      ? data.productVariations.map((variant) => {
          return {
            name: variant.optionName,
            slug: slugify(variant.optionName),
            sku: variant.optionSku,
            gtin: variant.optionGTin,
            price: variant.optionPrice,
            thumbnailMediaId: variant.optionThumbnail?.id,
            productImageIds: variant.optionImages?.map((image) => image.id),
          };
        })
      : [],
    productOptionValues: data.productOptions?.map((option) => ({ ...option, displayOrder: 1 })),
  };
}
