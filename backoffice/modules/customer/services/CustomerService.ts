import { Customers } from "../models/Customers";

export async function getCustomers(pageNo: number): Promise<Customers> {
  const response = await fetch(
    `/api/customer/backoffice/customers/?pageNo=${pageNo}`
  );
  return response.json();
}
