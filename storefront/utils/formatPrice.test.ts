import { formatPrice } from './formatPrice';

describe('formatPrice', () => {
  it('formats positive numbers as USD currency', () => {
    expect(formatPrice(1234.5)).toBe('$1,234.50');
  });

  it('formats zero correctly', () => {
    expect(formatPrice(0)).toBe('$0.00');
  });
});
