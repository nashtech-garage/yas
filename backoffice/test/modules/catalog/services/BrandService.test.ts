import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getBrands,
  getPageableBrands,
  createBrand,
  getBrand,
  deleteBrand,
  editBrand,
} from '../../../../modules/catalog/services/BrandService';
import { Brand } from '../../../../modules/catalog/models/Brand';

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

describe('BrandService', () => {
  const mockBrand: Brand = {
    id: 1,
    name: 'Nike',
    slug: 'nike',
    isPublish: true,
  };

  const mockBrands: Brand[] = [
    { id: 1, name: 'Nike', slug: 'nike', isPublish: true },
    { id: 2, name: 'Adidas', slug: 'adidas', isPublish: true },
    { id: 3, name: 'Puma', slug: 'puma', isPublish: false },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getBrands', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockBrands) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getBrands();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/brands');
    });

    it('should return brands data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockBrands) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getBrands();

      expect(result).toEqual(mockBrands);
      expect(result).toHaveLength(3);
      expect(result[0].name).toBe('Nike');
    });

    it('should handle empty brand list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getBrands();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getBrands()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableBrands', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockPageableResponse = {
        content: mockBrands,
        totalPages: 5,
        totalElements: 50,
        pageable: { pageNumber: 0, pageSize: 10 },
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableBrands(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/brands/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: mockBrands,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableBrands(pageNo, pageSize);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(3);
      expect(result.totalPages).toBe(5);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableBrands(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/brands/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableBrands(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/brands/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableBrands(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/brands/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableBrands(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableBrands(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('createBrand', () => {
    const newBrand: Brand = {
      id: 0,
      name: 'New Balance',
      slug: 'new-balance',
      isPublish: true,
    };

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 4, name: 'New Balance' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createBrand(newBrand);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating brand', async () => {
      const error = new Error('Brand name already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createBrand(newBrand)).rejects.toThrow('Brand name already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Brand name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createBrand(newBrand)).rejects.toThrow('Brand name is required');
    });
  });

  describe('getBrand', () => {
    const brandId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockBrand) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getBrand(brandId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/brands/1');
    });

    it('should return brand data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockBrand) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getBrand(brandId);

      expect(result).toEqual(mockBrand);
      expect(result.id).toBe(1);
      expect(result.name).toBe('Nike');
    });

    it('should handle brandId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getBrand(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/brands/0');
    });

    it('should handle API error when brand not found', async () => {
      const error = new Error('Brand not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getBrand(999)).rejects.toThrow('Brand not found');
    });
  });

  describe('deleteBrand', () => {
    const brandId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteBrand(brandId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/product/backoffice/brands/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteBrand(brandId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteBrand(brandId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when brand not found', async () => {
      const error = new Error('Brand not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteBrand(999)).rejects.toThrow('Brand not found');
    });

    it('should handle brand with existing products', async () => {
      const error = new Error('Cannot delete brand with existing products');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteBrand(1)).rejects.toThrow('Cannot delete brand with existing products');
    });
  });

  describe('editBrand', () => {
    const brandId = 1;
    const updatedBrand: Brand = {
      id: 1,
      name: 'Nike Updated',
      slug: 'nike-updated',
      isPublish: true,
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await editBrand(brandId, updatedBrand);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/brands/1',
        JSON.stringify(updatedBrand)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editBrand(brandId, updatedBrand);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editBrand(brandId, updatedBrand);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when brand not found', async () => {
      const error = new Error('Brand not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editBrand(999, updatedBrand)).rejects.toThrow('Brand not found');
    });

    it('should handle duplicate brand name error', async () => {
      const error = new Error('Brand name already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editBrand(brandId, updatedBrand)).rejects.toThrow('Brand name already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Brand slug is required');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editBrand(brandId, updatedBrand)).rejects.toThrow('Brand slug is required');
    });
  });


});