import { expect, test, describe } from 'vitest';
import { concatQueryString } from './concatQueryString';

describe('Test concatQueryString', () => {
  test('Mảng rỗng phải trả về URL gốc', () => {
    expect(concatQueryString([], 'http://api.com')).toBe('http://api.com');
  });

  test('Mảng có 1 phần tử phải thêm dấu ?', () => {
    expect(concatQueryString(['name=yas'], 'api')).toBe('api?name=yas');
  });

  test('Mảng có nhiều phần tử phải thêm dấu & từ phần tử thứ 2', () => {
    expect(concatQueryString(['name=yas', 'page=1'], 'api')).toBe('api?name=yas&page=1');
  });
});
