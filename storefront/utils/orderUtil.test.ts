import { getDeliveryMethodTitle, getDeliveryStatusTitle, getOrderStatusTitle } from './orderUtil';
import { EDeliveryMethod } from '@/modules/order/models/EDeliveryMethod';
import { EDeliveryStatus } from '@/modules/order/models/EDeliveryStatus';
import { EOrderStatus } from '@/modules/order/models/EOrderStatus';

describe('orderUtil', () => {
  it('maps each order status to the expected title', () => {
    expect(getOrderStatusTitle(EOrderStatus.PENDING)).toBe('Pending');
    expect(getOrderStatusTitle(EOrderStatus.ACCEPTED)).toBe('Accepted');
    expect(getOrderStatusTitle(EOrderStatus.COMPLETED)).toBe('Completed');
    expect(getOrderStatusTitle(EOrderStatus.CANCELLED)).toBe('Cancelled');
    expect(getOrderStatusTitle(EOrderStatus.PENDING_PAYMENT)).toBe('Pending Payment');
    expect(getOrderStatusTitle(EOrderStatus.PAID)).toBe('Paid');
    expect(getOrderStatusTitle(EOrderStatus.REFUND)).toBe('Refund');
    expect(getOrderStatusTitle(EOrderStatus.SHIPPING)).toBe('Shipping');
    expect(getOrderStatusTitle(EOrderStatus.REJECT)).toBe('Reject');
    expect(getOrderStatusTitle(null)).toBe('All');
  });

  it('maps each delivery method to the expected title', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.GRAB_EXPRESS)).toBe('Grab Express');
    expect(getDeliveryMethodTitle(EDeliveryMethod.VIETTEL_POST)).toBe('Viettel Post');
    expect(getDeliveryMethodTitle(EDeliveryMethod.SHOPEE_EXPRESS)).toBe('Shopee Express');
    expect(getDeliveryMethodTitle(EDeliveryMethod.YAS_EXPRESS)).toBe('Yas Express');
    expect(getDeliveryMethodTitle('UNKNOWN' as EDeliveryMethod)).toBe('Preparing');
  });

  it('maps each delivery status to the expected title', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.CANCELLED)).toBe('Cancelled');
    expect(getDeliveryStatusTitle(EDeliveryStatus.DELIVERED)).toBe('Delivered');
    expect(getDeliveryStatusTitle(EDeliveryStatus.DELIVERING)).toBe('Delivering');
    expect(getDeliveryStatusTitle(EDeliveryStatus.PENDING)).toBe('Pending');
    expect(getDeliveryStatusTitle('UNKNOWN' as EDeliveryStatus)).toBe('Preparing');
  });
});
