import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getProductOptions,
  getPageableProductOptions,
  getProductOption,
  createProductOption,
  updateProductOption,
  deleteProductOption,
} from '../../../../modules/catalog/services/ProductOptionService';
import { ProductOption } from '../../../../modules/catalog/models/ProductOption';

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

describe('ProductOptionService', () => {
  const mockProductOption: ProductOption = {
    id: 1,
    name: 'Size',
  };

  const mockProductOptions: ProductOption[] = [
    { id: 1, name: 'Size' },
    { id: 2, name: 'Color' },
    { id: 3, name: 'Material' },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getProductOptions', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductOptions) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductOptions();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-options');
    });

    it('should return product options data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductOptions) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductOptions();

      expect(result).toEqual(mockProductOptions);
      expect(result).toHaveLength(3);
      expect(result[0].name).toBe('Size');
    });

    it('should handle empty list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductOptions();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProductOptions()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableProductOptions', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockPageableResponse = {
        content: mockProductOptions,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductOptions(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-options/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: mockProductOptions,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableProductOptions(pageNo, pageSize);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(3);
      expect(result.totalPages).toBe(5);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductOptions(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-options/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductOptions(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-options/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductOptions(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-options/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableProductOptions(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableProductOptions(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('getProductOption', () => {
    const optionId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductOption) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductOption(optionId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-options/1');
    });

    it('should return product option data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductOption) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductOption(optionId);

      expect(result).toEqual(mockProductOption);
      expect(result.id).toBe(1);
      expect(result.name).toBe('Size');
    });

    it('should handle optionId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductOption(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-options/0');
    });

    it('should handle API error when option not found', async () => {
      const error = new Error('Product option not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProductOption(999)).rejects.toThrow('Product option not found');
    });
  });

  describe('createProductOption', () => {
    const newOption: ProductOption = {
      id: 0,
      name: 'Weight',
    };

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 4, name: 'Weight' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductOption(newOption);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating option', async () => {
      const error = new Error('Option name already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductOption(newOption)).rejects.toThrow('Option name already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Option name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductOption(newOption)).rejects.toThrow('Option name is required');
    });
  });

  describe('updateProductOption', () => {
    const optionId = 1;
    const updatedOption: ProductOption = {
      id: 1,
      name: 'Updated Size',
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateProductOption(optionId, updatedOption);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/product-options/1',
        JSON.stringify(updatedOption)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductOption(optionId, updatedOption);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductOption(optionId, updatedOption);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when option not found', async () => {
      const error = new Error('Product option not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductOption(999, updatedOption)).rejects.toThrow('Product option not found');
    });

    it('should handle duplicate name error', async () => {
      const error = new Error('Option name already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductOption(optionId, updatedOption)).rejects.toThrow('Option name already exists');
    });
  });

  describe('deleteProductOption', () => {
    const optionId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteProductOption(optionId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/product/backoffice/product-options/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProductOption(optionId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProductOption(optionId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when option not found', async () => {
      const error = new Error('Product option not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProductOption(999)).rejects.toThrow('Product option not found');
    });

    it('should handle option with existing values', async () => {
      const error = new Error('Cannot delete option with existing values');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProductOption(1)).rejects.toThrow('Cannot delete option with existing values');
    });
  });
});