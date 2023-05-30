export type OrderSearchForm = {
  createdFrom: Date;
  createdTo: Date;
  orderStatus: string[];
  productName: string;
  countryName: string;
  email: string;
  billingPhoneNumber: string;
  pageNo?: number;
  pageSize?: number;
};
