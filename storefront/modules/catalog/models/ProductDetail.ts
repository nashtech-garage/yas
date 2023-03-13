export type ProductDetail = {
  id: number;
  name: string;
  brandName: string;
  productCategories: string[];
  productAttributeGroups: {
    name: string;
    productAttributeValues: {
      name: string;
      value: string;
    }[];
  }[];
  shortDescription: string;
  description: string;
  specification: string;
  isAllowedToOrder: boolean;
  isPublished: boolean;
  isFeatured: boolean;
  price: number;
  averageStar: number;
  thumbnailMediaUrl: string;
  productImageMediaUrls: string[];
};
