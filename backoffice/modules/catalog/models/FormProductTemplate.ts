export type ProductAttributeOfTemplate = {
  ProductAttributeId?: number;
  displayOrder?: number;
};
export type productAttribute = {
  id: number;
  name: string;
};

export type ProductAttributeTemplate = {
  displayOrder?: number;
  productAttribute?: productAttribute;
};

export type FromProductTemplate = {
  name?: string;
  ProductAttributeTemplates?: ProductAttributeOfTemplate[];
};

export type ProductTemplate = {
  id?: number;
  name?: string;
  productAttributeTemplates?: ProductAttributeTemplate[];
};
