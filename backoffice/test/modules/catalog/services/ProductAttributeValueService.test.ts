import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getAttributeValueOfProduct,
  createProductAttributeValueOfProduct,
  updateProductAttributeValueOfProduct,
  deleteProductAttributeValueOfProductById,
} from '../../../../modules/catalog/services/ProductAttributeValueService';
import { ProductAttributeValue } from '../../../../modules/catalog/models/ProductAttributeValue';
import { ProductAttributeValuePost } from '../../../../modules/catalog/models/ProductAttributeValuePost';

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

describe('ProductAttributeValueService', () => {
  // SỬA: ProductAttributeValue có id, nameProductAttribute, value
  const mockProductAttributeValues: ProductAttributeValue[] = [
    {
      id: 1,
      nameProductAttribute: 'Color',
      value: 'Red',
    },
    {
      id: 2,
      nameProductAttribute: 'Size',
      value: 'Large',
    },
    {
      id: 3,
      nameProductAttribute: 'Material',
      value: 'Cotton',
    },
  ];

  const mockProductAttributeValuePost: ProductAttributeValuePost = {
    productId: 100,
    productAttributeId: 1,
    value: 'Blue',
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getAttributeValueOfProduct', () => {
    const productId = 100;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributeValues) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getAttributeValueOfProduct(productId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-value/100');
    });

    it('should return product attribute values on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributeValues) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getAttributeValueOfProduct(productId);

      expect(result).toEqual(mockProductAttributeValues);
      expect(result).toHaveLength(3);
      expect(result[0].nameProductAttribute).toBe('Color');
      expect(result[0].value).toBe('Red');
    });

    it('should handle productId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getAttributeValueOfProduct(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-value/0');
    });

    it('should handle empty attribute values', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getAttributeValueOfProduct(productId);

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getAttributeValueOfProduct(productId)).rejects.toThrow('Network error');
    });

    it('should handle product not found', async () => {
      const error = new Error('Product not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getAttributeValueOfProduct(999)).rejects.toThrow('Product not found');
    });
  });

  describe('createProductAttributeValueOfProduct', () => {
    it('should call API with correct URL and body', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributeValuePost) };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await createProductAttributeValueOfProduct(mockProductAttributeValuePost);

      expect(apiClientService.post).toHaveBeenCalledTimes(1);
      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-value',
        JSON.stringify(mockProductAttributeValuePost)
      );
    });

    it('should return created attribute value on success', async () => {
      const createdValue = { ...mockProductAttributeValuePost, id: 3 };
      const mockResponse = { json: vi.fn().mockResolvedValue(createdValue) };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductAttributeValueOfProduct(mockProductAttributeValuePost);

      expect(result).toEqual(createdValue);
    });

    it('should handle API error when creating attribute value', async () => {
      const error = new Error('Attribute value already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductAttributeValueOfProduct(mockProductAttributeValuePost)).rejects.toThrow(
        'Attribute value already exists'
      );
    });

    it('should handle validation error', async () => {
      const error = new Error('Value is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductAttributeValueOfProduct(mockProductAttributeValuePost)).rejects.toThrow(
        'Value is required'
      );
    });

    it('should handle product not found error', async () => {
      const error = new Error('Product not found');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductAttributeValueOfProduct(mockProductAttributeValuePost)).rejects.toThrow(
        'Product not found'
      );
    });
  });

  describe('updateProductAttributeValueOfProduct', () => {
    const attributeValueId = 1;
    const updatedValue: ProductAttributeValuePost = {
      productId: 100,
      productAttributeId: 1,
      value: 'Updated Value',
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateProductAttributeValueOfProduct(attributeValueId, updatedValue);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-value/1',
        JSON.stringify(updatedValue)
      );
    });

    it('should return status code 204 when update is successful', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductAttributeValueOfProduct(attributeValueId, updatedValue);

      expect(result).toBe(204);
    });

    it('should return response json when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductAttributeValueOfProduct(attributeValueId, updatedValue);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when attribute value not found', async () => {
      const error = new Error('Attribute value not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductAttributeValueOfProduct(999, updatedValue)).rejects.toThrow(
        'Attribute value not found'
      );
    });

    it('should handle duplicate value error', async () => {
      const error = new Error('Duplicate attribute value');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductAttributeValueOfProduct(attributeValueId, updatedValue)).rejects.toThrow(
        'Duplicate attribute value'
      );
    });
  });

  describe('deleteProductAttributeValueOfProductById', () => {
    const attributeValueId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteProductAttributeValueOfProductById(attributeValueId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-value/1');
    });

    it('should return status code 204 when deletion is successful', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProductAttributeValueOfProductById(attributeValueId);

      expect(result).toBe(204);
    });

    it('should return response json when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully', id: 1 }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProductAttributeValueOfProductById(attributeValueId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully', id: 1 });
    });

    it('should handle API error when attribute value not found', async () => {
      const error = new Error('Attribute value not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProductAttributeValueOfProductById(999)).rejects.toThrow('Attribute value not found');
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProductAttributeValueOfProductById(attributeValueId)).rejects.toThrow('Network error');
    });
  });

  describe('URL construction', () => {
    it('should use correct base URL for getAttributeValueOfProduct', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getAttributeValueOfProduct(100);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-value/100');
    });

    it('should use correct base URL for createProductAttributeValueOfProduct', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProductAttributeValuePost) };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await createProductAttributeValueOfProduct(mockProductAttributeValuePost);

      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-value',
        expect.any(String)
      );
    });

    it('should use correct base URL for updateProductAttributeValueOfProduct', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateProductAttributeValueOfProduct(1, mockProductAttributeValuePost);

      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/product-attribute-value/1',
        expect.any(String)
      );
    });

    it('should use correct base URL for deleteProductAttributeValueOfProductById', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteProductAttributeValueOfProductById(1);

      expect(apiClientService.delete).toHaveBeenCalledWith('/api/product/backoffice/product-attribute-value/1');
    });
  });

  describe('Edge cases', () => {
    it('should handle very long product ID', async () => {
      const largeProductId = 999999999;
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getAttributeValueOfProduct(largeProductId);

      expect(apiClientService.get).toHaveBeenCalledWith(
        `/api/product/backoffice/product-attribute-value/${largeProductId}`
      );
    });

    it('should handle value with special characters', async () => {
      const valueWithSpecialChars: ProductAttributeValuePost = {
        productId: 100,
        productAttributeId: 1,
        value: 'Special @#$% Value',
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(valueWithSpecialChars) };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductAttributeValueOfProduct(valueWithSpecialChars);

      expect(result.value).toBe('Special @#$% Value');
    });

    it('should handle very long value string', async () => {
      const longValue = 'A'.repeat(1000);
      const longValuePost: ProductAttributeValuePost = {
        productId: 100,
        productAttributeId: 1,
        value: longValue,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(longValuePost) };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductAttributeValueOfProduct(longValuePost);

      expect(result.value).toBe(longValue);
      expect(result.value.length).toBe(1000);
    });

    it('should handle empty value', async () => {
      const emptyValuePost: ProductAttributeValuePost = {
        productId: 100,
        productAttributeId: 1,
        value: '',
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(emptyValuePost) };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductAttributeValueOfProduct(emptyValuePost);

      expect(result.value).toBe('');
    });
  });
});