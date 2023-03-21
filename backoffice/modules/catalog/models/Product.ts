import { Category } from './Category';
import { Media } from './Media';

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
  stockTrackingEnabled: boolean;
  brandId: number;
  categories: Category[];
  thumbnailMedia: Media;
  productImageMedias: Media[];
};
