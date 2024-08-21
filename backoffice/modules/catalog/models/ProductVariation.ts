import { Media } from './Media';

export type ProductVariation = {
  id?: number;
  optionName: string;
  optionSku: string;
  optionGTin: string;
  optionPrice: number;
  optionThumbnail?: Media;
  optionImages?: Media[];
  optionValueMap: Record<number, string>; 
};

export type Variantion = {
  id: number;
  name: string;
  slug: string;
  sku: string;
  gtin: string;
  price: number;
  thumbnail: Media;
  productImages: Media[];
  options: {
    [key: string]: string;
  };
};
