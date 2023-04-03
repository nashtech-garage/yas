import { Media } from './Media';

export type ProductVariation = {
  optionName: string;
  optionSku: string;
  optionGTin: string;
  optionPrice: number;
  optionThumbnail?: Media;
  optionImages?: Media[];
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
