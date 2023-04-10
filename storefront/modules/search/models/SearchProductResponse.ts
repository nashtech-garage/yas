import { ProductSearchResult } from './ProductSearchResult';

export type SearchProductResponse = {
  product: ProductSearchResult[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
  aggregations: {
    [key: string]: {
      [key: string]: number;
    };
  };
};
