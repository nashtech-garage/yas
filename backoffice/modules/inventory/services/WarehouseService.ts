import { Warehouse } from '../models/Warehouse';
import { ProductInfoVm } from '../models/ProductInfoVm';

export enum FilterExistInWHSelection {
  ALL = 'ALL',
  YES = 'YES',
  NO = 'NO',
}

export async function getWarehouses(): Promise<Warehouse[]> {
  const response = await fetch('/api/inventory/backoffice/warehouses');
  return await response.json();
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
