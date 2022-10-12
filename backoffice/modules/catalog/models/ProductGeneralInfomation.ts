export type ProductGeneralInformation = {
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
};
