export type ProductOptionsValuePost = {
  productOptionId?: number;
  value: string[];
  displayType?: string;
  displayOrder?: number;
};

export type ProductOptionValuePost = {
  productOptionId?: number;
  value: {
    [key: string]: string;
  };
  displayType?: string;
  displayOrder?: number;
};

export type ProductOptionValueDisplay = {
  productOptionId?: number;
  value: string;
  displayType?: string;
  displayOrder?: number;
  productOptionName?: string;
};

export type ProductOptionValueDisplayGet = {
  productOptionId?: number;
  productOptionValue: string;
  displayType?: string;
  displayOrder?: number;
  productOptionName?: string;
};
