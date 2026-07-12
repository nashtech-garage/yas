import { describe, it, expect } from 'vitest';
import type { ProductSearchSuggestions } from './ProductSearchSuggestions';
import type { SearchSuggestion } from './SearchSuggestion';

describe('ProductSearchSuggestions type', () => {
  it('should create a valid ProductSearchSuggestions object with empty suggestions', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [],
    };

    expect(suggestions.productNames).toEqual([]);
    expect(suggestions.productNames).toHaveLength(0);
  });

  it('should create a valid ProductSearchSuggestions object with multiple suggestions', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [{ name: 'Nike Shoes' }, { name: 'Adidas Running' }, { name: 'Puma Classic' }],
    };

    expect(suggestions.productNames).toHaveLength(3);
    expect(suggestions.productNames[0].name).toBe('Nike Shoes');
  });

  it('should allow accessing individual product names', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [{ name: 'Product A' }, { name: 'Product B' }, { name: 'Product C' }],
    };

    expect(suggestions.productNames[0].name).toBe('Product A');
    expect(suggestions.productNames[1].name).toBe('Product B');
    expect(suggestions.productNames[2].name).toBe('Product C');
  });

  it('should allow filtering product names by keyword', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [
        { name: 'Nike Air Force' },
        { name: 'Nike Running Shoes' },
        { name: 'Adidas Boost' },
      ],
    };

    const nikeSuggestions = suggestions.productNames.filter((p) => p.name.includes('Nike'));
    expect(nikeSuggestions).toHaveLength(2);
    expect(nikeSuggestions[0].name).toBe('Nike Air Force');
  });

  it('should allow mapping product names to uppercase', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [{ name: 'product one' }, { name: 'product two' }],
    };

    const uppercased = suggestions.productNames.map((p) => ({
      name: p.name.toUpperCase(),
    }));

    expect(uppercased[0].name).toBe('PRODUCT ONE');
    expect(uppercased[1].name).toBe('PRODUCT TWO');
  });

  it('should allow checking if a product exists in suggestions', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [{ name: 'Existing Product' }, { name: 'Another Product' }],
    };

    const hasProduct = suggestions.productNames.some((p) => p.name === 'Existing Product');
    const notExists = suggestions.productNames.some((p) => p.name === 'Missing Product');

    expect(hasProduct).toBe(true);
    expect(notExists).toBe(false);
  });

  it('should allow sorting product suggestions alphabetically', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [{ name: 'Zebra' }, { name: 'Apple' }, { name: 'Mango' }],
    };

    const sorted = [...suggestions.productNames].sort((a, b) => a.name.localeCompare(b.name));

    expect(sorted[0].name).toBe('Apple');
    expect(sorted[1].name).toBe('Mango');
    expect(sorted[2].name).toBe('Zebra');
  });

  it('should allow adding a product suggestion', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [{ name: 'Existing' }],
    };

    const newSuggestion: SearchSuggestion = { name: 'New Product' };
    suggestions.productNames.push(newSuggestion);

    expect(suggestions.productNames).toHaveLength(2);
    expect(suggestions.productNames[1].name).toBe('New Product');
  });

  it('should allow removing a product suggestion', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [{ name: 'Product 1' }, { name: 'Product 2' }, { name: 'Product 3' }],
    };

    suggestions.productNames = suggestions.productNames.filter((p) => p.name !== 'Product 2');

    expect(suggestions.productNames).toHaveLength(2);
    expect(suggestions.productNames.some((p) => p.name === 'Product 2')).toBe(false);
  });

  it('should handle large number of suggestions', () => {
    const largeSuggestionsList: SearchSuggestion[] = Array.from({ length: 1000 }, (_, i) => ({
      name: `Product ${i + 1}`,
    }));

    const suggestions: ProductSearchSuggestions = {
      productNames: largeSuggestionsList,
    };

    expect(suggestions.productNames).toHaveLength(1000);
    expect(suggestions.productNames[0].name).toBe('Product 1');
    expect(suggestions.productNames[999].name).toBe('Product 1000');
  });

  it('should allow duplicates in product suggestions', () => {
    const suggestions: ProductSearchSuggestions = {
      productNames: [{ name: 'Duplicate' }, { name: 'Duplicate' }, { name: 'Unique' }],
    };

    expect(suggestions.productNames).toHaveLength(3);
    const duplicates = suggestions.productNames.filter((p) => p.name === 'Duplicate');
    expect(duplicates).toHaveLength(2);
  });
});
