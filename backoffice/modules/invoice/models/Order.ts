import { Address } from "./Address";

export type Order = {
    id: number;
    email: string;
    tax: number;
    totalPrice: number;
    note: string;
    billingAddressVm: Address;
    shippingAddressVm: Address;
    orderStatus: string;
    paymentStatus: string;
}