import { ProductQuantityInStock } from './../models/ProductQuantityInStock';
import { StockPostVM } from '@inventoryModels/Stock';

export async function addProductIntoWarehouse(stocks: StockPostVM[]) {
  const response = await fetch('/api/inventory/backoffice/stocks', {
    method: 'POST',
    body: JSON.stringify(stocks),
    headers: { 'Content-Type': 'application/json' },
  });

  return response;
}

export async function fetchStocksInWarehouseByProductNameAndProductSku(
  warehouseId: number,
  productName: string,
  productSku: string
): Promise<Response> {
  const response = await fetch(
    `/api/inventory/backoffice/stocks?warehouseId=${warehouseId}&productName=${productName}&productSku=${productSku}`,
    {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    }
  );

  return response;
}

export async function updateProductQuantityInStock(
  productQuantityInStock: ProductQuantityInStock[]
) {
  const response = await fetch(`/api/inventory/backoffice/stocks`, {
    method: 'PUT',
    body: JSON.stringify({ stockQuantityList: productQuantityInStock }),
    headers: { 'Content-Type': 'application/json' },
  });

  return response;
}
