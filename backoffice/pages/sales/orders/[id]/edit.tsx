import type { NextPage } from 'next';
import { useState, useEffect } from 'react';
import { getOrderById, getOrders } from 'modules/order/services/OrderService';
import { SALES_ORDERS_URL } from '@constants/Common';
import { Order } from 'modules/order/models/Order';
import { useRouter } from 'next/router';
import { toastError } from '@commonServices/ToastService';
import Link from 'next/link';
import OrderBriefInfo from 'modules/order/components/OrderBriefInfo';
import BillingNShippingInfo from 'modules/order/components/BillingNShippingInfo';
import OrderProductInfo from 'modules/order/components/OrderProductInfo';
import OrderHistory from 'modules/order/components/OrderHistory';

const EditOrder: NextPage = () => {
  //Get ID
  const router = useRouter();
  const { id } = router.query;
  const [order, setOrder] = useState<Order>();
  const [isLoading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    if (id) {
      getOrderById(+id)
        .then((res) => {
          setOrder(res);
          setLoading(false);
        })
        .catch((err) => {
          toastError(err.message);
          router.push(SALES_ORDERS_URL);
        });
    }
  }, [id]);
  if (isLoading) return <p>Loading...</p>;
  return (
    <>
      <div className="row mt-3">
        <div className="col-md-6 d-flex">
          <Link href={`/sales/orders`}>
            <i
              className="fa fa-arrow-left me-2 mt-2 border border-1 rounded-circle p-2 border-dark cursor-pointer"
              aria-hidden="true"
            ></i>
          </Link>
          <h2 className="text-danger font-weight-bold mb-3">Order Detail - {id}</h2>
        </div>
        <div className="col-md-6 text-right">
          <button type="button" className="btn btn-info me-2">
            <i className="fa fa-print me-2" aria-hidden="true"></i> Print PDF Invoice
          </button>
          <button type="button" className="btn btn-danger me-2">
            <i className="fa fa-trash" aria-hidden="true"></i> Delete
          </button>
        </div>
      </div>

      <OrderBriefInfo order={order!} />
      <BillingNShippingInfo order={order!} />
      <OrderProductInfo order={order!} />
      <OrderHistory order={order!} />
    </>
  );
};

export default EditOrder;
