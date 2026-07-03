import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getProductAttributes,
  getPageableProductAttributes,
  createProductAttribute,
  updateProductAttribute,
  getProductAttribute,
  deleteProductAttribute,
} from '../../../../modules/catalog/services/ProductAttributeService';
import { ProductAttribute } from '../../../../modules/catalog/models/ProductAttribute';

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

describe('ProductAttributeService', () => {
  // SỬA: ProductAttribute có id, name, productAttributeGroup
  const mockProductAttribute: ProductAttribute = {
    id: 1,
    name: 'Size',
    productAttributeGroup: 'Clothing Attributes',
  };

  const mockProductAttributes: ProductAttribute[] = [
    { id: 1, name: 'Size', productAttributeGroup: 'Clothing Attributes' },
    { id: 2, name: 'Color', productAttributeGroup: 'Clothing Attributes' },
    { id: 3, name: 'Material', productAttributeGroup: 'Furniture Attributes' },
  ];

  const mockProductAttributeId = {
    name: 'New Attribute',
    productAttributeGroupId: '1',
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getProductAttributes', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributes) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductAttributes();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute');
    });

    it('should return product attributes data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributes) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductAttributes();

      expect(result).toEqual(mockProductAttributes);
      expect(result).toHaveLength(3);
      expect(result[0].name).toBe('Size');
      expect(result[0].productAttributeGroup).toBe('Clothing Attributes');
    });

    it('should handle empty list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductAttributes();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProductAttributes()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableProductAttributes', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockPageableResponse = {
        content: mockProductAttributes,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductAttributes(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: mockProductAttributes,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableProductAttributes(pageNo, pageSize);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(3);
      expect(result.totalPages).toBe(5);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductAttributes(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductAttributes(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductAttributes(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableProductAttributes(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableProductAttributes(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('createProductAttribute', () => {
    it('should call API with correct URL and body', async () => {
      const mockResponse = { status: 201, data: { id: 4, ...mockProductAttributeId } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await createProductAttribute(mockProductAttributeId);

      expect(apiClientService.post).toHaveBeenCalledTimes(1);
      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute',
        JSON.stringify(mockProductAttributeId)
      );
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 4, name: 'New Attribute' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductAttribute(mockProductAttributeId);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating attribute', async () => {
      const error = new Error('Attribute name already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductAttribute(mockProductAttributeId)).rejects.toThrow('Attribute name already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Attribute name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductAttribute(mockProductAttributeId)).rejects.toThrow('Attribute name is required');
    });
  });

  describe('getProductAttribute', () => {
    const attributeId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttribute) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductAttribute(attributeId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute/1');
    });

    it('should return product attribute data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttribute) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductAttribute(attributeId);

      expect(result).toEqual(mockProductAttribute);
      expect(result.id).toBe(1);
      expect(result.name).toBe('Size');
      expect(result.productAttributeGroup).toBe('Clothing Attributes');
    });

    it('should handle attributeId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductAttribute(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute/0');
    });

    it('should handle API error when attribute not found', async () => {
      const error = new Error('Product attribute not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProductAttribute(999)).rejects.toThrow('Product attribute not found');
    });
  });

  describe('updateProductAttribute', () => {
    const attributeId = 1;
    const updatedAttribute = {
      name: 'Updated Size',
      productAttributeGroupId: '2',
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateProductAttribute(attributeId, updatedAttribute);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute/1',
        JSON.stringify(updatedAttribute)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductAttribute(attributeId, updatedAttribute);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductAttribute(attributeId, updatedAttribute);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when attribute not found', async () => {
      const error = new Error('Product attribute not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductAttribute(999, updatedAttribute)).rejects.toThrow('Product attribute not found');
    });

    it('should handle duplicate name error', async () => {
      const error = new Error('Attribute name already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductAttribute(attributeId, updatedAttribute)).rejects.toThrow('Attribute name already exists');
    });
  });

  describe('deleteProductAttribute', () => {
    const attributeId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteProductAttribute(attributeId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/product/backoffice/product-attribute/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProductAttribute(attributeId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProductAttribute(attributeId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when attribute not found', async () => {
      const error = new Error('Product attribute not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProductAttribute(999)).rejects.toThrow('Product attribute not found');
    });

    it('should handle attribute with existing values', async () => {
      const error = new Error('Cannot delete attribute with existing values');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProductAttribute(1)).rejects.toThrow('Cannot delete attribute with existing values');
    });
  });

});