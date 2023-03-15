import { Category } from './Category';
import { Image } from './Image';

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
  metaTitle: string;
  metaKeyword: string;
  metaDescription: string;
  isAllowedToOrder: boolean;
  isPublished: boolean;
  isFeatured: boolean;
  isVisible?: boolean;
  brandId: number;
  categories: Category[];
  thumbnailMedia: Image;
  productImageMedias: Image[];
};
