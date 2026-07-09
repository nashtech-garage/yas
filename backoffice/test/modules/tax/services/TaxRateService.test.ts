import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getTaxRates,
  getPageableTaxRates,
  createTaxRate,
  getTaxRate,
  deleteTaxRate,
  editTaxRate,
} from '../../../../modules/tax/services/TaxRateService';
import { TaxRate } from '../../../../modules/tax/models/TaxRate';

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

describe('TaxRateService', () => {
  // SỬA: TaxRate với đúng cấu trúc type
  const mockTaxRate: TaxRate = {
    id: 1,
    rate: 10.5,
    zipCode: '90210',
    taxClassName: 'Standard Tax',
    taxClassId: 1,
    countryId: 1,
    countryName: 'United States',
    stateOrProvinceId: 1,
    stateOrProvinceName: 'California',
  };

  const mockTaxRates: TaxRate[] = [
    {
      id: 1,
      rate: 10.5,
      zipCode: '90210',
      taxClassName: 'Standard Tax',
      taxClassId: 1,
      countryId: 1,
      countryName: 'United States',
      stateOrProvinceId: 1,
      stateOrProvinceName: 'California',
    },
    {
      id: 2,
      rate: 8.5,
      zipCode: '94105',
      taxClassName: 'Standard Tax',
      taxClassId: 1,
      countryId: 1,
      countryName: 'United States',
      stateOrProvinceId: 2,
      stateOrProvinceName: 'Texas',
    },
    {
      id: 3,
      rate: 5.0,
      zipCode: 'M5V2T6',
      taxClassName: 'Reduced Tax',
      taxClassId: 2,
      countryId: 2,
      countryName: 'Canada',
      stateOrProvinceId: 3,
      stateOrProvinceName: 'Ontario',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getTaxRates', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockTaxRates) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getTaxRates();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/tax/backoffice/tax-rates');
    });

    it('should return tax rates data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockTaxRates) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getTaxRates();

      expect(result).toEqual(mockTaxRates);
      expect(result).toHaveLength(3);
      expect(result[0].rate).toBe(10.5);
      expect(result[0].zipCode).toBe('90210');
      expect(result[0].taxClassName).toBe('Standard Tax');
      expect(result[0].countryName).toBe('United States');
      expect(result[0].stateOrProvinceName).toBe('California');
    });

    it('should handle empty list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getTaxRates();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getTaxRates()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableTaxRates', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockPageableResponse = {
        content: mockTaxRates,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableTaxRates(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-rates/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: mockTaxRates,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableTaxRates(pageNo, pageSize);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(3);
      expect(result.totalPages).toBe(5);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableTaxRates(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-rates/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableTaxRates(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-rates/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableTaxRates(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-rates/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableTaxRates(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableTaxRates(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('createTaxRate', () => {
    const newTaxRate: TaxRate = {
      id: 0,
      rate: 7.5,
      zipCode: '12345',
      taxClassName: 'Standard Tax',
      taxClassId: 1,
      countryId: 1,
      countryName: 'United States',
      stateOrProvinceId: 4,
      stateOrProvinceName: 'Florida',
    };

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 4, rate: 7.5 } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createTaxRate(newTaxRate);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating tax rate', async () => {
      const error = new Error('Tax rate already exists for this location');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createTaxRate(newTaxRate)).rejects.toThrow('Tax rate already exists for this location');
    });

    it('should handle validation error', async () => {
      const error = new Error('Rate must be between 0 and 100');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createTaxRate(newTaxRate)).rejects.toThrow('Rate must be between 0 and 100');
    });
  });

  describe('getTaxRate', () => {
    const taxRateId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockTaxRate) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getTaxRate(taxRateId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/tax/backoffice/tax-rates/1');
    });

    it('should return tax rate data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockTaxRate) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getTaxRate(taxRateId);

      expect(result).toEqual(mockTaxRate);
      expect(result.id).toBe(1);
      expect(result.rate).toBe(10.5);
      expect(result.taxClassName).toBe('Standard Tax');
      expect(result.countryName).toBe('United States');
      expect(result.stateOrProvinceName).toBe('California');
    });

    it('should handle taxRateId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getTaxRate(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/tax/backoffice/tax-rates/0');
    });

    it('should handle API error when tax rate not found', async () => {
      const error = new Error('Tax rate not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getTaxRate(999)).rejects.toThrow('Tax rate not found');
    });
  });

  describe('deleteTaxRate', () => {
    const taxRateId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteTaxRate(taxRateId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/tax/backoffice/tax-rates/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteTaxRate(taxRateId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteTaxRate(taxRateId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when tax rate not found', async () => {
      const error = new Error('Tax rate not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteTaxRate(999)).rejects.toThrow('Tax rate not found');
    });
  });

  describe('editTaxRate', () => {
    const taxRateId = 1;
    const updatedTaxRate: TaxRate = {
      id: 1,
      rate: 12.5,
      zipCode: '90210',
      taxClassName: 'Standard Tax',
      taxClassId: 1,
      countryId: 1,
      countryName: 'United States',
      stateOrProvinceId: 1,
      stateOrProvinceName: 'California',
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await editTaxRate(taxRateId, updatedTaxRate);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-rates/1',
        JSON.stringify(updatedTaxRate)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editTaxRate(taxRateId, updatedTaxRate);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editTaxRate(taxRateId, updatedTaxRate);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when tax rate not found', async () => {
      const error = new Error('Tax rate not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editTaxRate(999, updatedTaxRate)).rejects.toThrow('Tax rate not found');
    });

    it('should handle duplicate tax rate error', async () => {
      const error = new Error('Tax rate already exists for this location');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editTaxRate(taxRateId, updatedTaxRate)).rejects.toThrow('Tax rate already exists for this location');
    });
  });
});