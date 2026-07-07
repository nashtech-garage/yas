import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getDistricts } from '../../../../modules/location/services/DistrictService';

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

describe('DistrictService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getDistricts', () => {
    const stateOrProvinceId = 1;

    it('should call API with correct URL', async () => {
      const mockDistricts = [
        { id: 1, name: 'District 1', stateOrProvinceId: 1 },
        { id: 2, name: 'District 2', stateOrProvinceId: 1 },
        { id: 3, name: 'District 3', stateOrProvinceId: 1 },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockDistricts) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getDistricts(stateOrProvinceId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/district/1');
    });

    it('should return districts data on success', async () => {
      const mockDistricts = [
        { id: 1, name: 'Quận 1', stateOrProvinceId: 1 },
        { id: 2, name: 'Quận 2', stateOrProvinceId: 1 },
        { id: 3, name: 'Quận 3', stateOrProvinceId: 1 },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockDistricts) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getDistricts(stateOrProvinceId);

      expect(result).toEqual(mockDistricts);
      expect(result).toHaveLength(3);
      expect(result[0].name).toBe('Quận 1');
    });

    it('should handle stateOrProvinceId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getDistricts(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/district/0');
    });

    it('should handle negative stateOrProvinceId', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getDistricts(-1);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/district/-1');
    });

    it('should handle large stateOrProvinceId', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getDistricts(999999);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/district/999999');
    });

    it('should return empty array when no districts found', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getDistricts(999);

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getDistricts(stateOrProvinceId)).rejects.toThrow('Network error');
    });

    it('should handle 404 Not Found', async () => {
      const error = new Error('State/Province not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getDistricts(999)).rejects.toThrow('State/Province not found');
    });

    it('should handle 500 server error', async () => {
      const error = new Error('Internal server error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getDistricts(stateOrProvinceId)).rejects.toThrow('Internal server error');
    });

    it('should handle string number parameter', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      // @ts-expect-error - Testing with string input
      await getDistricts('123');

      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/district/123');
    });
  });
});