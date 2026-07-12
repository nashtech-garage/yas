import { describe, it, expect } from 'vitest';
import type { SearchSuggestion } from './SearchSuggestion';

describe('SearchSuggestion type', () => {
  it('should create a valid SearchSuggestion object', () => {
    const suggestion: SearchSuggestion = {
      name: 'Nike Shoes',
    };

    expect(suggestion.name).toBe('Nike Shoes');
  });

  it('should allow multiple SearchSuggestion objects in an array', () => {
    const suggestions: SearchSuggestion[] = [
      { name: 'Nike' },
      { name: 'Adidas' },
      { name: 'Puma' },
    ];

    expect(suggestions).toHaveLength(3);
    expect(suggestions[0].name).toBe('Nike');
    expect(suggestions[2].name).toBe('Puma');
  });

  it('should allow empty string as name', () => {
    const suggestion: SearchSuggestion = {
      name: '',
    };

    expect(suggestion.name).toBe('');
  });

  it('should allow special characters in name', () => {
    const suggestion: SearchSuggestion = {
      name: "Product & Company's Brand!",
    };

    expect(suggestion.name).toContain('&');
    expect(suggestion.name).toContain("'");
    expect(suggestion.name).toContain('!');
  });

  it('should allow very long names', () => {
    const longName = 'A'.repeat(500);
    const suggestion: SearchSuggestion = {
      name: longName,
    };

    expect(suggestion.name.length).toBe(500);
  });

  it('should allow unicode characters in name', () => {
    const suggestion: SearchSuggestion = {
      name: 'Café ☕ Producto 日本',
    };

    expect(suggestion.name).toContain('Café');
    expect(suggestion.name).toContain('日本');
  });

  it('should allow numeric strings in name', () => {
    const suggestion: SearchSuggestion = {
      name: '123 Product 456',
    };

    expect(suggestion.name).toMatch(/\d+/);
  });

  it('should allow filtering suggestions by name', () => {
    const suggestions: SearchSuggestion[] = [
      { name: 'Nike Air Force' },
      { name: 'Nike Running Shoes' },
      { name: 'Adidas Boost' },
      { name: 'Nike Jordans' },
    ];

    const nikes = suggestions.filter((s) => s.name.startsWith('Nike'));
    expect(nikes).toHaveLength(3);
  });

  it('should allow mapping suggestions to display format', () => {
    const suggestions: SearchSuggestion[] = [{ name: 'Product A' }, { name: 'Product B' }];

    const displayNames = suggestions.map((s) => s.name.toUpperCase());
    expect(displayNames[0]).toBe('PRODUCT A');
    expect(displayNames[1]).toBe('PRODUCT B');
  });

  it('should allow checking if suggestion exists', () => {
    const suggestions: SearchSuggestion[] = [{ name: 'Existing' }, { name: 'Also Exists' }];

    const exists = suggestions.some((s) => s.name === 'Existing');
    const notExists = suggestions.some((s) => s.name === 'Not There');

    expect(exists).toBe(true);
    expect(notExists).toBe(false);
  });
});
