import { Warehouse } from '../models/Warehouse';
import { ProductInfoVm } from '../models/ProductInfoVm';
import { WarehouseDetail } from '@inventoryModels/WarehouseDetail';
import apiClientService from '@commonServices/ApiClientService';
import { INVENTORY_BACKOFFICE_WAREHOUSES_ENDPOINT } from '@constants/Endpoints';

const baseUrl = INVENTORY_BACKOFFICE_WAREHOUSES_ENDPOINT;

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
  const url = `${baseUrl}/${warehouseId}/products?productName=${productName}&productSku=${productSku}&existStatus=${existInWHSelection}`;
  return (await apiClientService.get(url)).json();
}

export async function getWarehouses(): Promise<WarehouseDetail[]> {
  return (await apiClientService.get(baseUrl)).json();
}

export async function getPageableWarehouses(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function createWarehouse(warehouseDetail: WarehouseDetail) {
  return apiClientService.post(baseUrl, JSON.stringify(warehouseDetail));
}
export async function getWarehouse(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteWarehouse(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function editWarehouse(id: number, warehouseDetail: WarehouseDetail) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(warehouseDetail));
  if (response.status === 204) return response;
  else return await response.json();
}
