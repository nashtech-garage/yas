import { render, screen } from '@testing-library/react';
import { formatPriceUSD, formatPriceVND } from './formatPrice';

describe('formatPrice utilities', () => {
  it('formats VND price', () => {
    const formatted = formatPriceVND(1000000);
    expect(typeof formatted).toBe('string');
    expect(formatted).toContain('1.000.000');
  });

  it('formats USD price and renders with testing-library/react', () => {
    const formatted = formatPriceUSD(1234.56);

    render(<span data-testid="usd-price">{formatted}</span>);

    expect(screen.getByTestId('usd-price').textContent).toContain('$');
    expect(screen.getByTestId('usd-price').textContent).toContain('1,234.56');
  });
});
