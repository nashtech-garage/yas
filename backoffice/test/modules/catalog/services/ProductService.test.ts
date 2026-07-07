import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getProducts,
  getLatestProducts,
  exportProducts,
  getProduct,
  createProduct,
  updateProduct,
  deleteProduct,
  getVariationsByProductId,
  getRelatedProductByProductId,
  getProductOptionValueByProductId,
} from '../../../../modules/catalog/services/ProductService';
import { Product } from '../../../../modules/catalog/models/Product';
import { Products } from '../../../../modules/catalog/models/Products';
import { Variantion } from '../../../../modules/catalog/models/ProductVariation';
import { ProductOptionValueDisplayGet } from '../../../../modules/catalog/models/ProductOptionValuePost';

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

describe('ProductService', () => {
  const mockProduct: Product = {
    id: 1,
    name: 'Test Product',
    sku: 'TEST-001',
    slug: 'test-product',
    price: 100,
    description: 'Test description',
    isPublish: true,
  };

  const mockProducts: Products = {
    content: [mockProduct],
    totalPages: 5,
    totalElements: 50,
    pageNo: 1,
    pageSize: 10,
  };

  const mockVariation: Variantion = {
    id: 1,
    sku: 'TEST-001-VAR1',
    price: 100,
    productId: 1,
    productOptionValues: [],
  };

  const mockVariations: Variantion[] = [mockVariation];

  const mockOptionValue: ProductOptionValueDisplayGet = {
    id: 1,
    productId: 1,
    productOptionId: 1,
    value: 'Red',
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getProducts', () => {
    const pageNo = 1;
    const productName = 'test';
    const brandName = 'nike';

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProducts) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProducts(pageNo, productName, brandName);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products?pageNo=1&product-name=test&brand-name=nike'
      );
    });

    it('should return products data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProducts) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProducts(pageNo, productName, brandName);

      expect(result).toEqual(mockProducts);
      expect(result.content).toHaveLength(1);
      expect(result.totalPages).toBe(5);
    });

    it('should handle empty product name', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProducts) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProducts(pageNo, '', brandName);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products?pageNo=1&product-name=&brand-name=nike'
      );
    });

    it('should handle empty brand name', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProducts) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProducts(pageNo, productName, '');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products?pageNo=1&product-name=test&brand-name='
      );
    });

    it('should handle empty response', async () => {
      const emptyResponse = { content: [], totalPages: 0, totalElements: 0 };
      const mockResponse = { json: vi.fn().mockResolvedValue(emptyResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProducts(pageNo, productName, brandName);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProducts(pageNo, productName, brandName)).rejects.toThrow(
        'Network error'
      );
    });
  });

  describe('getLatestProducts', () => {
    const count = 10;

    it('should call API with correct URL', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([mockProduct]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getLatestProducts(count);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products/latest/10'
      );
    });

    it('should return products on success', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([mockProduct]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getLatestProducts(count);

      expect(result).toEqual([mockProduct]);
      expect(result).toHaveLength(1);
    });

    it('should handle count = 0', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getLatestProducts(0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products/latest/0'
      );
    });

    it('should handle large count', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getLatestProducts(100);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products/latest/100'
      );
    });

    it('should reject on non-2xx status', async () => {
      const mockResponse = {
        status: 500,
        statusText: 'Internal Server Error',
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await expect(getLatestProducts(count)).rejects.toThrow('Internal Server Error');
    });
  });

  describe('exportProducts', () => {
    const productName = 'test';
    const brandName = 'nike';

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await exportProducts(productName, brandName);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/export/products?product-name=test&brand-name=nike'
      );
    });

    it('should return exported data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([mockProduct]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await exportProducts(productName, brandName);

      expect(result).toEqual([mockProduct]);
    });

    it('should handle empty product name', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await exportProducts('', brandName);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/export/products?product-name=&brand-name=nike'
      );
    });

    it('should handle API error', async () => {
      const error = new Error('Export failed');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(exportProducts(productName, brandName)).rejects.toThrow('Export failed');
    });
  });

  describe('getProduct', () => {
    const productId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProduct) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProduct(productId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products/1'
      );
    });

    it('should return product data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProduct) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProduct(productId);

      expect(result).toEqual(mockProduct);
      expect(result.id).toBe(1);
      expect(result.name).toBe('Test Product');
      expect(result.sku).toBe('TEST-001');
    });

    it('should handle productId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProduct(0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products/0'
      );
    });

    it('should handle API error when product not found', async () => {
      const error = new Error('Product not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProduct(999)).rejects.toThrow('Product not found');
    });
  });

  describe('createProduct', () => {
    const newProduct = {
      name: 'New Product',
      sku: 'NEW-001',
      price: 200,
    };

    it('should call API with correct URL and body', async () => {
      const mockResponse = { status: 201, data: { id: 2 } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await createProduct(newProduct as any);

      expect(apiClientService.post).toHaveBeenCalledTimes(1);
      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/product/backoffice/products',
        JSON.stringify(newProduct)
      );
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 2 } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createProduct(newProduct as any);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating product', async () => {
      const error = new Error('Product SKU already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProduct(newProduct as any)).rejects.toThrow(
        'Product SKU already exists'
      );
    });

    it('should handle validation error', async () => {
      const error = new Error('Product name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createProduct(newProduct as any)).rejects.toThrow(
        'Product name is required'
      );
    });
  });

  describe('updateProduct', () => {
    const productId = 1;
    const updatedProduct = {
      name: 'Updated Product',
      sku: 'UPDATED-001',
      price: 150,
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateProduct(productId, updatedProduct as any);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/products/1',
        JSON.stringify(updatedProduct)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProduct(productId, updatedProduct as any);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateProduct(productId, updatedProduct as any);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when product not found', async () => {
      const error = new Error('Product not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProduct(999, updatedProduct as any)).rejects.toThrow(
        'Product not found'
      );
    });

    it('should handle duplicate SKU error', async () => {
      const error = new Error('Product SKU already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateProduct(productId, updatedProduct as any)).rejects.toThrow(
        'Product SKU already exists'
      );
    });
  });

  describe('deleteProduct', () => {
    const productId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteProduct(productId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith(
        '/api/product/backoffice/products/1'
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProduct(productId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteProduct(productId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when product not found', async () => {
      const error = new Error('Product not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProduct(999)).rejects.toThrow('Product not found');
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteProduct(productId)).rejects.toThrow('Network error');
    });
  });

  describe('getVariationsByProductId', () => {
    const productId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue(mockVariations),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getVariationsByProductId(productId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-variations/1'
      );
    });

    it('should return variations on success', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue(mockVariations),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getVariationsByProductId(productId);

      expect(result).toEqual(mockVariations);
      expect(result).toHaveLength(1);
    });

    it('should handle productId = 0', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getVariationsByProductId(0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/product-variations/0'
      );
    });

    it('should reject on non-2xx status', async () => {
      const mockResponse = {
        status: 404,
        statusText: 'Not Found',
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await expect(getVariationsByProductId(productId)).rejects.toThrow('Not Found');
    });
  });

  describe('getRelatedProductByProductId', () => {
    const productId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([mockProduct]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getRelatedProductByProductId(productId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products/related-products/1'
      );
    });

    it('should return related products on success', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([mockProduct]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getRelatedProductByProductId(productId);

      expect(result).toEqual([mockProduct]);
    });

    it('should handle empty related products', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getRelatedProductByProductId(productId);

      expect(result).toEqual([]);
    });

    it('should reject on non-2xx status', async () => {
      const mockResponse = {
        status: 500,
        statusText: 'Internal Server Error',
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await expect(getRelatedProductByProductId(productId)).rejects.toThrow(
        'Internal Server Error'
      );
    });
  });

  describe('getProductOptionValueByProductId', () => {
    const productId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([mockOptionValue]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductOptionValueByProductId(productId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/storefront/product-option-values/1'
      );
    });

    it('should return option values on success', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([mockOptionValue]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getProductOptionValueByProductId(productId);

      expect(result).toEqual([mockOptionValue]);
      expect(result).toHaveLength(1);
    });

    it('should handle productId = 0', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue([]),
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductOptionValueByProductId(0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/storefront/product-option-values/0'
      );
    });

    it('should reject on non-2xx status', async () => {
      const mockResponse = {
        status: 404,
        statusText: 'Not Found',
      };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await expect(getProductOptionValueByProductId(productId)).rejects.toThrow(
        'Not Found'
      );
    });
  });
});