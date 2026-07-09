import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getCategories,
  getCategory,
  createCategory,
  updateCategory,
  deleteCategory,
  getProductsByCategory,
} from '../../../../modules/catalog/services/CategoryService';
import { Category } from '../../../../modules/catalog/models/Category';
import { ProductThumbnails } from '../../../../modules/catalog/models/ProductThumbnails';

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

describe('CategoryService', () => {
  const mockCategory: Category = {
    id: 1,
    name: 'Electronics',
    description: 'Electronic products',
    slug: 'electronics',
    parentId: null,
    metaKeywords: 'electronics, gadgets',
    metaDescription: 'Best electronic products',
    displayOrder: 1,
    isPublish: true,
  };

  const mockCategories: Category[] = [
    { id: 1, name: 'Electronics', description: 'Electronic products', slug: 'electronics', parentId: null, metaKeywords: '', metaDescription: '', displayOrder: 1, isPublish: true },
    { id: 2, name: 'Clothing', description: 'Clothing items', slug: 'clothing', parentId: null, metaKeywords: '', metaDescription: '', displayOrder: 2, isPublish: true },
    { id: 3, name: 'Books', description: 'Book items', slug: 'books', parentId: null, metaKeywords: '', metaDescription: '', displayOrder: 3, isPublish: false },
  ];


  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getCategories', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCategories) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCategories();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/categories');
    });

    it('should return categories data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCategories) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getCategories();

      expect(result).toEqual(mockCategories);
      expect(result).toHaveLength(3);
      expect(result[0].name).toBe('Electronics');
    });

    it('should handle empty category list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getCategories();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getCategories()).rejects.toThrow('Network error');
    });
  });

  describe('getCategory', () => {
    const categoryId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCategory) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCategory(categoryId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/categories/1');
    });

    it('should return category data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCategory) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getCategory(categoryId);

      expect(result).toEqual(mockCategory);
      expect(result.id).toBe(1);
      expect(result.name).toBe('Electronics');
    });

    it('should handle categoryId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCategory(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/product/backoffice/categories/0');
    });

    it('should handle API error when category not found', async () => {
      const error = new Error('Category not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getCategory(999)).rejects.toThrow('Category not found');
    });
  });

  describe('createCategory', () => {
    const newCategory: Category = {
      id: 0,
      name: 'New Category',
      description: 'New description',
      slug: 'new-category',
      parentId: null,
      metaKeywords: 'new',
      metaDescription: 'New category',
      displayOrder: 10,
      isPublish: true,
    };

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 4, name: 'New Category' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createCategory(newCategory);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating category', async () => {
      const error = new Error('Category slug already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createCategory(newCategory)).rejects.toThrow('Category slug already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Category name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createCategory(newCategory)).rejects.toThrow('Category name is required');
    });
  });

  describe('updateCategory', () => {
    const categoryId = 1;
    const updatedCategory: Category = {
      id: 1,
      name: 'Updated Electronics',
      description: 'Updated description',
      slug: 'updated-electronics',
      parentId: null,
      metaKeywords: 'updated',
      metaDescription: 'Updated category',
      displayOrder: 1,
      isPublish: true,
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateCategory(categoryId, updatedCategory);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/product/backoffice/categories/1',
        JSON.stringify(updatedCategory)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateCategory(categoryId, updatedCategory);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateCategory(categoryId, updatedCategory);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when category not found', async () => {
      const error = new Error('Category not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateCategory(999, updatedCategory)).rejects.toThrow('Category not found');
    });

    it('should handle duplicate slug error', async () => {
      const error = new Error('Category slug already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateCategory(categoryId, updatedCategory)).rejects.toThrow('Category slug already exists');
    });
  });

  describe('deleteCategory', () => {
    const categoryId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteCategory(categoryId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/product/backoffice/categories/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteCategory(categoryId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteCategory(categoryId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when category not found', async () => {
      const error = new Error('Category not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteCategory(999)).rejects.toThrow('Category not found');
    });

    it('should handle category with existing products', async () => {
      const error = new Error('Cannot delete category with existing products');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteCategory(1)).rejects.toThrow('Cannot delete category with existing products');
    });
  });

});