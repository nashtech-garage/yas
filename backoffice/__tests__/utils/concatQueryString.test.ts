import { concatQueryString } from '../../utils/concatQueryString';

describe('concatQueryString', () => {
  it('should return the url unchanged when array is empty', () => {
    const result = concatQueryString([], '/api/products');
    expect(result).toBe('/api/products');
  });

  it('should append a single query param with ? prefix', () => {
    const result = concatQueryString(['page=0'], '/api/products');
    expect(result).toBe('/api/products?page=0');
  });

  it('should append multiple query params with correct separators', () => {
    const result = concatQueryString(['page=0', 'size=10', 'sort=name'], '/api/products');
    expect(result).toBe('/api/products?page=0&size=10&sort=name');
  });

  it('should append two query params correctly', () => {
    const result = concatQueryString(['page=1', 'size=20'], '/api/items');
    expect(result).toBe('/api/items?page=1&size=20');
  });

  it('should work with an empty base url', () => {
    const result = concatQueryString(['q=test'], '');
    expect(result).toBe('?q=test');
  });

  it('should handle query params with special characters', () => {
    const result = concatQueryString(['name=Hello World'], '/api/search');
    expect(result).toBe('/api/search?name=Hello World');
  });
});
