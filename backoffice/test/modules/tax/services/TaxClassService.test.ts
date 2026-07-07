import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getTaxClasses,
  getPageableTaxClasses,
  createTaxClass,
  getTaxClass,
  deleteTaxClass,
  editTaxClass,
} from '../../../../modules/tax/services/TaxClassService';
import { TaxClass } from '../../../../modules/tax/models/TaxClass';

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

describe('TaxClassService', () => {
  const mockTaxClass: TaxClass = {
    id: 1,
    name: 'Standard Tax',
  };

  const mockTaxClasses: TaxClass[] = [
    { id: 1, name: 'Standard Tax' },
    { id: 2, name: 'Reduced Tax' },
    { id: 3, name: 'Zero Tax' },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getTaxClasses', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockTaxClasses) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getTaxClasses();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/tax/backoffice/tax-classes');
    });

    it('should return tax classes data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockTaxClasses) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getTaxClasses();

      expect(result).toEqual(mockTaxClasses);
      expect(result).toHaveLength(3);
      expect(result[0].name).toBe('Standard Tax');
    });

    it('should handle empty list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getTaxClasses();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getTaxClasses()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableTaxClasses', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockPageableResponse = {
        content: mockTaxClasses,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableTaxClasses(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-classes/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: mockTaxClasses,
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableTaxClasses(pageNo, pageSize);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(3);
      expect(result.totalPages).toBe(5);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableTaxClasses(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-classes/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableTaxClasses(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-classes/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableTaxClasses(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-classes/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableTaxClasses(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableTaxClasses(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('createTaxClass', () => {
    const newTaxClass: TaxClass = {
      id: 0,
      name: 'New Tax Class',
    };


    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 4, name: 'New Tax Class' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createTaxClass(newTaxClass);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating tax class', async () => {
      const error = new Error('Tax class name already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createTaxClass(newTaxClass)).rejects.toThrow('Tax class name already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Tax class name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createTaxClass(newTaxClass)).rejects.toThrow('Tax class name is required');
    });
  });

  describe('getTaxClass', () => {
    const taxClassId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockTaxClass) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getTaxClass(taxClassId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/tax/backoffice/tax-classes/1');
    });

    it('should return tax class data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockTaxClass) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getTaxClass(taxClassId);

      expect(result).toEqual(mockTaxClass);
      expect(result.id).toBe(1);
      expect(result.name).toBe('Standard Tax');
    });

    it('should handle taxClassId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getTaxClass(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/tax/backoffice/tax-classes/0');
    });

    it('should handle API error when tax class not found', async () => {
      const error = new Error('Tax class not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getTaxClass(999)).rejects.toThrow('Tax class not found');
    });
  });

  describe('deleteTaxClass', () => {
    const taxClassId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteTaxClass(taxClassId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/tax/backoffice/tax-classes/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteTaxClass(taxClassId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteTaxClass(taxClassId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when tax class not found', async () => {
      const error = new Error('Tax class not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteTaxClass(999)).rejects.toThrow('Tax class not found');
    });

    it('should handle tax class with existing tax rates', async () => {
      const error = new Error('Cannot delete tax class with existing tax rates');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteTaxClass(1)).rejects.toThrow('Cannot delete tax class with existing tax rates');
    });
  });

  describe('editTaxClass', () => {
    const taxClassId = 1;
    const updatedTaxClass: TaxClass = {
      id: 1,
      name: 'Updated Tax Class',
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await editTaxClass(taxClassId, updatedTaxClass);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/tax/backoffice/tax-classes/1',
        JSON.stringify(updatedTaxClass)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editTaxClass(taxClassId, updatedTaxClass);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editTaxClass(taxClassId, updatedTaxClass);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when tax class not found', async () => {
      const error = new Error('Tax class not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editTaxClass(999, updatedTaxClass)).rejects.toThrow('Tax class not found');
    });

    it('should handle duplicate name error', async () => {
      const error = new Error('Tax class name already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editTaxClass(taxClassId, updatedTaxClass)).rejects.toThrow('Tax class name already exists');
    });
  });

});