import { BrandVm } from './Brand';
import { CategoryGetVm } from './Category';
import { ProductVm } from './Product';

export type PromotionDetail = {
  id: number;
  name: string;
  slug: string;
  description: string;
  couponCode: string;
  usageLimit: number;
  usageCount: number;
  discountType: string;
  applyTo: string;
  usageType: string;
  discountPercentage: number;
  discountAmount: number;
  isActive: boolean;
  startDate: Date;
  endDate: Date;
  brands: BrandVm[];
  categories: CategoryGetVm[];
  products: ProductVm[];
};

export type PromotionPage = {
  promotionDetailVmList: PromotionDetail[];
  pageNo: number;
  pageSize: number;
  totalPage: number;
  totalElements: number;
};

export type PromotionListRequest = {
  promotionName: string;
  couponCode: string;
  pageNo: number;
  pageSize: number;
};
