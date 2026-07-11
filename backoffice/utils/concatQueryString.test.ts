import { concatQueryString } from './concatQueryString';

describe('concatQueryString utility', () => {
  test('should append query strings correctly to a URL', () => {
    const url = 'https://example.com';
    const params = ['name=test', 'page=1'];
    const result = concatQueryString(params, url);
    expect(result).toBe('https://example.com?name=test&page=1');
  });

  test('should return original URL if no params provided', () => {
    const url = 'https://example.com';
    const params: string[] = [];
    const result = concatQueryString(params, url);
    expect(result).toBe('https://example.com');
  });

  test('should handle a single parameter', () => {
    const url = 'https://example.com';
    const params = ['id=123'];
    const result = concatQueryString(params, url);
    expect(result).toBe('https://example.com?id=123');
  });
});
