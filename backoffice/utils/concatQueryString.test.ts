import { concatQueryString } from './concatQueryString';

describe('concatQueryString', () => {
  it('returns original url when no query parts', () => {
    expect(concatQueryString([], '/api/items')).toBe('/api/items');
  });

  it('concatenates query parts in order', () => {
    const result = concatQueryString(
      ['pageNo=1', 'pageSize=10', 'product-name=shirt'],
      '/api/items'
    );
    expect(result).toBe('/api/items?pageNo=1&pageSize=10&product-name=shirt');
  });
});
