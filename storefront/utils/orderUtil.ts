import {EDeliveryMethod} from '@/modules/order/models/EDeliveryMethod';
import {EDeliveryStatus} from '@/modules/order/models/EDeliveryStatus';
import {EOrderStatus} from '@/modules/order/models/EOrderStatus';

export function getOrderStatusTitle(orderStatus: EOrderStatus | null) {
  switch (orderStatus) {
    case EOrderStatus.PENDING:
      return 'Pending';
    case EOrderStatus.ACCEPTED:
      return 'Accepted';
    case EOrderStatus.COMPLETED:
      return 'Completed';
    case EOrderStatus.CANCELLED:
      return 'Cancelled';
    case EOrderStatus.PENDING_PAYMENT:
      return 'Pending Payment';
    case EOrderStatus.PAID:
      return 'Paid';
    case EOrderStatus.REFUND:
      return 'Refund';
    case EOrderStatus.SHIPPING:
      return 'Shipping';
    case EOrderStatus.REJECT:
      return 'Reject';
    default:
      return 'All';
  }
}

export function getDeliveryMethodTitle(deliveryMethod: EDeliveryMethod) {
  switch (deliveryMethod) {
    case EDeliveryMethod.GRAB_EXPRESS:
      return 'Grab Express';
    case EDeliveryMethod.VIETTEL_POST:
      return 'Viettel Post';
    case EDeliveryMethod.SHOPEE_EXPRESS:
      return 'Shopee Express';
    case EDeliveryMethod.YAS_EXPRESS:
      return 'Yas Express';
    default:
      return 'Preparing';
  }
}

export function getDeliveryStatusTitle(deliveryStatus: EDeliveryStatus) {
  switch (deliveryStatus) {
    case EDeliveryStatus.CANCELLED:
      return 'Cancelled';
    case EDeliveryStatus.DELIVERED:
      return 'Delivered';
    case EDeliveryStatus.DELIVERING:
      return 'Delivering';
    case EDeliveryStatus.PENDING:
      return 'Pending';
    default:
      return 'Preparing';
  }
}
