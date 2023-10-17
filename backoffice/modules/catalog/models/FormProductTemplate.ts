export type ProductAttributeOfTemplate = {
    ProductAttributeId?: number;
    displayOder?: number;
}

export type FromProductTemplate = {
    name?: string;
    ProductAttributeTemplates?: ProductAttributeOfTemplate[];
};