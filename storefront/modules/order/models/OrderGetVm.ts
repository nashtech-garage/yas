import { Moment } from 'moment';
import { EDeliveryMethod } from './EDeliveryMethod';
import { EDeliveryStatus } from './EDeliveryStatus';
import { EOrderStatus } from './EOrderStatus';
import { OrderItemGetVm } from './OrderItemGetVm';

export type OrderGetVm = {
  id: number;
  orderStatus: EOrderStatus;
  totalPrice: number;
  deliveryStatus: EDeliveryStatus;
  deliveryMethod: EDeliveryMethod;
  orderItems: OrderItemGetVm[];
  createdOn: Moment;
};
