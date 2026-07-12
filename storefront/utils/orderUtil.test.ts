import { expect, test, describe } from 'vitest';
import * as orderUtil from './orderUtil';

import { EDeliveryMethod } from '@/modules/order/models/EDeliveryMethod';
import { EDeliveryStatus } from '@/modules/order/models/EDeliveryStatus';
import { EOrderStatus } from '@/modules/order/models/EOrderStatus';

describe('Test orderUtil', () => {
  test('getOrderStatusTitle trả về đúng text', () => {
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.PENDING)).toBe('Pending');
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.ACCEPTED)).toBe('Accepted');
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.COMPLETED)).toBe('Completed');
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.CANCELLED)).toBe('Cancelled');
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.PENDING_PAYMENT)).toBe('Pending Payment');
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.PAID)).toBe('Paid');
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.REFUND)).toBe('Refund');
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.SHIPPING)).toBe('Shipping');
    expect(orderUtil.getOrderStatusTitle(EOrderStatus.REJECT)).toBe('Reject');

    expect(orderUtil.getOrderStatusTitle(null)).toBe('All');
  });

  test('getDeliveryMethodTitle trả về đúng text', () => {
    expect(orderUtil.getDeliveryMethodTitle(EDeliveryMethod.GRAB_EXPRESS)).toBe('Grab Express');
    expect(orderUtil.getDeliveryMethodTitle(EDeliveryMethod.VIETTEL_POST)).toBe('Viettel Post');
    expect(orderUtil.getDeliveryMethodTitle(EDeliveryMethod.SHOPEE_EXPRESS)).toBe('Shopee Express');
    expect(orderUtil.getDeliveryMethodTitle(EDeliveryMethod.YAS_EXPRESS)).toBe('Yas Express');

    expect(orderUtil.getDeliveryMethodTitle('UNKNOWN' as EDeliveryMethod)).toBe('Preparing');
  });

  test('getDeliveryStatusTitle trả về đúng text', () => {
    expect(orderUtil.getDeliveryStatusTitle(EDeliveryStatus.CANCELLED)).toBe('Cancelled');
    expect(orderUtil.getDeliveryStatusTitle(EDeliveryStatus.DELIVERED)).toBe('Delivered');
    expect(orderUtil.getDeliveryStatusTitle(EDeliveryStatus.DELIVERING)).toBe('Delivering');
    expect(orderUtil.getDeliveryStatusTitle(EDeliveryStatus.PENDING)).toBe('Pending');

    expect(orderUtil.getDeliveryStatusTitle('UNKNOWN' as EDeliveryStatus)).toBe('Preparing');
  });
});
