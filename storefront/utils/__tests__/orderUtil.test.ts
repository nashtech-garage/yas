/// <reference types="jest" />
import { getOrderStatusTitle, getDeliveryMethodTitle, getDeliveryStatusTitle } from '../orderUtil';
import { EOrderStatus } from '../../modules/order/models/EOrderStatus';
import { EDeliveryMethod } from '../../modules/order/models/EDeliveryMethod';
import { EDeliveryStatus } from '../../modules/order/models/EDeliveryStatus';

describe('getOrderStatusTitle', () => {
  it('should return Pending for PENDING status', () => {
    expect(getOrderStatusTitle(EOrderStatus.PENDING)).toBe('Pending');
  });

  it('should return Accepted for ACCEPTED status', () => {
    expect(getOrderStatusTitle(EOrderStatus.ACCEPTED)).toBe('Accepted');
  });

  it('should return Completed for COMPLETED status', () => {
    expect(getOrderStatusTitle(EOrderStatus.COMPLETED)).toBe('Completed');
  });

  it('should return Cancelled for CANCELLED status', () => {
    expect(getOrderStatusTitle(EOrderStatus.CANCELLED)).toBe('Cancelled');
  });

  it('should return Pending Payment for PENDING_PAYMENT status', () => {
    expect(getOrderStatusTitle(EOrderStatus.PENDING_PAYMENT)).toBe('Pending Payment');
  });

  it('should return Paid for PAID status', () => {
    expect(getOrderStatusTitle(EOrderStatus.PAID)).toBe('Paid');
  });

  it('should return Refund for REFUND status', () => {
    expect(getOrderStatusTitle(EOrderStatus.REFUND)).toBe('Refund');
  });

  it('should return Shipping for SHIPPING status', () => {
    expect(getOrderStatusTitle(EOrderStatus.SHIPPING)).toBe('Shipping');
  });

  it('should return Reject for REJECT status', () => {
    expect(getOrderStatusTitle(EOrderStatus.REJECT)).toBe('Reject');
  });

  it('should return All for null status', () => {
    expect(getOrderStatusTitle(null)).toBe('All');
  });
});

describe('getDeliveryMethodTitle', () => {
  it('should return Grab Express for GRAB_EXPRESS', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.GRAB_EXPRESS)).toBe('Grab Express');
  });

  it('should return Viettel Post for VIETTEL_POST', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.VIETTEL_POST)).toBe('Viettel Post');
  });

  it('should return Shopee Express for SHOPEE_EXPRESS', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.SHOPEE_EXPRESS)).toBe('Shopee Express');
  });

  it('should return Yas Express for YAS_EXPRESS', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.YAS_EXPRESS)).toBe('Yas Express');
  });
});

describe('getDeliveryStatusTitle', () => {
  it('should return Cancelled for CANCELLED status', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.CANCELLED)).toBe('Cancelled');
  });

  it('should return Delivered for DELIVERED status', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.DELIVERED)).toBe('Delivered');
  });

  it('should return Delivering for DELIVERING status', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.DELIVERING)).toBe('Delivering');
  });

  it('should return Pending for PENDING status', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.PENDING)).toBe('Pending');
  });
});
