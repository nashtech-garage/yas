import { TaxClass } from '../models/TaxClass';

export async function getTaxClasses(): Promise<TaxClass[]> {
  const response = await fetch('/api/tax/backoffice/tax-classes');
  return await response.json();
}

export async function getPageableTaxClasses(pageNo: number, pageSize: number) {
  const url = `/api/tax/backoffice/tax-classes/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  const response = await fetch(url);
  return await response.json();
}

export async function createTaxClass(taxClass: TaxClass) {
  const response = await fetch('/api/tax/backoffice/tax-classes', {
    method: 'POST',
    body: JSON.stringify(taxClass),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return response;
}
export async function getTaxClass(id: number) {
  const response = await fetch('/api/tax/backoffice/tax-classes/' + id);
  return await response.json();
}

export async function deleteTaxClass(id: number) {
  const response = await fetch(`/api/tax/backoffice/tax-classes/${id}`, {
    method: 'DELETE',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editTaxClass(id: number, taxClass: TaxClass) {
  const response = await fetch(`/api/tax/backoffice/tax-classes/${id}`, {
    method: 'PUT',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(taxClass),
  });
  if (response.status === 204) return response;
  else return await response.json();
}
