import ImageWithFallBack from '@/common/components/ImageWithFallback';
import { OrderGetVm } from '../models/OrderGetVm';
import Link from 'next/link';
import { formatPrice } from '@/utils/formatPrice';
import {
  getDeliveryMethodTitle,
  getDeliveryStatusTitle,
  getOrderStatusTitle,
} from '@/utils/orderUtil';
import moment, { Moment } from 'moment';

type Props = {
  order: OrderGetVm;
};

const OrderCard = ({ order }: Props) => {
  const DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';

  const getOrderDate = (orderDate: Moment | string) => {
    if (typeof orderDate === 'string') {
      return moment(orderDate).format(DATE_TIME_FORMAT);
    }
    return orderDate.format(DATE_TIME_FORMAT);
  };

  return (
    <div className="card" style={{ maxWidth: '100%' }} key={order.id}>
      <div className="card-header bg-transparent d-flex justify-content-between">
        <div className="delivery-method-title">
          <span>Delivery Method: {getDeliveryMethodTitle(order.deliveryMethod)}</span>
        </div>
        <div className="delivery-status-title">
          <span>Delivery Status: {getDeliveryStatusTitle(order.deliveryStatus)}</span>
        </div>
        <div className="order-status-title">
          <span>Status: {getOrderStatusTitle(order.orderStatus)}</span>
        </div>
      </div>
      <div className="card-body">
        {order.orderItems.map((item) => (
          <div key={item.id} className="order-item d-flex pb-3">
            <Link
              href={{
                pathname: '/redirect',
                query: { productId: item.productId },
              }}
            >
              <ImageWithFallBack
                src={item.mediaUrl}
                alt={item.productName}
                style={{ width: '120px', height: '120px', cursor: 'pointer' }}
              />
            </Link>
            <div className="align-self-center p-4">
              <div className="item-product-name">
                <Link
                  href={{
                    pathname: '/redirect',
                    query: { productId: item.productId },
                  }}
                >
                  {item.productName}
                </Link>
              </div>
              <div>
                <span style={{ fontWeight: 600 }}>Quantity: {item.quantity}</span>
              </div>
            </div>
            <div className="align-self-center p-4 item-price">
              <span>{formatPrice(item.productPrice)}</span>
            </div>
          </div>
        ))}
      </div>
      <div className="card-footer d-flex justify-content-between bg-transparent">
        <div className="mt-2">
          <span>Order On: {getOrderDate(order.createdOn)}</span>
        </div>
        <div className="order-total-price mt-2">
          <span>Total: {formatPrice(order.totalPrice)}</span>
        </div>
      </div>
    </div>
  );
};

export default OrderCard;
