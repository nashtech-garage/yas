import { formatPrice } from '../formatPrice';

describe('formatPrice', () => {
  it('formats positive number as USD currency', () => {
    expect(formatPrice(1234.56)).toBe('$1,234.56');
  });

  it('formats zero as USD currency', () => {
    expect(formatPrice(0)).toBe('$0.00');
  });
});
