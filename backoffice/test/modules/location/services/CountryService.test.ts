import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getCountries,
  getPageableCountries,
  createCountry,
  getCountry,
  deleteCountry,
  editCountry,
} from '../../../../modules/location/services/CountryService';
import { Country } from '../../../../modules/location/models/Country';

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

describe('CountryService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getCountries', () => {
    it('should call API with correct URL', async () => {
      const mockCountries: Country[] = [
        { id: 1, code2: 'US', code3: 'USA', name: 'United States', isBillingEnabled: true, isShippingEnabled: true, isCityEnabled: true, isZipCodeEnabled: true, isDistrictEnabled: true },
        { id: 2, code2: 'VN', code3: 'VNM', name: 'Vietnam', isBillingEnabled: true, isShippingEnabled: true, isCityEnabled: true, isZipCodeEnabled: true, isDistrictEnabled: true },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCountries) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCountries();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/countries');
    });

    it('should return countries data on success', async () => {
      const mockCountries: Country[] = [
        { id: 1, code2: 'US', code3: 'USA', name: 'United States', isBillingEnabled: true, isShippingEnabled: true, isCityEnabled: true, isZipCodeEnabled: true, isDistrictEnabled: true },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCountries) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getCountries();

      expect(result).toEqual(mockCountries);
      expect(result).toHaveLength(1);
      expect(result[0].name).toBe('United States');
    });

    it('should handle empty country list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getCountries();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getCountries()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableCountries', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockPageableResponse = {
        content: [
          { id: 1, name: 'United States', code2: 'US', code3: 'USA' },
          { id: 2, name: 'Vietnam', code2: 'VN', code3: 'VNM' },
        ],
        totalPages: 5,
        totalElements: 50,
        pageable: { pageNumber: 0, pageSize: 10 },
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableCountries(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/location/backoffice/countries/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: [{ id: 1, name: 'United States', code2: 'US', code3: 'USA' }],
        totalPages: 1,
        totalElements: 1,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableCountries(pageNo, pageSize);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(1);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableCountries(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/location/backoffice/countries/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableCountries(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/location/backoffice/countries/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableCountries(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/location/backoffice/countries/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableCountries(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableCountries(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('createCountry', () => {
    const mockCountry: Country = {
      id: 0,
      code2: 'FR',
      code3: 'FRA',
      name: 'France',
      isBillingEnabled: true,
      isShippingEnabled: true,
      isCityEnabled: true,
      isZipCodeEnabled: true,
      isDistrictEnabled: false,
    };


    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 1, name: 'France' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createCountry(mockCountry);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating country', async () => {
      const error = new Error('Country code already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createCountry(mockCountry)).rejects.toThrow('Country code already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Country name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createCountry(mockCountry)).rejects.toThrow('Country name is required');
    });
  });

  describe('getCountry', () => {
    const countryId = 1;

    it('should call API with correct URL', async () => {
      const mockCountry: Country = {
        id: 1,
        code2: 'US',
        code3: 'USA',
        name: 'United States',
        isBillingEnabled: true,
        isShippingEnabled: true,
        isCityEnabled: true,
        isZipCodeEnabled: true,
        isDistrictEnabled: true,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCountry) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCountry(countryId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/countries/1');
    });

    it('should return country data on success', async () => {
      const mockCountry: Country = {
        id: 1,
        code2: 'US',
        code3: 'USA',
        name: 'United States',
        isBillingEnabled: true,
        isShippingEnabled: true,
        isCityEnabled: true,
        isZipCodeEnabled: true,
        isDistrictEnabled: true,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCountry) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getCountry(countryId);

      expect(result).toEqual(mockCountry);
      expect(result.id).toBe(1);
      expect(result.name).toBe('United States');
    });

    it('should handle countryId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCountry(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/countries/0');
    });

    it('should handle API error when country not found', async () => {
      const error = new Error('Country not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getCountry(999)).rejects.toThrow('Country not found');
    });
  });

  describe('deleteCountry', () => {
    const countryId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteCountry(countryId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/location/backoffice/countries/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteCountry(countryId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteCountry(countryId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when country not found', async () => {
      const error = new Error('Country not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteCountry(999)).rejects.toThrow('Country not found');
    });

    it('should handle country with existing references', async () => {
      const error = new Error('Cannot delete country with existing states/provinces');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteCountry(1)).rejects.toThrow('Cannot delete country with existing states/provinces');
    });
  });

  describe('editCountry', () => {
    const countryId = 1;
    const mockCountry: Country = {
      id: 1,
      code2: 'US',
      code3: 'USA',
      name: 'United States of America',
      isBillingEnabled: true,
      isShippingEnabled: true,
      isCityEnabled: true,
      isZipCodeEnabled: true,
      isDistrictEnabled: true,
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await editCountry(countryId, mockCountry);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/location/backoffice/countries/1',
        JSON.stringify(mockCountry)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editCountry(countryId, mockCountry);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editCountry(countryId, mockCountry);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when country not found', async () => {
      const error = new Error('Country not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editCountry(999, mockCountry)).rejects.toThrow('Country not found');
    });

    it('should handle duplicate country code error', async () => {
      const error = new Error('Country code already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editCountry(countryId, mockCountry)).rejects.toThrow('Country code already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Country name is required');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editCountry(countryId, mockCountry)).rejects.toThrow('Country name is required');
    });
  });
});