import { ProductQuantityInStock } from './../models/ProductQuantityInStock';
import { StockPostVM } from '@inventoryModels/Stock';
import apiClientService from '@commonServices/ApiClientService';
import { INVENTORY_BACKOFFICE_STOCKS_ENDPOINT } from '@constants/Endpoints';

const baseUrl = INVENTORY_BACKOFFICE_STOCKS_ENDPOINT;

export async function addProductIntoWarehouse(stocks: StockPostVM[]) {
  return apiClientService.post(baseUrl, JSON.stringify(stocks));
}

export async function fetchStocksInWarehouseByProductNameAndProductSku(
  warehouseId: number,
  productName: string,
  productSku: string
): Promise<Response> {
  const url = `${baseUrl}?warehouseId=${warehouseId}&productName=${productName}&productSku=${productSku}`;
  return apiClientService.get(url);
}

export async function updateProductQuantityInStock(
  productQuantityInStock: ProductQuantityInStock[]
) {
  return apiClientService.put(
    baseUrl,
    JSON.stringify({ stockQuantityList: productQuantityInStock })
  );
}
