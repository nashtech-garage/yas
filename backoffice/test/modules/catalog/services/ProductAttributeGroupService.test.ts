import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getProductAttributeGroups,
  getPageableProductAttributeGroups,
  getProductAttributeGroup,
  createProductAttributeGroup,
  updateProductAttributeGroup,
  deleteProductAttributeGroup,
} from '../../../../modules/catalog/services/ProductAttributeGroupService';
import { ProductAttributeGroup } from '../../../../modules/catalog/models/ProductAttributeGroup';

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

describe('ProductAttributeGroupService', () => {
  const mockProductAttributeGroup: ProductAttributeGroup = {
    id: 1,
    name: 'Size',
  };

  const mockProductAttributeGroups: ProductAttributeGroup[] = [
    { id: 1, name: 'Size' },
    { id: 2, name: 'Color' },
    { id: 3, name: 'Material' },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getProductAttributeGroups', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributeGroups) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductAttributeGroups();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-groups');
    });

    it('should return product attribute groups data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributeGroups) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductAttributeGroups();

      expect(result).toEqual(mockProductAttributeGroups);
      expect(result).toHaveLength(3);
      expect(result[0].name).toBe('Size');
    });

    it('should handle empty list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductAttributeGroups();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProductAttributeGroups()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableProductAttributeGroups', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockPageableResponse = {
        content: mockProductAttributeGroups,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductAttributeGroups(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-groups/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: mockProductAttributeGroups,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableProductAttributeGroups(pageNo, pageSize);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(3);
      expect(result.totalPages).toBe(5);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductAttributeGroups(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-groups/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductAttributeGroups(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-groups/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductAttributeGroups(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-groups/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableProductAttributeGroups(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableProductAttributeGroups(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('getProductAttributeGroup', () => {
    const groupId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributeGroup) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductAttributeGroup(groupId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-groups/1');
    });

    it('should return product attribute group data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributeGroup) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductAttributeGroup(groupId);

      expect(result).toEqual(mockProductAttributeGroup);
      expect(result.id).toBe(1);
      expect(result.name).toBe('Size');
    });

    it('should handle groupId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductAttributeGroup(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-groups/0');
    });

    it('should handle API error when group not found', async () => {
      const error = new Error('Product attribute group not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProductAttributeGroup(999)).rejects.toThrow('Product attribute group not found');
    });
  });

  describe('createProductAttributeGroup', () => {
    const newGroup: ProductAttributeGroup = {
      id: 0,
      name: 'Weight',
    };


    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 4, name: 'Weight' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductAttributeGroup(newGroup);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating group', async () => {
      const error = new Error('Group name already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductAttributeGroup(newGroup)).rejects.toThrow('Group name already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Group name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductAttributeGroup(newGroup)).rejects.toThrow('Group name is required');
    });
  });

  describe('updateProductAttributeGroup', () => {
    const groupId = 1;
    const updatedGroup: ProductAttributeGroup = {
      id: 1,
      name: 'Updated Size',
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateProductAttributeGroup(groupId, updatedGroup);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-groups/1',
        JSON.stringify(updatedGroup)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductAttributeGroup(groupId, updatedGroup);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductAttributeGroup(groupId, updatedGroup);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when group not found', async () => {
      const error = new Error('Product attribute group not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductAttributeGroup(999, updatedGroup)).rejects.toThrow('Product attribute group not found');
    });

    it('should handle duplicate name error', async () => {
      const error = new Error('Group name already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductAttributeGroup(groupId, updatedGroup)).rejects.toThrow('Group name already exists');
    });
  });

  describe('deleteProductAttributeGroup', () => {
    const groupId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteProductAttributeGroup(groupId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-groups/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProductAttributeGroup(groupId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProductAttributeGroup(groupId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when group not found', async () => {
      const error = new Error('Product attribute group not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProductAttributeGroup(999)).rejects.toThrow('Product attribute group not found');
    });

    it('should handle group with existing attributes', async () => {
      const error = new Error('Cannot delete group with existing attributes');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProductAttributeGroup(1)).rejects.toThrow('Cannot delete group with existing attributes');
    });
  });

});