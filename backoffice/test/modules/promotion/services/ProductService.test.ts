import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  searchProducts,
  searchCategories,
  searchBrands,
} from '../../../../modules/promotion/services/ProductService';
import { Product } from '../../../../modules/catalog/models/Product';
import { Category } from '../../../../modules/catalog/models/Category';
import { Brand } from '../../../../modules/catalog/models/Brand';
import { Media } from '../../../../modules/catalog/models/Media';

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

describe('SearchService', () => {
  // Mock Category với đầy đủ fields
  const mockCategory: Category = {
    id: 1,
    name: 'Electronics',
    description: 'Electronic products and gadgets',
    slug: 'electronics',
    parentId: null,
    metaKeywords: 'electronics, gadgets, devices',
    metaDescription: 'Best electronic products',
    displayOrder: 1,
    isPublish: true,
    imageId: 1,
    categoryImage: {
      id: 1,
      url: '/uploads/category.jpg',
    },
  };

  // Mock Media với đầy đủ fields
  const mockMedia: Media = {
    id: 1,
    url: '/uploads/product.jpg',
  };

  // Mock Product với đầy đủ fields
  const mockProducts: Product[] = [
    {
      id: 1,
      name: 'Laptop',
      shortDescription: 'High performance laptop',
      description: 'Full description of laptop',
      specification: '16GB RAM, 512GB SSD',
      sku: 'SKU-001',
      gtin: 'GTIN-001',
      slug: 'laptop',
      weight: 2.5,
      dimensionUnit: 'kg',
      length: 35,
      width: 25,
      height: 2,
      price: 999.99,
      metaTitle: 'Laptop Title',
      metaKeyword: 'laptop,computer',
      metaDescription: 'Meta for laptop',
      isAllowedToOrder: true,
      isPublished: true,
      isFeatured: true,
      isVisible: true,
      stockTrackingEnabled: true,
      taxIncluded: true,
      brandId: 1,
      categories: [mockCategory],
      thumbnailMedia: mockMedia,
      productImageMedias: [mockMedia],
      createdOn: new Date(),
      taxClassId: 1,
      parentId: 0,
    },
    {
      id: 2,
      name: 'Mouse',
      shortDescription: 'Wireless mouse',
      description: 'Full description of mouse',
      specification: 'Wireless, 3 buttons',
      sku: 'SKU-002',
      gtin: 'GTIN-002',
      slug: 'mouse',
      weight: 0.2,
      dimensionUnit: 'kg',
      length: 10,
      width: 6,
      height: 3,
      price: 29.99,
      metaTitle: 'Mouse Title',
      metaKeyword: 'mouse,wireless',
      metaDescription: 'Meta for mouse',
      isAllowedToOrder: true,
      isPublished: true,
      isFeatured: false,
      isVisible: true,
      stockTrackingEnabled: true,
      taxIncluded: true,
      brandId: 2,
      categories: [mockCategory],
      thumbnailMedia: mockMedia,
      productImageMedias: [mockMedia],
      createdOn: new Date(),
      taxClassId: 1,
      parentId: 0,
    },
  ];

  // Mock Categories với đầy đủ fields
  const mockCategories: Category[] = [
    {
      id: 1,
      name: 'Electronics',
      description: 'Electronic products',
      slug: 'electronics',
      parentId: null,
      metaKeywords: 'electronics',
      metaDescription: 'Electronics category',
      displayOrder: 1,
      isPublish: true,
    },
    {
      id: 2,
      name: 'Clothing',
      description: 'Clothing items',
      slug: 'clothing',
      parentId: null,
      metaKeywords: 'clothing',
      metaDescription: 'Clothing category',
      displayOrder: 2,
      isPublish: true,
    },
  ];

  // Mock Brands
  const mockBrands: Brand[] = [
    { id: 1, name: 'Nike', slug: 'nike', isPublish: true },
    { id: 2, name: 'Adidas', slug: 'adidas', isPublish: true },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('searchProducts', () => {
    const searchTerm = 'laptop';

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProducts) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchProducts(searchTerm);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products?product-name=laptop'
      );
    });

    it('should return products on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProducts) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await searchProducts(searchTerm);

      expect(result).toEqual(mockProducts);
      expect(result).toHaveLength(2);
      expect(result[0].name).toBe('Laptop');
      expect(result[0].price).toBe(999.99);
      expect(result[0].sku).toBe('SKU-001');
      expect(result[0].categories).toHaveLength(1);
      expect(result[0].categories[0].name).toBe('Electronics');
    });

    it('should handle empty search term', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchProducts('');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products?product-name='
      );
    });

    it('should handle search term with spaces', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchProducts('gaming laptop');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products?product-name=gaming laptop'
      );
    });

    it('should handle search term with special characters', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchProducts('laptop@#$%');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products?product-name=laptop@#$%'
      );
    });

    it('should return empty array when no products found', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await searchProducts('nonexistent');

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(searchProducts(searchTerm)).rejects.toThrow('Network error');
    });
  });

  describe('searchCategories', () => {
    const searchTerm = 'electronics';

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCategories) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchCategories(searchTerm);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/categories?categoryName=electronics'
      );
    });

    it('should return categories on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCategories) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await searchCategories(searchTerm);

      expect(result).toEqual(mockCategories);
      expect(result).toHaveLength(2);
      expect(result[0].name).toBe('Electronics');
      expect(result[0].description).toBe('Electronic products');
      expect(result[0].slug).toBe('electronics');
    });

    it('should handle empty search term', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchCategories('');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/categories?categoryName='
      );
    });

    it('should handle search term with spaces', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchCategories('home appliance');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/categories?categoryName=home appliance'
      );
    });

    it('should return empty array when no categories found', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await searchCategories('nonexistent');

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(searchCategories(searchTerm)).rejects.toThrow('Network error');
    });
  });

  describe('searchBrands', () => {
    const searchTerm = 'nike';

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockBrands) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchBrands(searchTerm);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/brands?brandName=nike'
      );
    });

    it('should return brands on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockBrands) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await searchBrands(searchTerm);

      expect(result).toEqual(mockBrands);
      expect(result).toHaveLength(2);
      expect(result[0].name).toBe('Nike');
    });

    it('should handle empty search term', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchBrands('');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/brands?brandName='
      );
    });

    it('should handle search term with spaces', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchBrands('new balance');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/brands?brandName=new balance'
      );
    });

    it('should handle search term with special characters', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchBrands('nike@#$%');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/brands?brandName=nike@#$%'
      );
    });

    it('should return empty array when no brands found', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await searchBrands('nonexistent');

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(searchBrands(searchTerm)).rejects.toThrow('Network error');
    });
  });

  describe('URL construction', () => {
    it('should use correct base URLs', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchProducts('test');
      await searchCategories('test');
      await searchBrands('test');

      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/products?product-name=test');
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/categories?categoryName=test');
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/brands?brandName=test');
    });
  });

  describe('Edge cases', () => {
    it('should handle very long search term', async () => {
      const longSearchTerm = 'a'.repeat(500);
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchProducts(longSearchTerm);

      expect(apiClientService.get).toHaveBeenCalledWith(
        `/api/product/backoffice/products?product-name=${longSearchTerm}`
      );
    });

    it('should handle search term with unicode characters', async () => {
      const unicodeSearchTerm = 'café';
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchProducts(unicodeSearchTerm);

      expect(apiClientService.get).toHaveBeenCalledWith(
        `/api/product/backoffice/products?product-name=café`
      );
    });

    it('should handle case sensitivity in search term', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await searchProducts('LAPTOP');

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/product/backoffice/products?product-name=LAPTOP'
      );
    });
  });
});