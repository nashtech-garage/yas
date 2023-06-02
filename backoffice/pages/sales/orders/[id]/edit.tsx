import type { NextPage } from 'next';
import React, { useRef, useState, useEffect, useCallback } from 'react';
import { getOrderById } from 'modules/order/services/OrderService';
import { Order } from 'modules/order/models/Order';
import { useRouter } from 'next/router';
import { toastError } from '@commonServices/ToastService';
import Link from 'next/link';
import OrderBriefInfo from 'modules/order/components/OrderBriefInfo';
import BillingNShippingInfo from 'modules/order/components/BillingNShippingInfo';
import OrderProductInfo from 'modules/order/components/OrderProductInfo';
import OrderHistory from 'modules/order/components/OrderHistory';
import ReactToPrint from 'react-to-print';
import HeaderPdf from '../HeaderPdf';
import moment from 'moment';

const EditOrder: NextPage = () => {
  //Get ID
  const router = useRouter();
  const { id } = router.query;
  const [order, setOrder] = useState<Order>();
  const [isLoading, setLoading] = useState(false);

  const componentRef = useRef(null);
  const onBeforeGetContentResolve = useRef<((value?: void) => void) | null>(null);

  const [headerPdf, setHeaderPdf] = useState('');

  const handleAfterPrint = useCallback(() => {
    setHeaderPdf('');
  }, [id]);

  const handleOnBeforeGetContent = useCallback(() => {
    return new Promise((resolve) => {
      onBeforeGetContentResolve.current = resolve;
      setHeaderPdf(
        `Order# ${id} <br />
      <a href="http://storefront.com">http://storefront.com</a> <br />
       Date: ${moment().format('MMMM Do YYYY, h:mm:ss a')}`
      );
      resolve(null);
    });
  }, [id, setHeaderPdf]);

  const reactToPrintContent = useCallback(() => {
    return componentRef.current;
  }, [componentRef.current]);

  const reactToPrintTrigger = useCallback(() => {
    // NOTE: could just as easily return <SomeComponent />. Do NOT pass an `onClick` prop
    // to the root node of the returned component as it will be overwritten.

    // Bad: the `onClick` here will be overwritten by `react-to-print`
    // return <button onClick={() => alert('This will not work')}>Print this out!</button>;

    // Good
    return (
      <button type="button" className="btn btn-info me-2">
        <i className="fa fa-print me-2" aria-hidden="true"></i> Print PDF Invoice
      </button>
    );
  }, []);

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
          <ReactToPrint
            content={reactToPrintContent}
            documentTitle={`invoice-${id}`}
            onAfterPrint={handleAfterPrint}
            onBeforeGetContent={handleOnBeforeGetContent}
            removeAfterPrint
            trigger={reactToPrintTrigger}
          />
          {isLoading && <p className="indicator">onBeforeGetContent: Loading...</p>}
          <button type="button" className="btn btn-danger me-2">
            <i className="fa fa-trash" aria-hidden="true"></i> Delete
          </button>
        </div>
      </div>
      <div ref={componentRef} className="content-will-be-printed">
        <HeaderPdf text={headerPdf} />
        <OrderBriefInfo order={order!} />
        <BillingNShippingInfo order={order!} />
        <OrderProductInfo order={order!} />
      </div>
      <div className="content-not-printed">
        <OrderHistory order={order!} />
      </div>
    </>
  );
};

export default EditOrder;
