import { ProductThumbnail } from "./ProductThumbnail";

export type ProductThumbnails = {
    productContent: ProductThumbnail[];
    pageNo: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
    isLast: boolean;
}