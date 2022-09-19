export type Product = {
  id: number;
  name: string;
  slug: string;
  description: string;
  shortDescription: string;
  specification: string;
  sku: string;
  gtin: string;
  price: number;
  thumbnail: FileList;
  productImages: File[];
  isAllowedToOrder: boolean;
  isPublished: boolean;
  isFeatured: boolean;
  brand: number;
  categoriesId: number[];
  metaKeyword: string,
  metaDescription: string
};