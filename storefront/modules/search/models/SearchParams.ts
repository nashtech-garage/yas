export type SearchParams = {
  keyword: string;
  category?: string;
  attribute?: string;
  minPrice?: number;
  maxPrice?: number;
  sortType?: 'DEFAULT' | 'PRICE_ASC' | 'PRICE_DESC';
  page?: number;
  pageSize?: number;
};
