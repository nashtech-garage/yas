export type OrderAddress = {
  id?: number;
  contactName: string;
  phone: string;
  addressLine1: string;
  addressLine2?: string;
  city?: string;
  zipCode?: string;
  districtId: number;
  districtName?: string;
  stateOrProvinceId: number;
  stateOrProvinceName?: string;
  countryId: number;
  countryName?: string;
  isActive?: boolean;
};
