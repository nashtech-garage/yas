import slugify from 'slugify';

import { FormProduct } from './FormProduct';
import { ProductOptionValuePost } from './ProductOptionValuePost';
import { ProductVariation } from './ProductVariation';
import { ProductVariationPost } from './ProductVariationPost';
import { ProductVariationPut } from './ProductVariationPut';

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
  metaTitle?: string;
  metaKeyword?: string;
  metaDescription?: string;
  thumbnailMediaId?: number;
  productImageIds?: number[];
  variations?: ProductVariationPost[] | ProductVariationPut[];
  productOptionValues?: ProductOptionValuePost[];
  relatedProductIds?: number[];
  taxClassId?: number;
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
    weight: data.weight,
    dimensionUnit: data.dimensionUnit,
    length: data.length,
    width: data.width,
    height: data.height,
    price: data.price,
    isAllowedToOrder: data.isAllowedToOrder,
    isPublished: data.isPublished,
    isFeatured: data.isFeatured,
    isVisibleIndividually: data.isVisibleIndividually,
    stockTrackingEnabled: data.stockTrackingEnabled,
    taxIncluded: data.taxIncluded,
    metaTitle: data.metaTitle,
    metaKeyword: data.metaKeyword,
    metaDescription: data.metaDescription,
    thumbnailMediaId: data.thumbnailMedia?.id,
    productImageIds: data.productImageMedias?.map((image) => image.id),
    variations: data.productVariations
      ? data.productVariations.map((variant) => {
          return {
            id: variant.id,
            name: variant.optionName,
            slug: slugify(variant.optionName, { lower: true, strict: true }),
            sku: variant.optionSku,
            gtin: variant.optionGTin,
            price: variant.optionPrice,
            thumbnailMediaId: variant.optionThumbnail?.id,
            productImageIds: variant.optionImages?.map((image) => image.id),
            optionValuesByOptionId: variant.optionValuesByOptionId,
          };
        })
      : [],
    productOptionValues: createProductOptionValues(data.productVariations || []),
    relatedProductIds: data.relateProduct,
    taxClassId: data.taxClassId,
  };
}

const createProductOptionValues = (productVariations: ProductVariation[]) => {
  let productOptionValues: ProductOptionValuePost[] = [];
  productVariations.forEach((variation) => {
    const option = variation.optionValuesByOptionId;
    Object.entries(option).forEach((entry) => {
      const id = +entry[0];
      const value = entry[1];
      let optionValue = productOptionValues.find((option) => {
        return option.productOptionId === id;
      });
      if (optionValue) {
        if (!optionValue.value.includes(value)) {
          optionValue.value.push(value);
        }
      } else {
        productOptionValues.push({
          productOptionId: id,
          displayOrder: 1,
          value: [value],
        });
      }
    });
  });
  return productOptionValues;
};
