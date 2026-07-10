import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  addProductIntoWarehouse,
  fetchStocksInWarehouseByProductNameAndProductSku,
  updateProductQuantityInStock,
} from '../../../../modules/inventory/services/StockService';
import { StockPostVM } from '../../../../modules/inventory/models/Stock';
import { ProductQuantityInStock } from '../../../../modules/inventory/models/ProductQuantityInStock';

// Mock ApiClientService
vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import apiClientService from '../../../../common/services/ApiClientService';

describe('StockService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });


  describe('fetchStocksInWarehouseByProductNameAndProductSku', () => {
    const warehouseId = 1;
    const productName = 'Laptop';
    const productSku = 'SKU-123';

    it('should call API with correct URL with all parameters', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]), status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await fetchStocksInWarehouseByProductNameAndProductSku(warehouseId, productName, productSku);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks?warehouseId=1&productName=Laptop&productSku=SKU-123'
      );
    });

    it('should return response on success', async () => {
      const mockStocks = [
        {
          productId: 1,
          productName: 'Laptop',
          productSku: 'SKU-123',
          quantity: 50,
          warehouseId: 1,
        },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockStocks), status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await fetchStocksInWarehouseByProductNameAndProductSku(
        warehouseId,
        productName,
        productSku
      );

      expect(result).toEqual(mockResponse);
    });

    it('should handle empty product name', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]), status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await fetchStocksInWarehouseByProductNameAndProductSku(warehouseId, '', productSku);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks?warehouseId=1&productName=&productSku=SKU-123'
      );
    });

    it('should handle empty product SKU', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]), status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await fetchStocksInWarehouseByProductNameAndProductSku(warehouseId, productName, '');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks?warehouseId=1&productName=Laptop&productSku='
      );
    });

    it('should handle API error when fetching stocks', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(
        fetchStocksInWarehouseByProductNameAndProductSku(warehouseId, productName, productSku)
      ).rejects.toThrow('Network error');
    });
  });

  describe('updateProductQuantityInStock', () => {
    // SỬA: Dùng đúng type ProductQuantityInStock với stockId, quantity, note
    const mockStockQuantities: ProductQuantityInStock[] = [
      {
        stockId: 1,
        quantity: 200,
        note: 'Increase stock for Lunar New Year',
      },
      {
        stockId: 2,
        quantity: 150,
        note: 'Restock after inventory count',
      },
    ];

    it('should call API with correct URL and body', async () => {
      const mockResponse = { status: 200, data: { message: 'Updated successfully' } };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(mockStockQuantities);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks',
        JSON.stringify({ stockQuantityList: mockStockQuantities })
      );
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 200, data: { message: 'Stock updated' } };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductQuantityInStock(mockStockQuantities);

      expect(result).toEqual(mockResponse);
    });

    it('should handle empty array', async () => {
      const mockResponse = { status: 200, data: { message: 'No updates' } };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock([]);

      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks',
        JSON.stringify({ stockQuantityList: [] })
      );
    });

    it('should handle single stock update', async () => {
      const singleUpdate: ProductQuantityInStock[] = [
        {
          stockId: 1,
          quantity: 50,
          note: 'Adjustment for damaged goods',
        },
      ];
      const mockResponse = { status: 200 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(singleUpdate);

      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks',
        JSON.stringify({ stockQuantityList: singleUpdate })
      );
    });

    it('should handle quantity as zero', async () => {
      const zeroQuantityUpdate: ProductQuantityInStock[] = [
        {
          stockId: 1,
          quantity: 0,
          note: 'Out of stock - discontinued',
        },
      ];
      const mockResponse = { status: 200 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(zeroQuantityUpdate);

      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks',
        JSON.stringify({ stockQuantityList: zeroQuantityUpdate })
      );
    });

    it('should handle negative quantity (returns/defects)', async () => {
      const negativeQuantityUpdate: ProductQuantityInStock[] = [
        {
          stockId: 1,
          quantity: -10,
          note: 'Returned defective items',
        },
      ];
      const mockResponse = { status: 200 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(negativeQuantityUpdate);

      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks',
        JSON.stringify({ stockQuantityList: negativeQuantityUpdate })
      );
    });

    it('should handle empty note string', async () => {
      const emptyNoteUpdate: ProductQuantityInStock[] = [
        {
          stockId: 1,
          quantity: 100,
          note: '',
        },
      ];
      const mockResponse = { status: 200 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(emptyNoteUpdate);

      const callBody = (apiClientService.put as any).mock.calls[0][1];
      const parsedBody = JSON.parse(callBody);
      
      expect(parsedBody.stockQuantityList[0].note).toBe('');
    });

    it('should handle long note text', async () => {
      const longNote = 'A'.repeat(500);
      const longNoteUpdate: ProductQuantityInStock[] = [
        {
          stockId: 1,
          quantity: 100,
          note: longNote,
        },
      ];
      const mockResponse = { status: 200 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(longNoteUpdate);

      const callBody = (apiClientService.put as any).mock.calls[0][1];
      const parsedBody = JSON.parse(callBody);
      
      expect(parsedBody.stockQuantityList[0].note).toBe(longNote);
      expect(parsedBody.stockQuantityList[0].note.length).toBe(500);
    });

    it('should handle API error when updating stock', async () => {
      const error = new Error('Insufficient stock');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductQuantityInStock(mockStockQuantities)).rejects.toThrow('Insufficient stock');
    });

    it('should handle 409 Conflict error', async () => {
      const error = new Error('Stock already reserved');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductQuantityInStock(mockStockQuantities)).rejects.toThrow('Stock already reserved');
    });

    it('should handle 404 Not Found error', async () => {
      const error = new Error('Stock record not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductQuantityInStock(mockStockQuantities)).rejects.toThrow('Stock record not found');
    });

    it('should preserve note field when updating', async () => {
      const updateWithNote: ProductQuantityInStock[] = [
        {
          stockId: 1,
          quantity: 75,
          note: 'Monthly stock adjustment',
        },
      ];
      const mockResponse = { status: 200 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(updateWithNote);

      const callBody = (apiClientService.put as any).mock.calls[0][1];
      const parsedBody = JSON.parse(callBody);
      
      expect(parsedBody.stockQuantityList[0]).toEqual({
        stockId: 1,
        quantity: 75,
        note: 'Monthly stock adjustment',
      });
    });
  });

  describe('Request body format', () => {
    it('should format update request body correctly with stockQuantityList wrapper', async () => {
      const updateData: ProductQuantityInStock[] = [
        { stockId: 1, quantity: 100, note: 'First item' },
        { stockId: 2, quantity: 200, note: 'Second item' },
      ];
      const mockResponse = { status: 200 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(updateData);

      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks',
        JSON.stringify({ stockQuantityList: updateData })
      );
    });

    it('should stringify the request body', async () => {
      const updateData: ProductQuantityInStock[] = [
        { stockId: 1, quantity: 50, note: 'Test note' },
      ];
      const mockResponse = { status: 200 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updateProductQuantityInStock(updateData);

      const callArg = (apiClientService.put as any).mock.calls[0][1];
      expect(typeof callArg).toBe('string');
      expect(() => JSON.parse(callArg)).not.toThrow();
    });
  });
});