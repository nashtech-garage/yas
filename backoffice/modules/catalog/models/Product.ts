export type Product = {
  id: number;
  name: string;
  slug: string;
  description: string;
  shortDescription: string;
  specification:string[];
  sku:string;
  gtin:string;
  price:number;
  thumbnail:File;
  productImages:File[];
  isAllowedToOrder:boolean;
  isPublished:boolean;
  isFeatured:boolean;
  brand:string;
  category:string[];
};