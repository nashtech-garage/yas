import { Product } from './Product';

export type Products = {
  productContent: Product[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
};
