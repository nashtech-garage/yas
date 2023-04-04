import { TaxRate } from '../models/TaxRate';

export async function getTaxRates(): Promise<TaxRate[]> {
  const response = await fetch('/api/tax/backoffice/tax-rates');
  return await response.json();
}

export async function getPageableTaxRates(pageNo: number, pageSize: number) {
  const url = `/api/tax/backoffice/tax-rates/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  const response = await fetch(url);
  return await response.json();
}

export async function createTaxRate(taxRate: TaxRate) {
  const response = await fetch('/api/tax/backoffice/tax-rates', {
    method: 'POST',
    body: JSON.stringify(taxRate),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return response;
}
export async function getTaxRate(id: number) {
  const response = await fetch('/api/tax/backoffice/tax-rates/' + id);
  return await response.json();
}

export async function deleteTaxRate(id: number) {
  const response = await fetch(`/api/tax/backoffice/tax-rates/${id}`, {
    method: 'DELETE',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editTaxRate(id: number, taxRate: TaxRate) {
  const response = await fetch(`/api/tax/backoffice/tax-rates/${id}`, {
    method: 'PUT',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(taxRate),
  });
  if (response.status === 204) return response;
  else return await response.json();
}
