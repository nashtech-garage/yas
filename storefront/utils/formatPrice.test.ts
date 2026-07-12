import { expect, test, describe } from 'vitest';
import { formatPrice } from './formatPrice';

describe('Test formatPrice', () => {
  test('Định dạng đúng số tiền dương', () => {
    const result = formatPrice(1000);
    expect(result).toContain('$1,000.00');
  });

  test('Định dạng đúng số 0', () => {
    expect(formatPrice(0)).toContain('$0.00');
  });
});
