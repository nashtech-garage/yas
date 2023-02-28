import { ProductThumbnail } from './ProductThumbnail';

export type ProductFeature = {
  totalPage: number;
  productList: ProductThumbnail[];
};

export type ProductAll = {
  totalPages?: number;
  productContent?: ProductThumbnail[];
  data?: any;
};
