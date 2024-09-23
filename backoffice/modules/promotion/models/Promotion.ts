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
  startDate: string;
  endDate: string;
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

export type PromotionDto = {
  id?: number;
  name: string;
  slug: string;
  description: string;
  couponCode: string;
  usageLimit: number;
  discountType: string;
  applyTo: string;
  usageType: string;
  discountPercentage?: number;
  discountAmount?: number;
  isActive: boolean;
  startDate: string;
  endDate: string;
  brandIds: number[];
  categoryIds: number[];
  productIds: number[];
};
