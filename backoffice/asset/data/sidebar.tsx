import { COUNTRY_URL, STATE_OR_PROVINCE_URL, TAX_CLASS_URL, TAX_RATE_URL } from '@constants/Common';

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
