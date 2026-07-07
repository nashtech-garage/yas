import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getProductInWarehouse,
  getWarehouses,
  getPageableWarehouses,
  createWarehouse,
  getWarehouse,
  deleteWarehouse,
  editWarehouse,
  FilterExistInWHSelection,
} from '../../../../modules/inventory/services/WarehouseService';
import { WarehouseDetail } from '../../../../modules/inventory/models/WarehouseDetail';
import { ProductInfoVm } from '../../../../modules/inventory/models/ProductInfoVm';

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

describe('WarehouseService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('FilterExistInWHSelection Enum', () => {
    it('should have ALL option', () => {
      expect(FilterExistInWHSelection.ALL).toBe('ALL');
    });

    it('should have YES option', () => {
      expect(FilterExistInWHSelection.YES).toBe('YES');
    });

    it('should have NO option', () => {
      expect(FilterExistInWHSelection.NO).toBe('NO');
    });
  });

  describe('getProductInWarehouse', () => {
    const warehouseId = 1;
    const productName = 'Laptop';
    const productSku = 'SKU-123';
    const existInWHSelection = FilterExistInWHSelection.ALL;

    it('should handle empty product name', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductInWarehouse(warehouseId, '', productSku, existInWHSelection);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/1/products?productName=&productSku=SKU-123&existStatus=ALL'
      );
    });

    it('should handle empty product SKU', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductInWarehouse(warehouseId, productName, '', existInWHSelection);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/1/products?productName=Laptop&productSku=&existStatus=ALL'
      );
    });

    it('should handle existInWHSelection = YES', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductInWarehouse(warehouseId, productName, productSku, FilterExistInWHSelection.YES);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/1/products?productName=Laptop&productSku=SKU-123&existStatus=YES'
      );
    });

    it('should handle existInWHSelection = NO', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductInWarehouse(warehouseId, productName, productSku, FilterExistInWHSelection.NO);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/1/products?productName=Laptop&productSku=SKU-123&existStatus=NO'
      );
    });

    it('should handle warehouseId as zero', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getProductInWarehouse(0, productName, productSku, existInWHSelection);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/0/products?productName=Laptop&productSku=SKU-123&existStatus=ALL'
      );
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getProductInWarehouse(warehouseId, productName, productSku, existInWHSelection)).rejects.toThrow('Network error');
    });
  });

  describe('getWarehouses', () => {
    it('should call API with correct URL', async () => {
      const mockWarehouses: WarehouseDetail[] = [
        { id: 1, name: 'Main Warehouse', contactName: 'John', phone: '123', addressLine1: '123 St', addressLine2: '', city: 'NYC', countryId: 1, stateOrProvinceId: 1, districtId: 1, zipCode: '10001' },
        { id: 2, name: 'Secondary Warehouse', contactName: 'Jane', phone: '456', addressLine1: '456 St', addressLine2: '', city: 'LA', countryId: 1, stateOrProvinceId: 2, districtId: 2, zipCode: '90001' },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockWarehouses) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWarehouses();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/inventory/backoffice/warehouses');
    });

    it('should return warehouses data on success', async () => {
      const mockWarehouses: WarehouseDetail[] = [
        { id: 1, name: 'Main Warehouse', contactName: 'John', phone: '123', addressLine1: '123 St', addressLine2: '', city: 'NYC', countryId: 1, stateOrProvinceId: 1, districtId: 1, zipCode: '10001' },
      ];
      const mockResponse = { json: vi.fn().mockResolvedValue(mockWarehouses) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getWarehouses();

      expect(result).toEqual(mockWarehouses);
      expect(result).toHaveLength(1);
    });

    it('should handle empty warehouse list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getWarehouses();

      expect(result).toEqual([]);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getWarehouses()).rejects.toThrow('Network error');
    });
  });

  describe('getPageableWarehouses', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockPageableResponse = {
        content: [
          { id: 1, name: 'Warehouse 1' },
          { id: 2, name: 'Warehouse 2' },
        ],
        totalPages: 5,
        totalElements: 50,
        pageable: { pageNumber: 0, pageSize: 10 },
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableWarehouses(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return pageable response data', async () => {
      const mockPageableResponse = {
        content: [{ id: 1, name: 'Warehouse 1' }],
        totalPages: 1,
        totalElements: 1,
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPageableResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPageableWarehouses(pageNo, pageSize);

      expect(result).toEqual(mockPageableResponse);
      expect(result.content).toHaveLength(1);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableWarehouses(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableWarehouses(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPageableWarehouses(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPageableWarehouses(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('createWarehouse', () => {
    const mockWarehouseDetail: WarehouseDetail = {
      id: 0,
      name: 'New Warehouse',
      contactName: 'John Doe',
      phone: '123-456-7890',
      addressLine1: '123 Main St',
      addressLine2: 'Suite 100',
      city: 'Los Angeles',
      countryId: 1,
      stateOrProvinceId: 1,
      districtId: 1,
      zipCode: '90210',
    };


    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 1, name: 'New Warehouse' } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createWarehouse(mockWarehouseDetail);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error', async () => {
      const error = new Error('Warehouse name already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createWarehouse(mockWarehouseDetail)).rejects.toThrow('Warehouse name already exists');
    });
  });

  describe('getWarehouse', () => {
    const warehouseId = 1;

    it('should call API with correct URL', async () => {
      const mockWarehouse: WarehouseDetail = {
        id: 1,
        name: 'Main Warehouse',
        contactName: 'John Doe',
        phone: '123-456-7890',
        addressLine1: '123 Main St',
        addressLine2: '',
        city: 'NYC',
        countryId: 1,
        stateOrProvinceId: 1,
        districtId: 1,
        zipCode: '10001',
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockWarehouse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWarehouse(warehouseId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/inventory/backoffice/warehouses/1');
    });

    it('should return warehouse data on success', async () => {
      const mockWarehouse: WarehouseDetail = {
        id: 1,
        name: 'Main Warehouse',
        contactName: 'John Doe',
        phone: '123-456-7890',
        addressLine1: '123 Main St',
        addressLine2: '',
        city: 'NYC',
        countryId: 1,
        stateOrProvinceId: 1,
        districtId: 1,
        zipCode: '10001',
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockWarehouse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getWarehouse(warehouseId);

      expect(result).toEqual(mockWarehouse);
    });

    it('should handle warehouseId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWarehouse(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/inventory/backoffice/warehouses/0');
    });

    it('should handle API error when warehouse not found', async () => {
      const error = new Error('Warehouse not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getWarehouse(999)).rejects.toThrow('Warehouse not found');
    });
  });

  describe('deleteWarehouse', () => {
    const warehouseId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteWarehouse(warehouseId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/inventory/backoffice/warehouses/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteWarehouse(warehouseId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteWarehouse(warehouseId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when warehouse not found', async () => {
      const error = new Error('Warehouse not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteWarehouse(999)).rejects.toThrow('Warehouse not found');
    });

    it('should handle warehouse with existing products', async () => {
      const error = new Error('Cannot delete warehouse with existing products');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteWarehouse(1)).rejects.toThrow('Cannot delete warehouse with existing products');
    });
  });

  describe('editWarehouse', () => {
    const warehouseId = 1;
    const mockWarehouseDetail: WarehouseDetail = {
      id: 1,
      name: 'Updated Warehouse',
      contactName: 'Jane Doe',
      phone: '987-654-3210',
      addressLine1: '456 Oak St',
      addressLine2: 'Suite 200',
      city: 'San Francisco',
      countryId: 1,
      stateOrProvinceId: 2,
      districtId: 2,
      zipCode: '94105',
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await editWarehouse(warehouseId, mockWarehouseDetail);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/inventory/backoffice/warehouses/1',
        JSON.stringify(mockWarehouseDetail)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editWarehouse(warehouseId, mockWarehouseDetail);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await editWarehouse(warehouseId, mockWarehouseDetail);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when warehouse not found', async () => {
      const error = new Error('Warehouse not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editWarehouse(999, mockWarehouseDetail)).rejects.toThrow('Warehouse not found');
    });

    it('should handle validation error', async () => {
      const error = new Error('Warehouse name is required');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(editWarehouse(warehouseId, mockWarehouseDetail)).rejects.toThrow('Warehouse name is required');
    });
  });

});