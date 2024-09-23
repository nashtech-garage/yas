export type ProductAttributeOfTemplate = {
  productAttributeId?: number;
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
  productAttributeTemplates?: ProductAttributeOfTemplate[];
};

export type ProductTemplate = {
  id?: number;
  name?: string;
  productAttributeTemplates?: ProductAttributeTemplate[];
};
