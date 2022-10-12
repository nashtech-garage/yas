import { Product } from './Product';
export type ProductPost = {
  name: string;
  slug: string;
  description: string;
  shortDescription: string;
  specification: string;
  sku: string;
  gtin: string;
  price: number;
  isAllowedToOrder: boolean;
  isPublished: boolean;
  isFeatured: boolean;
  brandId?: number;
  thumbnail?: File;
  productImages?: FileList;
  metaTitle: string;
  metaKeyword: string;
  metaDescription: string;
  relateProduct: number[]; // product id
  crossSell: number[]; // product id
  categoryIds: number[]; // category id
  product:Product
};
