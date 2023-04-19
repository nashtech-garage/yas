import { Warehouse } from '../models/Warehouse';
import { ProductInfoVm } from '../models/ProductInfoVm';
import { WarehouseDetail } from '@inventoryModels/WarehouseDetail';

export enum FilterExistInWHSelection {
  ALL = 'ALL',
  YES = 'YES',
  NO = 'NO',
}

export async function getProductInWarehouse(
  warehouseId: number,
  productName: string,
  productSku: string,
  existInWHSelection: string
): Promise<ProductInfoVm[]> {
  const response = await fetch(`
    /api/inventory/backoffice/warehouses/${warehouseId}/products?productName=${productName}&productSku=${productSku}&existStatus=${existInWHSelection}`);
  return await response.json();
}

export async function getWarehouses(): Promise<WarehouseDetail[]> {
  const response = await fetch('/api/inventory/backoffice/warehouses');
  return await response.json();
}

export async function getPageableWarehouses(pageNo: number, pageSize: number) {
  const url = `/api/inventory/backoffice/warehouses/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  const response = await fetch(url);
  return await response.json();
}

export async function createWarehouse(warehouseDetail: WarehouseDetail) {
  const response = await fetch('/api/inventory/backoffice/warehouses', {
    method: 'POST',
    body: JSON.stringify(warehouseDetail),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return response;
}
export async function getWarehouse(id: number) {
  const response = await fetch('/api/inventory/backoffice/warehouses/' + id);
  return await response.json();
}

export async function deleteWarehouse(id: number) {
  const response = await fetch(`/api/inventory/backoffice/warehouses/${id}`, {
    method: 'DELETE',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editWarehouse(id: number, warehouseDetail: WarehouseDetail) {
  const response = await fetch(`/api/inventory/backoffice/warehouses/${id}`, {
    method: 'PUT',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(warehouseDetail),
  });
  if (response.status === 204) return response;
  else return await response.json();
}
