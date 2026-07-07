import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getStockHistories } from '../../../../modules/inventory/services/StockHistoryService';
import apiClientService from '@commonServices/ApiClientService';

// Mock ApiClientService
vi.mock('@commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('StockHistoryService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getStockHistories', () => {
    it('should call API with correct URL when both warehouseId and productId are provided', async () => {
      const mockResponse = { data: [], status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStockHistories(1, 100);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks/histories?warehouseId=1&productId=100'
      );
    });

    it('should handle warehouseId as zero', async () => {
      const mockResponse = { data: [], status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStockHistories(0, 100);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks/histories?warehouseId=0&productId=100'
      );
    });

    it('should handle productId as zero', async () => {
      const mockResponse = { data: [], status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStockHistories(1, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks/histories?warehouseId=1&productId=0'
      );
    });

    it('should handle negative warehouseId', async () => {
      const mockResponse = { data: [], status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStockHistories(-1, 100);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks/histories?warehouseId=-1&productId=100'
      );
    });

    it('should handle negative productId', async () => {
      const mockResponse = { data: [], status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStockHistories(1, -100);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks/histories?warehouseId=1&productId=-100'
      );
    });

    it('should handle large numbers', async () => {
      const mockResponse = { data: [], status: 200 };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStockHistories(999999, 888888);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/stocks/histories?warehouseId=999999&productId=888888'
      );
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getStockHistories(1, 100)).rejects.toThrow('Network error');
    });

    it('should handle 404 response', async () => {
      const error = new Error('Not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getStockHistories(999, 999)).rejects.toThrow('Not found');
    });

    it('should handle 500 server error', async () => {
      const error = new Error('Internal server error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getStockHistories(1, 100)).rejects.toThrow('Internal server error');
    });
  });

  describe('URL construction', () => {
    it('should construct URL with correct parameters order', async () => {
      const mockResponse = { data: [] };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStockHistories(5, 10);

      const callUrl = (apiClientService.get as any).mock.calls[0][0];
      expect(callUrl).toContain('warehouseId=5');
      expect(callUrl).toContain('productId=10');
      expect(callUrl.indexOf('warehouseId')).toBeLessThan(callUrl.indexOf('productId'));
    });

    it('should use correct base URL', async () => {
      const mockResponse = { data: [] };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStockHistories(1, 100);

      const callUrl = (apiClientService.get as any).mock.calls[0][0];
      expect(callUrl).toContain('/api/inventory/backoffice/stocks/histories');
    });
  });
});