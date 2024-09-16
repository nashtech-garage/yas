export type ProductVm = {
  id: number;
  name: string;
  slug: string;
  isAllowedToOrder: boolean;
  isPublished: boolean;
  isFeatured: boolean;
  isVisibleIndividually: boolean;
  createdOn: Date;
  taxClassId: number;
};
