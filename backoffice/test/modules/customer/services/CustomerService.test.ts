import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getCustomers,
  getCustomer,
  createCustomer,
  updateCustomer,
  deleteCustomer,
  getMyProfile,
} from '../../../../modules/customer/services/CustomerService';
import apiClientService from '@commonServices/ApiClientService';

// Mock ApiClientService
vi.mock('@commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('CustomerService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getCustomers', () => {
    it('should call API with correct URL for page 1', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ items: [], totalPages: 1 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCustomers(1);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/backoffice/customers?pageNo=1');
      expect(mockResponse.json).toHaveBeenCalled();
    });

    it('should call API with correct URL for page 5', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ items: [], totalPages: 5 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCustomers(5);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/backoffice/customers?pageNo=5');
    });

    it('should handle page 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ items: [], totalPages: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCustomers(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/backoffice/customers?pageNo=0');
    });
  });

  describe('getCustomer', () => {
    it('should call API with correct URL for given userId', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ id: '123', username: 'john_doe' }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCustomer('123');

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/backoffice/customers/profile/123');
      expect(mockResponse.json).toHaveBeenCalled();
    });

    it('should return customer data', async () => {
      const mockCustomer = {
        id: '456',
        username: 'jane_doe',
        email: 'jane@example.com',
        firstName: 'Jane',
        lastName: 'Doe',
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockCustomer) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getCustomer('456');

      expect(result).toEqual(mockCustomer);
      expect(result.id).toBe('456');
      expect(result.username).toBe('jane_doe');
    });

    it('should handle user not found', async () => {
      const mockResponse = { json: vi.fn().mockRejectedValue(new Error('User not found')) };
      (apiClientService.get as any).mockRejectedValue(new Error('User not found'));

      await expect(getCustomer('999')).rejects.toThrow('User not found');
    });
  });

  describe('createCustomer', () => {
    const mockCustomerData = {
      username: 'new_user',
      password: 'password123',
      confirmPassword: 'password123',
      email: 'new@example.com',
      firstName: 'New',
      lastName: 'User',
      role: 'CUSTOMER',
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.post as any).mockResolvedValue({ status: 201 });

      await createCustomer(mockCustomerData);

      expect(apiClientService.post).toHaveBeenCalledTimes(1);
      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/customer/backoffice/customers',
        JSON.stringify(mockCustomerData)
      );
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 201, json: vi.fn().mockResolvedValue({ id: 1 }) };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createCustomer(mockCustomerData);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating customer', async () => {
      (apiClientService.post as any).mockRejectedValue(new Error('Email already exists'));

      await expect(createCustomer(mockCustomerData)).rejects.toThrow('Email already exists');
    });

    it('should send customer data as JSON string', async () => {
      (apiClientService.post as any).mockResolvedValue({ status: 201 });

      await createCustomer(mockCustomerData);

      const callArg = (apiClientService.post as any).mock.calls[0][1];
      expect(callArg).toBe(JSON.stringify(mockCustomerData));
      expect(() => JSON.parse(callArg)).not.toThrow();
    });
  });

  describe('updateCustomer', () => {
    const mockCustomerUpdateData = {
      username: 'updated_user',
      email: 'updated@example.com',
      firstName: 'Updated',
      lastName: 'User',
      isPublish: true,
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 200 });

      await updateCustomer('789', mockCustomerUpdateData);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/customer/backoffice/customers/profile/789',
        JSON.stringify(mockCustomerUpdateData)
      );
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 200, json: vi.fn().mockResolvedValue({ id: '789' }) };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateCustomer('789', mockCustomerUpdateData);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when updating customer', async () => {
      (apiClientService.put as any).mockRejectedValue(new Error('Customer not found'));

      await expect(updateCustomer('999', mockCustomerUpdateData)).rejects.toThrow('Customer not found');
    });

    it('should send update data as JSON string', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 200 });

      await updateCustomer('123', mockCustomerUpdateData);

      const callArg = (apiClientService.put as any).mock.calls[0][1];
      expect(callArg).toBe(JSON.stringify(mockCustomerUpdateData));
    });
  });

  describe('deleteCustomer', () => {
    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteCustomer('123');

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/customer/backoffice/customers/profile/123');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteCustomer('123');

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteCustomer('123');

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle 404 error when customer not found', async () => {
      (apiClientService.delete as any).mockRejectedValue(new Error('Customer not found'));

      await expect(deleteCustomer('999')).rejects.toThrow('Customer not found');
    });

    it('should handle 400 error', async () => {
      (apiClientService.delete as any).mockRejectedValue(new Error('Cannot delete customer with orders'));

      await expect(deleteCustomer('123')).rejects.toThrow('Cannot delete customer with orders');
    });
  });

  describe('getMyProfile', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ id: 'current_user', username: 'me' }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getMyProfile();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/storefront/customer/profile');
    });

    it('should return current user profile', async () => {
      const mockProfile = {
        id: 'current_user',
        username: 'john_doe',
        email: 'john@example.com',
        firstName: 'John',
        lastName: 'Doe',
      };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockProfile) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getMyProfile();

      expect(result).toEqual(mockProfile);
      expect(result.id).toBe('current_user');
      expect(result.username).toBe('john_doe');
    });

    it('should handle unauthenticated user', async () => {
      (apiClientService.get as any).mockRejectedValue(new Error('Unauthorized'));

      await expect(getMyProfile()).rejects.toThrow('Unauthorized');
    });

    it('should handle network error', async () => {
      (apiClientService.get as any).mockRejectedValue(new Error('Network error'));

      await expect(getMyProfile()).rejects.toThrow('Network error');
    });
  });

  describe('Error handling', () => {
    it('should handle network errors in getCustomers', async () => {
      (apiClientService.get as any).mockRejectedValue(new Error('Network error'));

      await expect(getCustomers(1)).rejects.toThrow('Network error');
    });

    it('should handle network errors in getCustomer', async () => {
      (apiClientService.get as any).mockRejectedValue(new Error('Network error'));

      await expect(getCustomer('123')).rejects.toThrow('Network error');
    });

    it('should handle network errors in createCustomer', async () => {
      (apiClientService.post as any).mockRejectedValue(new Error('Network error'));

      await expect(createCustomer({} as any)).rejects.toThrow('Network error');
    });

    it('should handle network errors in updateCustomer', async () => {
      (apiClientService.put as any).mockRejectedValue(new Error('Network error'));

      await expect(updateCustomer('123', {} as any)).rejects.toThrow('Network error');
    });

    it('should handle network errors in deleteCustomer', async () => {
      (apiClientService.delete as any).mockRejectedValue(new Error('Network error'));

      await expect(deleteCustomer('123')).rejects.toThrow('Network error');
    });
  });

  describe('URL construction', () => {
    it('should handle page numbers correctly', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({}) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCustomers(10);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/backoffice/customers?pageNo=10');
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({}) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getCustomers(1000);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/customer/backoffice/customers?pageNo=1000');
    });
  });
});