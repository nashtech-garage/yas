import { concatQueryString } from '../concatQueryString';

describe('concatQueryString', () => {
  it('returns original url when query array is empty', () => {
    expect(concatQueryString([], '/products')).toBe('/products');
  });

  it('concats first item with ? and remaining items with &', () => {
    expect(concatQueryString(['a=1', 'b=2', 'c=3'], '/products')).toBe('/products?a=1&b=2&c=3');
  });
});
