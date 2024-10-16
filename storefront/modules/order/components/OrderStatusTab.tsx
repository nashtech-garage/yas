import { useEffect, useState } from 'react';
import { EOrderStatus } from '../models/EOrderStatus';
import { OrderGetVm } from '../models/OrderGetVm';
import { getMyOrders } from '../services/OrderService';
import OrderCard from './OrderCard';
import { getProductsByIds } from '@/modules/catalog/services/ProductService';

type Props = {
  orderStatus: EOrderStatus | null;
};

const OrderStatusTab = ({ orderStatus }: Props) => {
  const [orders, setOrders] = useState<OrderGetVm[]>([]);
  const [productName, setProductName] = useState('');

  const getProductThumbnails = (ids: number[]) => {
    return getProductsByIds(ids);
  };

  useEffect(() => {
    getMyOrders(productName, orderStatus).then((results) => {
      const productIds = results.flatMap((order) => order.orderItems.map((item) => item.productId));
      const orderResults = [...results];
      if (orderResults.length) {
        getProductThumbnails(productIds).then((productThumbnails) => {
          orderResults.forEach((result) => {
            result.orderItems.forEach(
              (item) =>
                (item.mediaUrl = productThumbnails.find(
                  (p) => p.id === item.productId
                )?.thumbnailUrl!)
            );
          });
          setOrders(orderResults);
        });
      } else {
        setOrders(orderResults);
      }
    });
  }, [orderStatus, productName]);

  return (
    <>
      <div className="p-3 my-4">
        <input
          type="text"
          name="product"
          className="form-control p-3"
          id={orderStatus + '-productName'}
          onChange={(e) => setProductName(e.target.value)}
          placeholder="Product Name ..."
        />
      </div>
      {orders.length > 0 ? (
        orders.map((order) => (
          <>
            <OrderCard key={order.id} order={order}></OrderCard>
          </>
        ))
      ) : (
        <div>
          <h3 style={{ textAlign: 'center' }}>No Orders</h3>
        </div>
      )}
    </>
  );
};

export default OrderStatusTab;
