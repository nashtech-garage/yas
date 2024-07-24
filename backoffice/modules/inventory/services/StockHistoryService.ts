import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/inventory/backoffice/stocks/histories';

export async function getStockHistories(warehouseId: number, productId: number) {
  const url = `${baseUrl}?warehouseId=${warehouseId}&productId=${productId}`;
  return apiClientService.get(url);
}
