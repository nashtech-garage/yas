import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getOrders,
  getLatestOrders,
  getOrderById,
} from '../../../../modules/order/services/OrderService';
import { Order } from '../../../../modules/order/models/Order';
import { OrderItem } from '../../../../modules/order/models/OrderItem';
import { OrderAddress } from '../../../../modules/order/models/OrderAddress';

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

describe('OrderService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // Mock address data
  const mockAddress: OrderAddress = {
    contactName: 'John Doe',
    phone: '123-456-7890',
    addressLine1: '123 Main St',
    addressLine2: 'Apt 4B',
    zipCode: '10001',
    districtName: 'Manhattan',
    districtId: 1,
    city: 'New York',
    stateOrProvinceName: 'New York',
    stateOrProvinceId: 1,
    countryName: 'United States',
    countryId: 1,
  };

  // Mock order items
  const mockOrderItems: OrderItem[] = [
    {
      id: 1,
      productId: 100,
      productName: 'Laptop',
      quantity: 1,
      productPrice: 999.99,
    },
    {
      id: 2,
      productId: 101,
      productName: 'Mouse',
      quantity: 2,
      productPrice: 29.99,
    },
  ];

  // Mock order data
  const mockOrder: Order = {
    id: 1,
    email: 'john@example.com',
    note: 'Please leave at front door',
    tax: 50.00,
    discount: 10.00,
    numberItem: 3,
    totalPrice: 1059.97,
    deliveryFee: 15.00,
    couponCode: 'SAVE10',
    deliveryMethod: 'Standard Shipping',
    deliveryStatus: 'Shipped',
    paymentMethod: 'Credit Card',
    paymentStatus: 'Paid',
    createdOn: new Date('2024-01-15T10:30:00Z'),
    orderStatus: 'Processing',
    orderItemVms: mockOrderItems,
    shippingAddressVm: mockAddress,
    billingAddressVm: mockAddress,
    checkoutId: 'checkout_123',
  };

  describe('getOrders', () => {
    const mockParams = 'pageNo=1&pageSize=10&sort=id,desc';

    it('should call API with correct URL', async () => {
      const mockResponse = {
        orderList: [mockOrder],
        totalPages: 5,
        totalElements: 50,
      };
      const mockApiResponse = { json: vi.fn().mockResolvedValue(mockResponse) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getOrders(mockParams);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/order/backoffice/orders?pageNo=1&pageSize=10&sort=id,desc'
      );
    });

    it('should return orders data on success', async () => {
      const mockResponse = {
        orderList: [mockOrder],
        totalPages: 5,
        totalElements: 50,
      };
      const mockApiResponse = { json: vi.fn().mockResolvedValue(mockResponse) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      const result = await getOrders(mockParams);

      expect(result).toEqual(mockResponse);
      expect(result.orderList).toHaveLength(1);
      expect(result.totalPages).toBe(5);
      expect(result.totalElements).toBe(50);
    });

    it('should handle empty params string', async () => {
      const mockResponse = { orderList: [], totalPages: 0, totalElements: 0 };
      const mockApiResponse = { json: vi.fn().mockResolvedValue(mockResponse) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getOrders('');

      expect(apiClientService.get).toHaveBeenCalledWith('/api/order/backoffice/orders?');
    });

    it('should handle empty order list', async () => {
      const mockResponse = { orderList: [], totalPages: 0, totalElements: 0 };
      const mockApiResponse = { json: vi.fn().mockResolvedValue(mockResponse) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      const result = await getOrders(mockParams);

      expect(result.orderList).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getOrders(mockParams)).rejects.toThrow('Network error');
    });

    it('should handle complex params string', async () => {
      const complexParams = 'pageNo=2&pageSize=20&sort=createdOn,asc&status=COMPLETED';
      const mockApiResponse = { json: vi.fn().mockResolvedValue({ orderList: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getOrders(complexParams);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/order/backoffice/orders?pageNo=2&pageSize=20&sort=createdOn,asc&status=COMPLETED'
      );
    });
  });

  describe('getLatestOrders', () => {
    const count = 5;

    it('should call API with correct URL', async () => {
      const mockResponse = [mockOrder];
      const mockApiResponse = { 
        status: 200,
        json: vi.fn().mockResolvedValue(mockResponse),
        statusText: 'OK',
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getLatestOrders(count);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/order/backoffice/orders/latest/5');
    });

    it('should return orders on success (status 200)', async () => {
      const mockResponse = [mockOrder];
      const mockApiResponse = { 
        status: 200,
        json: vi.fn().mockResolvedValue(mockResponse),
        statusText: 'OK',
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      const result = await getLatestOrders(count);

      expect(result).toEqual(mockResponse);
      expect(result).toHaveLength(1);
    });

    it('should return orders on success (status 201)', async () => {
      const mockResponse = [mockOrder];
      const mockApiResponse = { 
        status: 201,
        json: vi.fn().mockResolvedValue(mockResponse),
        statusText: 'Created',
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      const result = await getLatestOrders(count);

      expect(result).toEqual(mockResponse);
    });

    it('should return orders on success (status 204)', async () => {
      const mockResponse = [mockOrder];
      const mockApiResponse = { 
        status: 204,
        json: vi.fn().mockResolvedValue(mockResponse),
        statusText: 'No Content',
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      const result = await getLatestOrders(count);

      expect(result).toEqual(mockResponse);
    });

    it('should reject with error when status is 400', async () => {
      const mockApiResponse = { 
        status: 400,
        statusText: 'Bad Request',
        json: vi.fn(),
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await expect(getLatestOrders(count)).rejects.toThrow('Bad Request');
    });

    it('should reject with error when status is 404', async () => {
      const mockApiResponse = { 
        status: 404,
        statusText: 'Not Found',
        json: vi.fn(),
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await expect(getLatestOrders(count)).rejects.toThrow('Not Found');
    });

    it('should reject with error when status is 500', async () => {
      const mockApiResponse = { 
        status: 500,
        statusText: 'Internal Server Error',
        json: vi.fn(),
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await expect(getLatestOrders(count)).rejects.toThrow('Internal Server Error');
    });

    it('should handle count = 0', async () => {
      const mockApiResponse = { 
        status: 200,
        json: vi.fn().mockResolvedValue([]),
        statusText: 'OK',
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getLatestOrders(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/order/backoffice/orders/latest/0');
    });

    it('should handle count = 10', async () => {
      const mockApiResponse = { 
        status: 200,
        json: vi.fn().mockResolvedValue([]),
        statusText: 'OK',
      };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getLatestOrders(10);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/order/backoffice/orders/latest/10');
    });

    it('should handle API network error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getLatestOrders(count)).rejects.toThrow('Network error');
    });
  });

  describe('getOrderById', () => {
    const orderId = 1;

    it('should call API with correct URL', async () => {
      const mockApiResponse = { json: vi.fn().mockResolvedValue(mockOrder) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getOrderById(orderId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/order/backoffice/orders/1');
    });

    it('should return order data on success', async () => {
      const mockApiResponse = { json: vi.fn().mockResolvedValue(mockOrder) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      const result = await getOrderById(orderId);

      expect(result).toEqual(mockOrder);
      expect(result.id).toBe(1);
      expect(result.email).toBe('john@example.com');
    });

    it('should handle orderId = 0', async () => {
      const mockApiResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getOrderById(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/order/backoffice/orders/0');
    });

    it('should handle API error when order not found', async () => {
      const error = new Error('Order not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getOrderById(999)).rejects.toThrow('Order not found');
    });

    it('should handle large order ID', async () => {
      const mockApiResponse = { json: vi.fn().mockResolvedValue({ ...mockOrder, id: 999999 }) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getOrderById(999999);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/order/backoffice/orders/999999');
    });
  });

  describe('URL construction', () => {
    it('should use correct base URL for getOrders', async () => {
      const mockApiResponse = { json: vi.fn().mockResolvedValue({ orderList: [], totalPages: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getOrders('pageNo=1');

      expect(apiClientService.get).toHaveBeenCalledWith(
        expect.stringContaining('/api/order/backoffice/orders')
      );
    });

    it('should use correct base URL for getLatestOrders', async () => {
      const mockApiResponse = { status: 200, json: vi.fn().mockResolvedValue([]), statusText: 'OK' };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getLatestOrders(5);

      expect(apiClientService.get).toHaveBeenCalledWith(
        expect.stringContaining('/api/order/backoffice/orders/latest/5')
      );
    });

    it('should use correct base URL for getOrderById', async () => {
      const mockApiResponse = { json: vi.fn().mockResolvedValue(mockOrder) };
      (apiClientService.get as any).mockResolvedValue(mockApiResponse);

      await getOrderById(1);

      expect(apiClientService.get).toHaveBeenCalledWith(
        expect.stringContaining('/api/order/backoffice/orders/1')
      );
    });
  });
});