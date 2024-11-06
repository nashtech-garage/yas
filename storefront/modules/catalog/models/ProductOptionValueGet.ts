export type ProductOptionValueGet = {
  id: number;
  productOptionName: string;
  productOptionId: number;
  productOptionValue: string;
};

export type ProductOptionValueDisplay = {
  id: number,
  productOptionId: number;
  productOptionValue: string;
  displayType?: string;
  displayOrder?: number;
  productOptionName: string
}