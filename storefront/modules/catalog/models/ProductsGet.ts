import { ProductThumbnail } from './ProductThumbnail';

export type ProductsGet = {
  productContent: ProductThumbnail[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
};
