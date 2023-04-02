export type Address = {
    id?: number,
    contactName: string,
    phone: string,
    addressLine1: string,
    city: string,
    zipCode?: string,
    districtId: number,
    stateOrProvinceId: number,
    countryId: number,
}