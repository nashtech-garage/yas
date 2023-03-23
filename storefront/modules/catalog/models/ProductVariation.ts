export type ProductVariation = {
  id: number;
  name: string;
  slug: string;
  sku: string;
  gtin: string;
  price: number;
  thumbnailMediaUrl: string;
  productImageMediaUrls: string[];
  options: {
    [key: string]: string;
  };
};
