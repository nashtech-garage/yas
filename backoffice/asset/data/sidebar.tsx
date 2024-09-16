import {
  COUNTRY_URL,
  INVENTORY_WAREHOUSE_PRODUCTS_URL,
  INVENTORY_WAREHOUSE_STOCKS_URL,
  MANAGER_PROMOTIONS_URL,
  SALES_GIFT_CARDS_URL,
  SALES_ORDERS_URL,
  SALES_RECURRING_PAYMENTS_URL,
  SALES_RETURN_REQUESTS_URL,
  SALES_SHIPMENTS_URL,
  SALES_SHOPPING_CARTS_AND_WISHLISTS_URL,
  STATE_OR_PROVINCE_URL,
  SYSTEM_PAYMENT_PROVIDERS,
  SYSTEM_SETTINGS,
  TAX_CLASS_URL,
  TAX_RATE_URL,
  WAREHOUSE_URL,
  WEBHOOKS_URL,
} from '@constants/Common';

export const menu_catalog_item_data = [
  {
    id: 1,
    name: 'Brands',
    link: '/catalog/brands',
  },
  {
    id: 2,
    name: 'Categories',
    link: '/catalog/categories',
  },
  {
    id: 3,
    name: 'Products',
    link: '/catalog/products',
  },
  {
    id: 4,
    name: 'Product Options',
    link: '/catalog/product-options',
  },
  {
    id: 5,
    name: 'Product Attributes',
    link: '/catalog/product-attributes',
  },
  {
    id: 6,
    name: 'Product Attribute Groups',
    link: '/catalog/product-attribute-groups',
  },
  {
    id: 7,
    name: 'Product Templates',
    link: '/catalog/product-templates',
  },
];

export const menu_customer_item_data = [
  {
    id: 1,
    name: 'Customers',
    link: '/customers',
  },
  {
    id: 2,
    name: 'Product Reviews',
    link: '/reviews',
  },
];

export const menu_location_item_data = [
  {
    id: 1,
    name: 'Countries',
    link: COUNTRY_URL,
  },
  {
    id: 2,
    name: 'State Or Provinces',
    link: STATE_OR_PROVINCE_URL,
  },
];

export const menu_tax_item_data = [
  {
    id: 1,
    name: 'Tax Class',
    link: TAX_CLASS_URL,
  },
  {
    id: 2,
    name: 'Tax Rate',
    link: TAX_RATE_URL,
  },
];

export const menu_inventory_item_data = [
  {
    id: 1,
    name: 'Manage Warehouse Products',
    link: INVENTORY_WAREHOUSE_PRODUCTS_URL,
  },
  {
    id: 2,
    name: 'Manage Warehouses',
    link: WAREHOUSE_URL,
  },
  {
    id: 3,
    name: 'Manage Warehouse Stocks',
    link: INVENTORY_WAREHOUSE_STOCKS_URL,
  },
];

export const menu_sale_item_data = [
  {
    id: 1,
    name: 'Orders',
    link: SALES_ORDERS_URL,
  },
  {
    id: 2,
    name: 'Shipments',
    link: SALES_SHIPMENTS_URL,
  },
  {
    id: 3,
    name: 'Return requests',
    link: SALES_RETURN_REQUESTS_URL,
  },
  {
    id: 4,
    name: 'Recurring payments',
    link: SALES_RECURRING_PAYMENTS_URL,
  },
  {
    id: 5,
    name: 'Gift cards',
    link: SALES_GIFT_CARDS_URL,
  },
  {
    id: 6,
    name: 'Shopping carts and wishlists',
    link: SALES_SHOPPING_CARTS_AND_WISHLISTS_URL,
  },
];

export const menu_system_item_data = [
  {
    id: 1,
    name: 'Payment providers',
    link: SYSTEM_PAYMENT_PROVIDERS,
  },
  {
    id: 2,
    name: 'Settings',
    link: SYSTEM_SETTINGS,
  },
  {
    id: 3,
    name: 'Webhooks',
    link: WEBHOOKS_URL,
  },
];

export const menu_promotion_item_data = [
  {
    id: 1,
    name: 'Manager Promotions',
    link: MANAGER_PROMOTIONS_URL,
  },
];
