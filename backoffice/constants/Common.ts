export const ToastVariant = {
  SUCCESS: 'success',
  WARNING: 'warning',
  ERROR: 'error',
};
export const ResponseStatus = {
  CREATED: 201,
  SUCCESS: 204,
  NOT_FOUND: 404,
  BAD_REQUEST: 400,
};
export const ResponseTitle = {
  NOT_FOUND: 'Not Found',
  BAD_REQUEST: 'Bad Request',
};
export const HAVE_BEEN_DELETED = ' have been deleted';
export const DELETE_FAILED = 'Delete failed';
export const UPDATE_SUCCESSFULLY = 'Update successfully';
export const CREATE_SUCCESSFULLY = 'Create successfully';
export const UPDATE_FAILED = 'Update failed';
export const CREATE_FAILED = 'Create failed';
export const EXPORT_FAILED = 'Export failed';
export const TOAST_DURATION = 4000;
export const CATEGORIES_URL = '/catalog/categories';
export const BRAND_URL = '/catalog/brands';
export const PRODUCT_ATTRIBUTE_GROUPS_URL = '/catalog/product-attribute-groups';
export const PRODUCT_OPTIONS_URL = '/catalog/product-options';
export const PRODUCT_ATTRIBUTE_URL = '/catalog/product-attributes';
export const PRODUCT_URL = '/catalog/products';

export const DEFAULT_PAGE_SIZE = 10;
export const DEFAULT_PAGE_NUMBER = 0;
export const FORMAT_DATE_YYYY_MM_DD_HH_MM = 'yyyyMMddHHmm';

//Column header to export for product
export const mappingExportingProductColumnNames = {
  id: 'Id',
  name: 'Product Name',
  shortDescription: 'Short Description',
  description: 'Description',
  specification: 'Specification',
  sku: 'SKU',
  gtin: 'GTIN',
  slug: 'Slug',
  isAllowedToOrder: 'Allowed Order',
  isPublished: 'Published',
  isFeatured: 'Featured',
  isVisible: 'Visible',
  stockTrackingEnabled: 'Stock Tracking Enabled',
  price: 'Price',
  brandId: 'Brand Id',
  brandName: 'Brand Name',
  metaTitle: 'Meta Title',
  metaKeyword: 'Meta Keyword',
  metaDescription: 'Meta Description',
};
