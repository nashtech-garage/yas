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
export const TOAST_DURATION = 4000;
export const CATEGORIES_URL = '/catalog/categories';
export const BRAND_URL = '/catalog/brands';
export const PRODUCT_ATTRIBUTE_GROUPS_URL = '/catalog/product-attribute-groups';
export const PRODUCT_OPTIONS_URL = '/catalog/product-options';
export const PRODUCT_ATTRIBUTE_URL = '/catalog/product-attributes';
export const PRODUCT_URL = '/catalog/products';
export const COUNTRY_URL = '/location/countries';
export const STATE_OR_PROVINCE_URL = '/location/state-or-provinces';
export const TAX_CLASS_URL = '/tax/tax-classes';
export const TAX_RATE_URL = '/tax/tax-rates';
export const INVENTORY_WAREHOUSE_PRODUCTS_URL = '/inventory/warehouse-products';
export const WAREHOUSE_URL = '/inventory/warehouses';
export const INVENTORY_WAREHOUSE_STOCKS_URL = '/inventory/warehouse-stocks';
export const INVENTORY_WAREHOUSE_STOCKS_HISTORIES_URL = '/inventory/warehouse-stocks/histories';
export const SALES_ORDERS_URL = '/sales/orders';
export const SALES_SHIPMENTS_URL = '/sales/shipments';
export const SALES_RETURN_REQUESTS_URL = '/sales/return-requests';
export const SALES_RECURRING_PAYMENTS_URL = '/sales/recurring-payments';
export const SALES_GIFT_CARDS_URL = '/sales/gift-cards';
export const SALES_SHOPPING_CARTS_AND_WISHLISTS_URL = '/sales/shopping-carts-and-wishlists';

export const SYSTEM_PAYMENT_PROVIDERS = '/system/payment-providers';
export const SYSTEM_SETTINGS = '/system/settings';

export const DEFAULT_PAGE_SIZE = 10;
export const DEFAULT_PAGE_NUMBER = 0;
export const FORMAT_DATE_YYYY_MM_DD_HH_MM = 'YYYYMMDDHHmmss';

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

//Column header to export for orders
export const mappingExportingOrderColumnNames = {
  id: 'Id',
  email: 'Email',
  totalPrice: 'Total Price',
  shippingAddressContactName: 'Shipping Address ContactName',
  shippingAddressPhone: 'Shipping Address Phone',
  shippingAddressLine1: 'Shipping Address Line1',
  shippingAddressLine2: 'Shipping Address Line2',
  shippingAddressCity: 'Shipping Address City',
  shippingAddressZipCode: 'Shipping Address ZipCode',
  shippingAddressDistrictName: 'Shipping Address DistrictName',
  shippingAddressStateOrProvinceName: 'Shipping Address State Or Province Name',
  shippingAddressCountryName: 'Shipping Address CountryName',
  billingAddressContactName: 'Billing Address ContactName',
  billingAddressPhone: 'Billing Address Phone',
  billingAddressLine1: 'Billing Address Line1',
  billingAddressLine2: 'Billing Address Line2',
  billingAddressCity: 'Billing Address City',
  billingAddressZipCode: 'Billing Address ZipCode',
  billingAddressDistrictName: 'Billing Address DistrictName',
  billingAddressStateOrProvinceName: 'Billing Address State Or Province Name',
  billingAddressCountryName: 'Billing Address CountryName',
  note: 'Note',
  tax: 'Tax',
  discount: 'Discount',
  numberItem: 'Number Item',
  orderStatus: 'Order Status',
  deliveryMethod: 'Delivery Method',
  deliveryStatus: 'Delivery Status',
  paymentStatus: 'Payment Status',
  createdOn: 'Created On',
  deliveryFee: 'Delivery Fee',
  couponCode: 'Coupon Code',
};
