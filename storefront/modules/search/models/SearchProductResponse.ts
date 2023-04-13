import { Aggregations } from './Aggregations';
import { ProductSearchResult } from './ProductSearchResult';

export type SearchProductResponse = {
  products: ProductSearchResult[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
  aggregations: Aggregations;
};
