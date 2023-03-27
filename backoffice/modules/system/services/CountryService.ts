import { Country } from '../models/Country';

export async function getCountries(): Promise<Country[]> {
  const response = await fetch('/api/location/backoffice/countries');
  return await response.json();
}

export async function getPageableCountries(pageNo: number, pageSize: number) {
  const url = `/api/location/backoffice/country/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  const response = await fetch(url);
  return await response.json();
}

export async function createCountry(country: Country) {
  const response = await fetch('/api/location/backoffice/country', {
    method: 'POST',
    body: JSON.stringify(country),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return response;
}
export async function getCountry(id: number) {
  const response = await fetch('/api/location/backoffice/country/' + id);
  return await response.json();
}

export async function deleteCountry(id: number) {
  const response = await fetch(`/api/location/backoffice/country/${id}`, {
    method: 'DELETE',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editCountry(id: number, country: Country) {
  const response = await fetch(`/api/location/backoffice/country/${id}`, {
    method: 'PUT',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(country),
  });
  if (response.status === 204) return response;
  else return await response.json();
}
