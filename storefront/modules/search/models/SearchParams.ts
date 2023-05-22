import { ESortType } from './SortType';

export type SearchParams = {
  keyword: string;
  brand?: string;
  category?: string;
  attribute?: string;
  minPrice?: number;
  maxPrice?: number;
  sortType?: ESortType;
  page?: number;
  pageSize?: number;
};
