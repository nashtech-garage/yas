import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getRatings,
  getLatestRatings,
  deleteRatingById,
} from '../../../../modules/rating/services/RatingService';
import { Rating } from '../../../../modules/rating/models/Rating';

// Mock ApiClientService
vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import apiClientService from '../../../../common/services/ApiClientService';

describe('RatingService', () => {
  // SỬA: Rating với đúng cấu trúc type
  const mockRating: Rating = {
    id: 1,
    content: 'Great product! Very satisfied with the quality.',
    createdBy: 'john.doe@example.com',
    star: 5,
    productId: 100,
    createdOn: new Date('2024-01-15T10:30:00Z'),
    lastName: 'Doe',
    firstName: 'John',
    productName: 'Laptop',
  };

  const mockRatingList: Rating[] = [
    mockRating,
    {
      id: 2,
      content: 'Good quality, fast shipping',
      createdBy: 'jane.smith@example.com',
      star: 4,
      productId: 101,
      createdOn: new Date('2024-01-16T10:30:00Z'),
      lastName: 'Smith',
      firstName: 'Jane',
      productName: 'Mouse',
    },
    {
      id: 3,
      content: 'Excellent product, highly recommended',
      createdBy: 'bob.johnson@example.com',
      star: 5,
      productId: 102,
      createdOn: new Date('2024-01-17T10:30:00Z'),
      lastName: 'Johnson',
      firstName: 'Bob',
      productName: 'Keyboard',
    },
  ];

  const mockRatingsResponse = {
    ratingList: mockRatingList,
    totalPages: 5,
    totalElements: 50,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getRatings', () => {
    const mockParams = 'pageNo=1&pageSize=10&sort=id,desc';

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockRatingsResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getRatings(mockParams);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/rating/backoffice/ratings?pageNo=1&pageSize=10&sort=id,desc'
      );
    });

    it('should return ratings data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockRatingsResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getRatings(mockParams);

      expect(result).toEqual(mockRatingsResponse);
      expect(result.ratingList).toHaveLength(3);
      expect(result.totalPages).toBe(5);
      expect(result.totalElements).toBe(50);
      
      // Verify rating fields
      expect(result.ratingList[0].id).toBe(1);
      expect(result.ratingList[0].content).toBe('Great product! Very satisfied with the quality.');
      expect(result.ratingList[0].createdBy).toBe('john.doe@example.com');
      expect(result.ratingList[0].star).toBe(5);
      expect(result.ratingList[0].productId).toBe(100);
      expect(result.ratingList[0].firstName).toBe('John');
      expect(result.ratingList[0].lastName).toBe('Doe');
      expect(result.ratingList[0].productName).toBe('Laptop');
    });

    it('should handle empty params string', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ ratingList: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getRatings('');

      expect(apiClientService.get).toHaveBeenCalledWith('/api/rating/backoffice/ratings?');
    });

    it('should handle empty rating list', async () => {
      const emptyResponse = { ratingList: [], totalPages: 0, totalElements: 0 };
      const mockResponse = { json: vi.fn().mockResolvedValue(emptyResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getRatings(mockParams);

      expect(result.ratingList).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle complex params string', async () => {
      const complexParams = 'pageNo=2&pageSize=20&sort=createdOn,asc&star=5';
      const mockResponse = { json: vi.fn().mockResolvedValue(mockRatingsResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getRatings(complexParams);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/rating/backoffice/ratings?pageNo=2&pageSize=20&sort=createdOn,asc&star=5'
      );
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getRatings(mockParams)).rejects.toThrow('Network error');
    });
  });

  describe('getLatestRatings', () => {
    const count = 5;

    it('should call API with correct URL', async () => {
      const mockResponse = { status: 200, json: vi.fn().mockResolvedValue(mockRatingList) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getLatestRatings(count);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/rating/backoffice/ratings/latest/5');
    });

    it('should return ratings on success (status 200)', async () => {
      const mockResponse = { status: 200, json: vi.fn().mockResolvedValue(mockRatingList) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getLatestRatings(count);

      expect(result).toEqual(mockRatingList);
      expect(result).toHaveLength(3);
      expect(result[0].productName).toBe('Laptop');
      expect(result[0].firstName).toBe('John');
      expect(result[0].lastName).toBe('Doe');
      expect(result[0].star).toBe(5);
    });

    it('should return ratings on success (status 201)', async () => {
      const mockResponse = { status: 201, json: vi.fn().mockResolvedValue(mockRatingList) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getLatestRatings(count);

      expect(result).toEqual(mockRatingList);
    });

    it('should return ratings on success (status 204)', async () => {
      const mockResponse = { status: 204, json: vi.fn().mockResolvedValue(mockRatingList) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getLatestRatings(count);

      expect(result).toEqual(mockRatingList);
    });

    it('should reject with error when status is 400', async () => {
      const mockResponse = { status: 400, statusText: 'Bad Request' };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await expect(getLatestRatings(count)).rejects.toThrow('Bad Request');
    });

    it('should reject with error when status is 404', async () => {
      const mockResponse = { status: 404, statusText: 'Not Found' };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await expect(getLatestRatings(count)).rejects.toThrow('Not Found');
    });

    it('should reject with error when status is 500', async () => {
      const mockResponse = { status: 500, statusText: 'Internal Server Error' };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await expect(getLatestRatings(count)).rejects.toThrow('Internal Server Error');
    });

    it('should handle count = 0', async () => {
      const mockResponse = { status: 200, json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getLatestRatings(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/rating/backoffice/ratings/latest/0');
    });

    it('should handle count = 10', async () => {
      const mockResponse = { status: 200, json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getLatestRatings(10);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/rating/backoffice/ratings/latest/10');
    });

    it('should handle API network error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getLatestRatings(count)).rejects.toThrow('Network error');
    });
  });

  describe('deleteRatingById', () => {
    const ratingId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }) };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      await deleteRatingById(ratingId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/rating/backoffice/ratings/1');
    });

    it('should return response on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ message: 'Rating deleted successfully' }) };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteRatingById(ratingId);

      expect(result).toEqual({ message: 'Rating deleted successfully' });
    });

    it('should handle ratingId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({}) };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      await deleteRatingById(0);

      expect(apiClientService.delete).toHaveBeenCalledWith('/api/rating/backoffice/ratings/0');
    });

    it('should handle API error when rating not found', async () => {
      const error = new Error('Rating not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteRatingById(999)).rejects.toThrow('Rating not found');
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteRatingById(ratingId)).rejects.toThrow('Network error');
    });
  });

  describe('URL construction', () => {
    it('should use correct base URL for getRatings', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ ratingList: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getRatings('pageNo=1');

      expect(apiClientService.get).toHaveBeenCalledWith(
        expect.stringContaining('/api/rating/backoffice/ratings')
      );
    });

    it('should use correct base URL for getLatestRatings', async () => {
      const mockResponse = { status: 200, json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getLatestRatings(5);

      expect(apiClientService.get).toHaveBeenCalledWith(
        expect.stringContaining('/api/rating/backoffice/ratings/latest/5')
      );
    });

    it('should use correct base URL for deleteRatingById', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({}) };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      await deleteRatingById(1);

      expect(apiClientService.delete).toHaveBeenCalledWith(
        expect.stringContaining('/api/rating/backoffice/ratings/1')
      );
    });
  });
});