import { render, waitFor } from '@testing-library/react';
import React from 'react';
import { beforeEach, describe, expect, test, vi } from 'vitest';

const pushMock = vi.fn().mockResolvedValue(undefined);

vi.mock('next/router', () => ({
  useRouter: vi.fn(),
}));

vi.mock('@/modules/catalog/services/ProductService', () => ({
  getProductSlug: vi.fn(),
}));

import { useRouter } from 'next/router';
import { getProductSlug } from '@/modules/catalog/services/ProductService';
import RedirectPage from '../../../pages/redirect';

describe('RedirectPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    pushMock.mockResolvedValue(undefined);
  });

  test('should redirect to product detail when productId exists', async () => {
    vi.mocked(useRouter).mockReturnValue({
      query: { productId: '9' },
      push: pushMock,
    } as never);

    vi.mocked(getProductSlug).mockResolvedValue({
      slug: 'iphone-15',
      productVariantId: 101,
    } as never);

    render(React.createElement(RedirectPage));

    await waitFor(() => {
      expect(getProductSlug).toHaveBeenCalledWith(9);
      expect(pushMock).toHaveBeenCalledWith({
        pathname: '/products/iphone-15',
        query: { pvid: 101 },
      });
    });
  });

  test('should not call service when productId is missing', async () => {
    vi.mocked(useRouter).mockReturnValue({
      query: {},
      push: pushMock,
    } as never);

    render(React.createElement(RedirectPage));

    await waitFor(() => {
      expect(getProductSlug).not.toHaveBeenCalled();
      expect(pushMock).not.toHaveBeenCalled();
    });
  });

  test('should log error when getProductSlug rejects', async () => {
    const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => undefined);

    vi.mocked(useRouter).mockReturnValue({
      query: { productId: '3' },
      push: pushMock,
    } as never);

    const serviceError = new Error('cannot resolve slug');
    vi.mocked(getProductSlug).mockRejectedValue(serviceError);

    render(React.createElement(RedirectPage));

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith(serviceError);
    });
  });
});
