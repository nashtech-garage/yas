import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getStateOrProvincesByCountry,
  getPageableStateOrProvinces,
  createStateOrProvince,
  getStateOrProvince,
  deleteStateOrProvince,
  editStateOrProvince,
  getStatesOrProvinces,
} from '../../../../modules/location/services/StateOrProvinceService';
import { StateOrProvince } from '../../../../modules/location/models/StateOrProvince';

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

describe('StateOrProvinceService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getStateOrProvincesByCountry', () => {
    const countryId = 1;

    it('should call API with correct URL', async () => {
      const mockStates: StateOrProvince[] = [
        { id: 1, name: 'California', code: 'CA', type: 'State', countryId: 1 },
        { id: 2, name: 'Texas', code: 'TX', type: 'State', countryId: 1 },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockStates) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStateOrProvincesByCountry(countryId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/state-or-provinces?countryId=1');
    });

    it('should return states/provinces data on success', async () => {
      const mockStates: StateOrProvince[] = [
        { id: 1, name: 'California', code: 'CA', type: 'State', countryId: 1 },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockStates) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getStateOrProvincesByCountry(countryId);

      expect(result).toEqual(mockStates);
      expect(result).toHaveLength(1);
      expect(result[0].name).toBe('California');
    });

    it('should handle countryId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStateOrProvincesByCountry(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/state-or-provinces?countryId=0');
    });

    it('should return empty array when no states/provinces found', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getStateOrProvincesByCountry(999);

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getStateOrProvincesByCountry(countryId)).rejects.toThrow('Network error');
    });
  });

  describe('getPageableStateOrProvinces', () => {
    const pageNo = 1;
    const pageSize = 10;
    const countryId = 1;

    it('should call API with correct URL with all parameters', async () => {
      const mockPageableResponse = {
        content: [
          { id: 1, name: 'California', code: 'CA', type: 'State', countryId: 1 },
          { id: 2, name: 'Texas', code: 'TX', type: 'State', countryId: 1 },
        ],
        totalPages: 5,
        totalElements: 50,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableStateOrProvinces(pageNo, pageSize, countryId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/location/backoffice/state-or-provinces/paging?pageNo=1&pageSize=10&countryId=1'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: [{ id: 1, name: 'California', code: 'CA', type: 'State', countryId: 1 }],
        totalPages: 1,
        totalElements: 1,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableStateOrProvinces(pageNo, pageSize, countryId);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(1);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableStateOrProvinces(0, pageSize, countryId);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/location/backoffice/state-or-provinces/paging?pageNo=0&pageSize=10&countryId=1'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableStateOrProvinces(pageNo, 0, countryId);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/location/backoffice/state-or-provinces/paging?pageNo=1&pageSize=0&countryId=1'
      );
    });

    it('should handle countryId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableStateOrProvinces(pageNo, pageSize, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/location/backoffice/state-or-provinces/paging?pageNo=1&pageSize=10&countryId=0'
      );
    });

    it('should handle empty response', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableStateOrProvinces(pageNo, pageSize, countryId);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableStateOrProvinces(pageNo, pageSize, countryId)).rejects.toThrow('Network error');
    });
  });

  describe('createStateOrProvince', () => {
    const mockStateOrProvince: StateOrProvince = {
      id: 0,
      name: 'Florida',
      code: 'FL',
      type: 'State',
      countryId: 1,
    };

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 1, name: 'Florida' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createStateOrProvince(mockStateOrProvince);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating state/province', async () => {
      const error = new Error('State/Province already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createStateOrProvince(mockStateOrProvince)).rejects.toThrow('State/Province already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Name is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createStateOrProvince(mockStateOrProvince)).rejects.toThrow('Name is required');
    });
  });

  describe('getStateOrProvince', () => {
    const stateId = 1;

    it('should call API with correct URL', async () => {
      const mockState: StateOrProvince = {
        id: 1,
        name: 'California',
        code: 'CA',
        type: 'State',
        countryId: 1,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockState) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStateOrProvince(stateId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/state-or-provinces/1');
    });

    it('should return state/province data on success', async () => {
      const mockState: StateOrProvince = {
        id: 1,
        name: 'California',
        code: 'CA',
        type: 'State',
        countryId: 1,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockState) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getStateOrProvince(stateId);

      expect(result).toEqual(mockState);
      expect(result.id).toBe(1);
      expect(result.name).toBe('California');
    });

    it('should handle stateId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStateOrProvince(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/state-or-provinces/0');
    });

    it('should handle API error when state/province not found', async () => {
      const error = new Error('State/Province not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getStateOrProvince(999)).rejects.toThrow('State/Province not found');
    });
  });

  describe('deleteStateOrProvince', () => {
    const stateId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteStateOrProvince(stateId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/location/backoffice/state-or-provinces/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteStateOrProvince(stateId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteStateOrProvince(stateId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when state/province not found', async () => {
      const error = new Error('State/Province not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteStateOrProvince(999)).rejects.toThrow('State/Province not found');
    });

    it('should handle state with existing districts', async () => {
      const error = new Error('Cannot delete state/province with existing districts');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteStateOrProvince(1)).rejects.toThrow('Cannot delete state/province with existing districts');
    });
  });

  describe('editStateOrProvince', () => {
    const stateId = 1;
    const mockStateOrProvince: StateOrProvince = {
      id: 1,
      name: 'California',
      code: 'CA',
      type: 'State',
      countryId: 1,
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await editStateOrProvince(stateId, mockStateOrProvince);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/location/backoffice/state-or-provinces/1',
        JSON.stringify(mockStateOrProvince)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editStateOrProvince(stateId, mockStateOrProvince);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editStateOrProvince(stateId, mockStateOrProvince);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when state/province not found', async () => {
      const error = new Error('State/Province not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editStateOrProvince(999, mockStateOrProvince)).rejects.toThrow('State/Province not found');
    });

    it('should handle duplicate code error', async () => {
      const error = new Error('State/Province code already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editStateOrProvince(stateId, mockStateOrProvince)).rejects.toThrow('State/Province code already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Name is required');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editStateOrProvince(stateId, mockStateOrProvince)).rejects.toThrow('Name is required');
    });
  });

  describe('getStatesOrProvinces', () => {
    const stateId = 1;

    it('should call API with correct URL', async () => {
      const mockState: StateOrProvince = {
        id: 1,
        name: 'California',
        code: 'CA',
        type: 'State',
        countryId: 1,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockState) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStatesOrProvinces(stateId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/state-or-provinces/1');
    });

    it('should return state/province data on success', async () => {
      const mockState: StateOrProvince = {
        id: 1,
        name: 'California',
        code: 'CA',
        type: 'State',
        countryId: 1,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockState) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getStatesOrProvinces(stateId);

      expect(result).toEqual(mockState);
    });

    it('should handle stateId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getStatesOrProvinces(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/location/backoffice/state-or-provinces/0');
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getStatesOrProvinces(stateId)).rejects.toThrow('Network error');
    });
  });

});