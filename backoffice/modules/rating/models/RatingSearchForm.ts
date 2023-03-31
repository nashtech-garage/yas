export type RatingSearchForm = {
  createdFrom: Date;
  createdTo: Date;
  brandName: string;
  productName: string;
  customerName: string;
  message: string;
  pageNo?: number;
  pageSize?: number;
};
