import { formatPriceVND, formatPriceUSD } from './formatPrice';

describe('formatPrice utility', () => {
  test('formatPriceVND should format price in Vietnamese Dong', () => {
    const price = 100000;
    const result = formatPriceVND(price);
    // Replace non-breaking spaces and fix currency symbol representation if necessary
    const normalizedResult = result.replace(/\u00a0/g, ' ');
    expect(normalizedResult).toMatch(/100.000/);
    expect(normalizedResult).toMatch(/₫/);
  });

  test('formatPriceUSD should format price in US Dollars', () => {
    const price = 100;
    const result = formatPriceUSD(price);
    expect(result).toMatch(/\$100.00/);
  });
});
