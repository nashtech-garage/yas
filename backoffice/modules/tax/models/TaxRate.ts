export type TaxRate = {
  id: number;
  rate: number;
  zipCode: string;
  taxClassName: string;
  taxClassId: number;
  countryId: number;
  countryName: string;
  stateOrProvinceId: number;
  stateOrProvinceName: string;
};
