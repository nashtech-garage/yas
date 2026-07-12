import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { createRating, getAverageStarByProductId, getRatingsByProductId } from './RatingService';

describe('RatingService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getRatingsByProductId should append page params when provided', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue({ ratingList: [], totalPages: 0, totalElements: 0 }),
    } as unknown as Response);

    const result = await getRatingsByProductId(5, 2, 20);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/rating/storefront/ratings/products/5?pageNo=2&pageSize=20'
    );
    expect(result).toEqual({ ratingList: [], totalPages: 0, totalElements: 0 });
  });

  test('getRatingsByProductId should keep base URL when optional params are omitted', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      json: vi.fn().mockResolvedValue({ ratingList: [], totalPages: 0, totalElements: 0 }),
    } as unknown as Response);

    await getRatingsByProductId(8);

    expect(apiClientService.get).toHaveBeenCalledWith('/api/rating/storefront/ratings/products/8');
  });

  test('createRating should throw on non-2xx', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      status: 400,
      statusText: 'Bad Request',
    } as unknown as Response);

    await expect(createRating({} as never)).rejects.toThrow('Bad Request');
  });

  test('getAverageStarByProductId should return json on success', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 200,
      json: vi.fn().mockResolvedValue(4.5),
    } as unknown as Response);

    const result = await getAverageStarByProductId(12);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/rating/storefront/ratings/product/12/average-star'
    );
    expect(result).toBe(4.5);
  });
});
