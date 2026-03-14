import { render, waitFor } from '@testing-library/react';
import { useRouter } from 'next/router';

import RedirectPage from '@/pages/redirect';
import { getProductSlug } from '@/modules/catalog/services/ProductService';

jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}));

jest.mock('@/modules/catalog/services/ProductService', () => ({
  getProductSlug: jest.fn(),
}));

describe('RedirectPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('requests slug and redirects when productId exists', async () => {
    const pushMock = jest.fn().mockResolvedValue(true);

    (useRouter as jest.Mock).mockReturnValue({
      query: { productId: '12' },
      push: pushMock,
    });

    (getProductSlug as jest.Mock).mockResolvedValue({
      slug: 'iphone-15',
      productVariantId: 99,
    });

    render(<RedirectPage />);

    await waitFor(() => {
      expect(getProductSlug).toHaveBeenCalledWith(12);
      expect(pushMock).toHaveBeenCalledWith({
        pathname: '/products/iphone-15',
        query: { pvid: 99 },
      });
    });
  });

  it('does not call service when productId is missing', () => {
    (useRouter as jest.Mock).mockReturnValue({
      query: {},
      push: jest.fn(),
    });

    render(<RedirectPage />);

    expect(getProductSlug).not.toHaveBeenCalled();
  });
});
