import { Media } from './Media';

export type ProductVariation = {
  optionName: string;
  optionSku: string;
  optionGTin: string;
  optionPrice: number;
  optionRemainingQuantity: number;
  optionThumbnail?: Media;
  optionImages?: Media[];
};
