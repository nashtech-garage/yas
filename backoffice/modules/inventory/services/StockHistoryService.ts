import apiClientService from '@commonServices/ApiClientService';
import { INVENTORY_BACKOFFICE_STOCKS_HISTORIES_ENDPOINT } from '@constants/Endpoints';

const baseUrl = INVENTORY_BACKOFFICE_STOCKS_HISTORIES_ENDPOINT;

export async function getStockHistories(warehouseId: number, productId: number) {
  const url = `${baseUrl}?warehouseId=${warehouseId}&productId=${productId}`;
  return apiClientService.get(url);
}
