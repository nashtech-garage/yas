import { Country } from "../models/Country";

export async function getCountries(): Promise<Country[]> {
  const response = await fetch(`/api/location/storefront/countries`);
  return await response.json();
}
