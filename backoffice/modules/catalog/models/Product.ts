import { Category } from './Category';

export type Product = {
  id: number;
  name: string;
  shortDescription: string;
  description: string;
  specification: string;
  sku: string;
  gtin: string;
  slug: string;
  price: number;
  metaKeyword: string;
  metaDescription: string;
  isAllowedToOrder: boolean;
  isPublished: boolean;
  isFeatured: boolean;
  brandId: number;
  categories: Category[];
  thumbnailMediaUrl: string;
  productImageMediaUrls: string[];
};
