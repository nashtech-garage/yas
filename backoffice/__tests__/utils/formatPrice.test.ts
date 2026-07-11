import { formatPriceVND, formatPriceUSD } from '../../utils/formatPrice';

describe('formatPriceVND', () => {
  it('should format a positive number as VND currency', () => {
    const result = formatPriceVND(100000);
    expect(result).toContain('100.000');
    expect(result).toContain('₫');
  });

  it('should format zero as VND currency', () => {
    const result = formatPriceVND(0);
    expect(result).toContain('₫');
  });

  it('should format a large number as VND currency', () => {
    const result = formatPriceVND(1000000);
    expect(result).toContain('1.000.000');
  });

  it('should format a decimal number as VND currency', () => {
    const result = formatPriceVND(99999.99);
    expect(result).toContain('₫');
  });

  it('should format a negative number as VND currency', () => {
    const result = formatPriceVND(-50000);
    expect(result).toContain('₫');
  });
});

describe('formatPriceUSD', () => {
  it('should format a positive number as USD currency', () => {
    const result = formatPriceUSD(100);
    expect(result).toContain('$');
    expect(result).toContain('100');
  });

  it('should format zero as USD currency', () => {
    const result = formatPriceUSD(0);
    expect(result).toContain('$');
    expect(result).toContain('0');
  });

  it('should format a decimal number as USD currency', () => {
    const result = formatPriceUSD(9.99);
    expect(result).toContain('$');
    expect(result).toContain('9.99');
  });

  it('should format a large number as USD currency', () => {
    const result = formatPriceUSD(1000);
    expect(result).toContain('$');
    expect(result).toContain('1,000');
  });

  it('should format a negative number as USD currency', () => {
    const result = formatPriceUSD(-50);
    expect(result).toContain('$');
  });
});
