/// <reference types="jest" />
import { concatQueryString } from '../concatQueryString';

describe('concatQueryString', () => {
  it('should return url unchanged when array is empty', () => {
    const result = concatQueryString([], 'https://example.com/products');
    expect(result).toBe('https://example.com/products');
  });

  it('should append first param with ? prefix', () => {
    const result = concatQueryString(['page=1'], 'https://example.com/products');
    expect(result).toBe('https://example.com/products?page=1');
  });

  it('should append multiple params with & separator', () => {
    const result = concatQueryString(
      ['page=1', 'size=10', 'sort=name'],
      'https://example.com/products'
    );
    expect(result).toBe('https://example.com/products?page=1&size=10&sort=name');
  });

  it('should handle single param correctly', () => {
    const result = concatQueryString(['category=electronics'], '/api/products');
    expect(result).toBe('/api/products?category=electronics');
  });
});
