/// <reference types="jest" />
import { formatPrice } from '../formatPrice';

describe('formatPrice', () => {
  it('should format a whole number as USD currency', () => {
    const result = formatPrice(100);
    expect(result).toContain('100');
    expect(result).toContain('$');
  });

  it('should format zero correctly', () => {
    const result = formatPrice(0);
    expect(result).toContain('0');
    expect(result).toContain('$');
  });

  it('should format a decimal number as USD currency', () => {
    const result = formatPrice(99.99);
    expect(result).toContain('99.99');
    expect(result).toContain('$');
  });

  it('should format a large number', () => {
    const result = formatPrice(1000000);
    expect(result).toContain('1,000,000');
    expect(result).toContain('$');
  });

  it('should return a string', () => {
    const result = formatPrice(50);
    expect(typeof result).toBe('string');
  });
});
