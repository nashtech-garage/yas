import { describe, it, expect } from 'vitest';
import type { Category } from '../../../../modules/catalog/models/Category';

describe('Category Type', () => {
  describe('Type structure', () => {
    it('should have all required properties', () => {
      const validCategory: Category = {
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

      expect(validCategory).toHaveProperty('id');
      expect(validCategory).toHaveProperty('name');
      expect(validCategory).toHaveProperty('description');
      expect(validCategory).toHaveProperty('slug');
      expect(validCategory).toHaveProperty('parentId');
      expect(validCategory).toHaveProperty('metaKeywords');
      expect(validCategory).toHaveProperty('metaDescription');
      expect(validCategory).toHaveProperty('displayOrder');
      expect(validCategory).toHaveProperty('isPublish');
    });

    it('should have correct property types', () => {
      const category: Category = {
        id: 1,
        name: 'Electronics',
        description: 'Electronic products',
        slug: 'electronics',
        parentId: 5,
        metaKeywords: 'electronics',
        metaDescription: 'Best electronics',
        displayOrder: 10,
        isPublish: true,
      };

      expect(typeof category.id).toBe('number');
      expect(typeof category.name).toBe('string');
      expect(typeof category.description).toBe('string');
      expect(typeof category.slug).toBe('string');
      expect(category.parentId === null || typeof category.parentId === 'number').toBe(true);
      expect(typeof category.metaKeywords).toBe('string');
      expect(typeof category.metaDescription).toBe('string');
      expect(typeof category.displayOrder).toBe('number');
      expect(typeof category.isPublish).toBe('boolean');
    });

    it('should accept parentId as null', () => {
      const category: Category = {
        id: 1,
        name: 'Root Category',
        description: 'Root category',
        slug: 'root',
        parentId: null,
        metaKeywords: 'root',
        metaDescription: 'Root category',
        displayOrder: 0,
        isPublish: true,
      };

      expect(category.parentId).toBeNull();
    });

    it('should accept parentId as number', () => {
      const category: Category = {
        id: 2,
        name: 'Sub Category',
        description: 'Child category',
        slug: 'sub',
        parentId: 1,
        metaKeywords: 'sub',
        metaDescription: 'Sub category',
        displayOrder: 1,
        isPublish: true,
      };

      expect(category.parentId).toBe(1);
      expect(typeof category.parentId).toBe('number');
    });
  });

  describe('Optional properties', () => {
    it('should accept imageId as optional property', () => {
      const categoryWithoutImage: Category = {
        id: 1,
        name: 'Category',
        description: 'No image',
        slug: 'category',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
      };

      const categoryWithImage: Category = {
        id: 2,
        name: 'Category With Image',
        description: 'Has image',
        slug: 'category-with-image',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
        imageId: 100,
      };

      expect(categoryWithoutImage.imageId).toBeUndefined();
      expect(categoryWithImage.imageId).toBe(100);
      expect(typeof categoryWithImage.imageId).toBe('number');
    });

    it('should accept categoryImage as optional property', () => {
      const categoryWithoutImageObj: Category = {
        id: 1,
        name: 'Category',
        description: 'No image object',
        slug: 'category',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
      };

      const categoryWithImageObj: Category = {
        id: 2,
        name: 'Category With Image Object',
        description: 'Has image object',
        slug: 'category-with-image-obj',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
        imageId: 200,
        categoryImage: {
          id: 200,
          url: 'https://example.com/image.jpg',
        },
      };

      expect(categoryWithoutImageObj.categoryImage).toBeUndefined();
      expect(categoryWithImageObj.categoryImage).toBeDefined();
      expect(categoryWithImageObj.categoryImage?.id).toBe(200);
      expect(categoryWithImageObj.categoryImage?.url).toBe('https://example.com/image.jpg');
    });

    it('should have correct types for categoryImage', () => {
      const category: Category = {
        id: 1,
        name: 'Category',
        description: 'desc',
        slug: 'slug',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
        imageId: 1,
        categoryImage: {
          id: 1,
          url: 'https://example.com/img.jpg',
        },
      };

      expect(typeof category.categoryImage?.id).toBe('number');
      expect(typeof category.categoryImage?.url).toBe('string');
    });
  });

  describe('Category object creation', () => {
    it('should create valid category with all fields', () => {
      const categoryData: Category = {
        id: 10,
        name: 'Laptops',
        description: 'Laptop computers',
        slug: 'laptops',
        parentId: 1,
        metaKeywords: 'laptop, notebook, computer',
        metaDescription: 'Best laptops',
        displayOrder: 2,
        isPublish: true,
        imageId: 50,
        categoryImage: {
          id: 50,
          url: '/images/laptops.jpg',
        },
      };

      expect(categoryData).toEqual({
        id: 10,
        name: 'Laptops',
        description: 'Laptop computers',
        slug: 'laptops',
        parentId: 1,
        metaKeywords: 'laptop, notebook, computer',
        metaDescription: 'Best laptops',
        displayOrder: 2,
        isPublish: true,
        imageId: 50,
        categoryImage: {
          id: 50,
          url: '/images/laptops.jpg',
        },
      });
    });

    it('should allow partial category for updates', () => {
      const partialCategory: Partial<Category> = {
        name: 'Updated Category',
        isPublish: false,
        displayOrder: 5,
      };

      expect(partialCategory.name).toBe('Updated Category');
      expect(partialCategory.isPublish).toBe(false);
      expect(partialCategory.displayOrder).toBe(5);
      expect(partialCategory.id).toBeUndefined();
      expect(partialCategory.slug).toBeUndefined();
    });

    it('should accept empty strings for text fields', () => {
      const category: Category = {
        id: 1,
        name: '',
        description: '',
        slug: '',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: false,
      };

      expect(category.name).toBe('');
      expect(category.description).toBe('');
      expect(category.slug).toBe('');
      expect(category.metaKeywords).toBe('');
      expect(category.metaDescription).toBe('');
    });

    it('should accept negative displayOrder', () => {
      const category: Category = {
        id: 1,
        name: 'Hidden Category',
        description: 'Negative order',
        slug: 'hidden',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: -1,
        isPublish: false,
      };

      expect(category.displayOrder).toBe(-1);
    });
  });

  describe('Category array handling', () => {
    const categories: Category[] = [
      {
        id: 1,
        name: 'Electronics',
        description: 'Electronic items',
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
      {
        id: 3,
        name: 'Books',
        description: 'Book items',
        slug: 'books',
        parentId: null,
        metaKeywords: 'books',
        metaDescription: 'Books category',
        displayOrder: 3,
        isPublish: false,
      },
    ];

    it('should filter categories by isPublish', () => {
      const publishedCategories = categories.filter(c => c.isPublish);
      
      expect(publishedCategories).toHaveLength(2);
      expect(publishedCategories.every(c => c.isPublish)).toBe(true);
    });

    it('should find category by id', () => {
      const category = categories.find(c => c.id === 2);
      
      expect(category).toBeDefined();
      expect(category?.name).toBe('Clothing');
    });

    it('should get root categories (parentId = null)', () => {
      const rootCategories = categories.filter(c => c.parentId === null);
      
      expect(rootCategories).toHaveLength(3);
    });

    it('should sort categories by displayOrder', () => {
      const sorted = [...categories].sort((a, b) => a.displayOrder - b.displayOrder);
      
      expect(sorted[0].displayOrder).toBe(1);
      expect(sorted[1].displayOrder).toBe(2);
      expect(sorted[2].displayOrder).toBe(3);
    });
  });

  describe('Category utility functions', () => {
    // Validate category
    const isValidCategory = (category: any): category is Category => {
      return (
        typeof category.id === 'number' &&
        typeof category.name === 'string' &&
        typeof category.description === 'string' &&
        typeof category.slug === 'string' &&
        (category.parentId === null || typeof category.parentId === 'number') &&
        typeof category.metaKeywords === 'string' &&
        typeof category.metaDescription === 'string' &&
        typeof category.displayOrder === 'number' &&
        typeof category.isPublish === 'boolean'
      );
    };

    it('should validate correct category object', () => {
      const category = {
        id: 1,
        name: 'Valid',
        description: 'Valid description',
        slug: 'valid',
        parentId: null,
        metaKeywords: 'keywords',
        metaDescription: 'description',
        displayOrder: 0,
        isPublish: true,
      };

      expect(isValidCategory(category)).toBe(true);
    });

    it('should reject invalid category - missing required field', () => {
      const invalidCategory = {
        id: 1,
        name: 'Invalid',
        // missing description, slug, etc.
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
      };

      expect(isValidCategory(invalidCategory)).toBe(false);
    });

    // Create category from API response
    const createCategoryFromApi = (apiData: any): Category => {
      return {
        id: apiData.id,
        name: apiData.name,
        description: apiData.description,
        slug: apiData.slug,
        parentId: apiData.parent_id ?? null,
        metaKeywords: apiData.meta_keywords ?? '',
        metaDescription: apiData.meta_description ?? '',
        displayOrder: apiData.display_order ?? 0,
        isPublish: apiData.is_publish ?? false,
        imageId: apiData.image_id,
        categoryImage: apiData.category_image,
      };
    };

    it('should convert API response to Category object', () => {
      const apiResponse = {
        id: 10,
        name: 'New Category',
        description: 'New description',
        slug: 'new-category',
        parent_id: 5,
        meta_keywords: 'new, category',
        meta_description: 'New category description',
        display_order: 3,
        is_publish: true,
        image_id: 100,
        category_image: {
          id: 100,
          url: '/images/new.jpg',
        },
      };

      const category = createCategoryFromApi(apiResponse);

      expect(category).toEqual({
        id: 10,
        name: 'New Category',
        description: 'New description',
        slug: 'new-category',
        parentId: 5,
        metaKeywords: 'new, category',
        metaDescription: 'New category description',
        displayOrder: 3,
        isPublish: true,
        imageId: 100,
        categoryImage: {
          id: 100,
          url: '/images/new.jpg',
        },
      });
    });

    it('should handle missing optional fields from API', () => {
      const apiResponse = {
        id: 11,
        name: 'Minimal Category',
        description: 'Minimal description',
        slug: 'minimal',
        // missing parent_id, meta_keywords, etc.
      };

      const category = createCategoryFromApi(apiResponse);

      expect(category.parentId).toBeNull();
      expect(category.metaKeywords).toBe('');
      expect(category.metaDescription).toBe('');
      expect(category.displayOrder).toBe(0);
      expect(category.isPublish).toBe(false);
      expect(category.imageId).toBeUndefined();
      expect(category.categoryImage).toBeUndefined();
    });

    // Get full path name helper
    const getCategoryPath = (category: Category, parentMap: Map<number, Category>): string => {
      if (!category.parentId) return category.name;
      const parent = parentMap.get(category.parentId);
      if (!parent) return category.name;
      return `${getCategoryPath(parent, parentMap)} > ${category.name}`;
    };

    it('should build category path', () => {
      const parentMap = new Map<number, Category>();
      
      const electronics: Category = {
        id: 1,
        name: 'Electronics',
        description: '',
        slug: 'electronics',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
      };
      
      const laptops: Category = {
        id: 2,
        name: 'Laptops',
        description: '',
        slug: 'laptops',
        parentId: 1,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
      };
      
      const gaming: Category = {
        id: 3,
        name: 'Gaming Laptops',
        description: '',
        slug: 'gaming-laptops',
        parentId: 2,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
      };

      parentMap.set(1, electronics);
      parentMap.set(2, laptops);
      parentMap.set(3, gaming);

      const path = getCategoryPath(gaming, parentMap);
      
      expect(path).toBe('Electronics > Laptops > Gaming Laptops');
    });
  });

  describe('Category with image handling', () => {
    it('should create category with full image details', () => {
      const categoryWithImage: Category = {
        id: 1,
        name: 'Featured Category',
        description: 'With featured image',
        slug: 'featured',
        parentId: null,
        metaKeywords: 'featured',
        metaDescription: 'Featured category',
        displayOrder: 1,
        isPublish: true,
        imageId: 500,
        categoryImage: {
          id: 500,
          url: 'https://cdn.example.com/featured.jpg',
        },
      };

      expect(categoryWithImage.imageId).toBe(500);
      expect(categoryWithImage.categoryImage?.url).toContain('https://');
    });

    it('should update category image', () => {
      let category: Category = {
        id: 1,
        name: 'Category',
        description: 'desc',
        slug: 'slug',
        parentId: null,
        metaKeywords: '',
        metaDescription: '',
        displayOrder: 0,
        isPublish: true,
      };

      // Add image
      category = {
        ...category,
        imageId: 100,
        categoryImage: { id: 100, url: '/new-image.jpg' },
      };

      expect(category.imageId).toBe(100);
      expect(category.categoryImage?.url).toBe('/new-image.jpg');
    });
  });
});