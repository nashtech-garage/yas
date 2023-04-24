import { StockHistory } from '@inventoryModels/StockHistory';
import { StockHistoryList } from '@inventoryModels/StockHistoryList';

export async function getStockHistories(
  warehouseId: number,
  productId: number
) {
  const response = await fetch(
    `/api/inventory/backoffice/stocks/histories?warehouseId=${warehouseId}&productId=${productId}`,
    {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    }
  );

  return response;
}
