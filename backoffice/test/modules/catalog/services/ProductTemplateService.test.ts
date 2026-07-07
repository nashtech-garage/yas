import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getProductTemplates,
  getPageableProductTemplates,
  createProductTemplate,
  getProductTemplate,
  updateProductTemplate,
} from '../../../../modules/catalog/services/ProductTemplateService';
import { ProductTemplate } from '../../../../modules/catalog/models/ProductTemplate';
import { FromProductTemplate } from '../../../../modules/catalog/models/FormProductTemplate';
import { ProductAttributeOfTemplate, ProductAttributeTemplate } from '../../../../modules/catalog/models/FormProductTemplate';

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

describe('ProductTemplateService', () => {
  // Mock ProductAttribute
  const mockProductAttribute = {
    id: 1,
    name: 'Size',
  };

  // Mock ProductAttributeTemplate
  const mockProductAttributeTemplate: ProductAttributeTemplate = {
    displayOrder: 1,
    productAttribute: mockProductAttribute,
  };

  // Mock ProductAttributeOfTemplate cho request
  const mockProductAttributeOfTemplate: ProductAttributeOfTemplate = {
    productAttributeId: 1,
    displayOrder: 1,
  };


  // Mock FromProductTemplate cho request
  const mockFromProductTemplate: FromProductTemplate = {
    name: 'New Template',
    productAttributeTemplates: [mockProductAttributeOfTemplate],
  };

  const mockFromProductTemplateWithoutAttributes: FromProductTemplate = {
    name: 'Simple Template',
  };

  const mockFromProductTemplateUpdate: FromProductTemplate = {
    name: 'Updated Template',
    productAttributeTemplates: [
      { productAttributeId: 1, displayOrder: 1 },
      { productAttributeId: 2, displayOrder: 2 },
    ],
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getProductTemplates', () => {

    it('should handle empty list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductTemplates();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProductTemplates()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableProductTemplates', () => {
    const pageNo = 1;
    const pageSize = 10;


    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductTemplates(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-template/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductTemplates(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-template/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableProductTemplates(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-template/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableProductTemplates(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableProductTemplates(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('createProductTemplate', () => {
    it('should call API with correct URL and body (with attributes)', async () => {
      const mockResponse = { status: 201, data: { id: 4, ...mockFromProductTemplate } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await createProductTemplate(mockFromProductTemplate);

      expect(apiClientService.post).toHaveBeenCalledTimes(1);
      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/product/backoffice/product-template',
        JSON.stringify(mockFromProductTemplate)
      );
    });

    it('should call API with correct URL and body (without attributes)', async () => {
      const mockResponse = { status: 201, data: { id: 4, ...mockFromProductTemplateWithoutAttributes } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await createProductTemplate(mockFromProductTemplateWithoutAttributes);

      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/product/backoffice/product-template',
        JSON.stringify(mockFromProductTemplateWithoutAttributes)
      );
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 4, name: 'New Template' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductTemplate(mockFromProductTemplate);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating template', async () => {
      const error = new Error('Template name already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductTemplate(mockFromProductTemplate)).rejects.toThrow('Template name already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Template name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProductTemplate(mockFromProductTemplate)).rejects.toThrow('Template name is required');
    });
  });

  describe('updateProductTemplate', () => {
    const templateId = 1;

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateProductTemplate(templateId, mockFromProductTemplateUpdate);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/product-template/1',
        JSON.stringify(mockFromProductTemplateUpdate)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductTemplate(templateId, mockFromProductTemplateUpdate);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProductTemplate(templateId, mockFromProductTemplateUpdate);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when template not found', async () => {
      const error = new Error('Product template not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductTemplate(999, mockFromProductTemplateUpdate)).rejects.toThrow('Product template not found');
    });

    it('should handle duplicate name error', async () => {
      const error = new Error('Template name already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProductTemplate(templateId, mockFromProductTemplateUpdate)).rejects.toThrow('Template name already exists');
    });
  });

  describe('Empty FromProductTemplate', () => {
    it('should handle empty FromProductTemplate (only name)', async () => {
      const mockResponse = { status: 201, data: { id: 5, name: 'Minimal Template' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProductTemplate(mockFromProductTemplateWithoutAttributes);

      expect(result).toEqual(mockResponse);
    });

    it('should handle empty name in FromProductTemplate', async () => {
      const emptyNameTemplate: FromProductTemplate = {
        productAttributeTemplates: [mockProductAttributeOfTemplate],
      };
      const mockResponse = { status: 201 };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await createProductTemplate(emptyNameTemplate);

      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/product/backoffice/product-template',
        JSON.stringify(emptyNameTemplate)
      );
    });
  });

});