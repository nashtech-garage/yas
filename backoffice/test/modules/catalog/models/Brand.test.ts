import { describe, it, expect } from 'vitest';
import type { Brand } from '../../../../modules/catalog/models/Brand';

describe('Brand Type', () => {
  describe('Type structure', () => {
    it('should have correct property types', () => {
      const validBrand: Brand = {
        id: 1,
        name: 'Nike',
        slug: 'nike',
        isPublish: true,
      };

      expect(validBrand).toHaveProperty('id');
      expect(validBrand).toHaveProperty('name');
      expect(validBrand).toHaveProperty('slug');
      expect(validBrand).toHaveProperty('isPublish');
      
      expect(typeof validBrand.id).toBe('number');
      expect(typeof validBrand.name).toBe('string');
      expect(typeof validBrand.slug).toBe('string');
      expect(typeof validBrand.isPublish).toBe('boolean');
    });

    it('should accept valid brand object', () => {
      const brand: Brand = {
        id: 100,
        name: 'Adidas',
        slug: 'adidas',
        isPublish: false,
      };

      expect(brand.id).toBe(100);
      expect(brand.name).toBe('Adidas');
      expect(brand.slug).toBe('adidas');
      expect(brand.isPublish).toBe(false);
    });

    it('should accept brand with id as 0', () => {
      const brand: Brand = {
        id: 0,
        name: 'Puma',
        slug: 'puma',
        isPublish: true,
      };

      expect(brand.id).toBe(0);
    });

    it('should accept brand with negative id (if applicable)', () => {
      const brand: Brand = {
        id: -1,
        name: 'Test',
        slug: 'test',
        isPublish: false,
      };

      expect(brand.id).toBe(-1);
    });
  });

  describe('Brand object creation', () => {
    it('should create brand with all required fields', () => {
      const brandData = {
        id: 1,
        name: 'Brand Name',
        slug: 'brand-name',
        isPublish: true,
      };

      const brand: Brand = { ...brandData };

      expect(brand).toEqual(brandData);
    });

    it('should allow partial brand object for updates', () => {
      const partialBrand: Partial<Brand> = {
        name: 'Updated Name',
        isPublish: false,
      };

      expect(partialBrand.name).toBe('Updated Name');
      expect(partialBrand.isPublish).toBe(false);
      expect(partialBrand.id).toBeUndefined();
      expect(partialBrand.slug).toBeUndefined();
    });

    it('should create brand with empty string values', () => {
      const brand: Brand = {
        id: 1,
        name: '',
        slug: '',
        isPublish: false,
      };

      expect(brand.name).toBe('');
      expect(brand.slug).toBe('');
    });
  });

  describe('Brand array handling', () => {
    it('should create array of brands', () => {
      const brands: Brand[] = [
        { id: 1, name: 'Nike', slug: 'nike', isPublish: true },
        { id: 2, name: 'Adidas', slug: 'adidas', isPublish: false },
      ];

      expect(brands).toHaveLength(2);
      expect(brands[0].name).toBe('Nike');
      expect(brands[1].name).toBe('Adidas');
    });

    it('should filter brands by isPublish', () => {
      const brands: Brand[] = [
        { id: 1, name: 'Nike', slug: 'nike', isPublish: true },
        { id: 2, name: 'Adidas', slug: 'adidas', isPublish: false },
        { id: 3, name: 'Puma', slug: 'puma', isPublish: true },
      ];

      const publishedBrands = brands.filter(b => b.isPublish);
      
      expect(publishedBrands).toHaveLength(2);
      expect(publishedBrands.every(b => b.isPublish)).toBe(true);
    });

    it('should find brand by id', () => {
      const brands: Brand[] = [
        { id: 1, name: 'Nike', slug: 'nike', isPublish: true },
        { id: 2, name: 'Adidas', slug: 'adidas', isPublish: false },
      ];

      const brand = brands.find(b => b.id === 2);
      
      expect(brand).toBeDefined();
      expect(brand?.name).toBe('Adidas');
    });
  });

  describe('Brand utility functions', () => {
    // Helper function to validate brand
    const isValidBrand = (brand: any): brand is Brand => {
      return (
        typeof brand.id === 'number' &&
        typeof brand.name === 'string' &&
        typeof brand.slug === 'string' &&
        typeof brand.isPublish === 'boolean'
      );
    };

    it('should validate correct brand object', () => {
      const brand = {
        id: 1,
        name: 'Nike',
        slug: 'nike',
        isPublish: true,
      };

      expect(isValidBrand(brand)).toBe(true);
    });

    it('should reject invalid brand object - missing field', () => {
      const invalidBrand = {
        id: 1,
        name: 'Nike',
        slug: 'nike',
        // missing isPublish
      };

      expect(isValidBrand(invalidBrand)).toBe(false);
    });

    it('should reject invalid brand object - wrong type', () => {
      const invalidBrand = {
        id: '1', // string instead of number
        name: 'Nike',
        slug: 'nike',
        isPublish: true,
      };

      expect(isValidBrand(invalidBrand)).toBe(false);
    });

    // Create brand from API response
    const createBrandFromApi = (apiData: any): Brand => {
      return {
        id: apiData.id,
        name: apiData.name,
        slug: apiData.slug,
        isPublish: apiData.is_publish ?? false,
      };
    };

    it('should convert API response to Brand object', () => {
      const apiResponse = {
        id: 10,
        name: 'New Brand',
        slug: 'new-brand',
        is_publish: true,
      };

      const brand = createBrandFromApi(apiResponse);

      expect(brand).toEqual({
        id: 10,
        name: 'New Brand',
        slug: 'new-brand',
        isPublish: true,
      });
    });

    it('should handle missing is_publish from API', () => {
      const apiResponse = {
        id: 11,
        name: 'Test Brand',
        slug: 'test-brand',
      };

      const brand = createBrandFromApi(apiResponse);

      expect(brand.isPublish).toBe(false);
    });
  });
});