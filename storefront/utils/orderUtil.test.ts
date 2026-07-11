import { getOrderStatusTitle, getDeliveryMethodTitle, getDeliveryStatusTitle } from './orderUtil';
import { EOrderStatus } from '@/modules/order/models/EOrderStatus';
import { EDeliveryMethod } from '@/modules/order/models/EDeliveryMethod';
import { EDeliveryStatus } from '@/modules/order/models/EDeliveryStatus';

describe('orderUtil utility', () => {
  describe('getOrderStatusTitle', () => {
    test('should return correct title for PENDING', () => {
      expect(getOrderStatusTitle(EOrderStatus.PENDING)).toBe('Pending');
    });

    test('should return correct title for COMPLETED', () => {
      expect(getOrderStatusTitle(EOrderStatus.COMPLETED)).toBe('Completed');
    });

    test('should return "All" for unknown status', () => {
      expect(getOrderStatusTitle(null)).toBe('All');
    });
  });

  describe('getDeliveryMethodTitle', () => {
    test('should return correct title for GRAB_EXPRESS', () => {
      expect(getDeliveryMethodTitle(EDeliveryMethod.GRAB_EXPRESS)).toBe('Grab Express');
    });

    test('should return "Preparing" for unknown method', () => {
      expect(getDeliveryMethodTitle('UNKNOWN' as any)).toBe('Preparing');
    });
  });

  describe('getDeliveryStatusTitle', () => {
    test('should return correct title for DELIVERED', () => {
      expect(getDeliveryStatusTitle(EDeliveryStatus.DELIVERED)).toBe('Delivered');
    });

    test('should return "Preparing" for unknown status', () => {
      expect(getDeliveryStatusTitle('UNKNOWN' as any)).toBe('Preparing');
    });
  });
});
