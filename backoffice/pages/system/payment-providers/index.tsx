import type { NextPage } from 'next';
import { useState, useEffect } from 'react';
import { Stack, Table } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import moment from 'moment';
import { useForm, SubmitHandler } from 'react-hook-form';
import queryString from 'query-string';
import { getOrders } from 'modules/order/services/OrderService';
import { OrderSearchForm } from 'modules/order/models/OrderSearchForm';
import { DEFAULT_PAGE_SIZE } from '@constants/Common';
import { Order } from 'modules/order/models/Order';
import OrderSearch from 'modules/order/components/OrderSearch';
import { formatPriceVND } from 'utils/formatPrice';
import Link from 'next/link';
import { Payment } from 'modules/payment/models/Payment';

const temp_payment_data = [
  { id: 1, name: 'Paypal', isEnabled: true },
  { id: 2, name: 'Cash on Delivery', isEnabled: true },
  { id: 3, name: 'Stripe', isEnabled: false },
];

const PaymentProviders: NextPage = () => {
  const [paymentList, setpaymentList] = useState<Payment[]>([]);
  const [isLoading, setLoading] = useState(false);

  if (isLoading) return <p>Loading...</p>;
  return (
    <>
      <div className="row mt-3">
        <div className="col-md-6">
          <h2 className="text-danger font-weight-bold mb-3">Payment Providers</h2>
        </div>
      </div>
      <Table striped hover>
        <thead>
          <tr>
            <th>Payment method</th>
            <th>Is enabled</th>
            <th>Configure</th>
          </tr>
        </thead>
        <tbody>
          {' '}
          {Array.isArray(temp_payment_data) &&
            temp_payment_data.map((item) => (
              <tr key={item.id}>
                <td>{item.name}</td>
                <td>
                  {item.isEnabled ? (
                    <i className="fa fa-check text-success ms-4" aria-hidden="true"></i>
                  ) : (
                    <i className="fa fa-times text-danger  ms-4" aria-hidden="true"></i>
                  )}
                </td>
                <td style={{ width: '10%' }}>
                  <Stack direction="horizontal" gap={3}>
                    <button className="btn btn-outline-primary btn-sm" type="button">
                      <i className="fa fa-play" aria-hidden="true"></i>
                    </button>
                    <Link href={`/system/payment-providers/${item.id}/edit`}>
                      <button className="btn btn-outline-secondary btn-sm" type="button">
                        <i className="fa fa-cog" aria-hidden="true"></i>
                      </button>
                    </Link>
                  </Stack>
                </td>
              </tr>
            ))}
        </tbody>
      </Table>
    </>
  );
};

export default PaymentProviders;
