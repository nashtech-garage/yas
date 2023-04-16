import { StockPostVM } from '@inventoryModels/Stock';

export async function addProductIntoWarehouse(stocks: StockPostVM[]) {
  const response = await fetch('/api/inventory/backoffice/stocks', {
    method: 'POST',
    body: JSON.stringify(stocks),
    headers: { 'Content-Type': 'application/json' },
  });

  return response;
}
