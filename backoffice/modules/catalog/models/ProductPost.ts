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
  categoryIds: number[];
  metaKeyword: string;
  metaDescription: string;
  thumbnail?: File;
  productImages?: FileList;
};
