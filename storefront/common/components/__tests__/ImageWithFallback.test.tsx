import { fireEvent, render, screen } from '@testing-library/react';

import ImageWithFallback from '../ImageWithFallback';

describe('ImageWithFallback', () => {
  it('renders the provided source initially', () => {
    render(<ImageWithFallback src="/image-a.png" alt="product" />);

    const image = screen.getByAltText('product') as HTMLImageElement;
    expect(image).toHaveAttribute('src', '/image-a.png');
  });

  it('switches to fallback when image errors', () => {
    render(<ImageWithFallback src="/image-a.png" alt="product" fallBack="/fallback.png" />);

    const image = screen.getByAltText('product') as HTMLImageElement;
    fireEvent.error(image);

    expect(image).toHaveAttribute('src', '/fallback.png');
  });

  it('resets fallback when src prop changes', () => {
    const { rerender } = render(
      <ImageWithFallback src="/broken.png" alt="product" fallBack="/fallback.png" />
    );

    const image = screen.getByAltText('product') as HTMLImageElement;
    fireEvent.error(image);
    expect(image).toHaveAttribute('src', '/fallback.png');

    rerender(<ImageWithFallback src="/fresh.png" alt="product" fallBack="/fallback.png" />);

    expect(image).toHaveAttribute('src', '/fresh.png');
  });
});
