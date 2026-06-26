import { concatQueryString } from './concatQueryString';

describe('concatQueryString', () => {
  it('appends query params to a base URL', () => {
    expect(concatQueryString(['page=1', 'size=20'], '/products')).toBe('/products?page=1&size=20');
  });

  it('returns the original URL when there are no params', () => {
    expect(concatQueryString([], '/products')).toBe('/products');
  });
});
