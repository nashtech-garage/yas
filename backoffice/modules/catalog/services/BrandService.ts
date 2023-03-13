import { Brand } from '../models/Brand';
import { ResponseStatus } from '../../../constants/Common';

export async function getBrands(): Promise<Brand[]> {
  const response = await fetch('/api/product/backoffice/brands');
  return await response.json();
}

export async function createBrand(brand: Brand): Promise<Brand> {
  const response = await fetch('/api/product/backoffice/brands', {
    method: 'POST',
    body: JSON.stringify(brand),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === ResponseStatus.CREATE) return await response;
  else return await response.json();
}
export async function getBrand(id: number) {
  const response = await fetch('/api/product/backoffice/brands/' + id);
  return await response.json();
}

export async function deleteBrand(id: number) {
  const response = await fetch(`/api/product/backoffice/brands/${id}`, {
    method: 'DELETE',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return await response;
  else return await response.json();
}

export async function editBrand(id: number, brand: Brand) {
  const response = await fetch(`/api/product/backoffice/brands/${id}`, {
    method: 'PUT',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(brand),
  });
  if (response.status === 204) return await response;
  else return await response.json();
}
