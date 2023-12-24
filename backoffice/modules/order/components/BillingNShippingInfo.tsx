import { Order } from '../models/Order';
import Link from 'next/link';
import { Stack, Table } from 'react-bootstrap';
import AddressTable from './AddressTable';
import styles from '@styles/print-hidden.module.css';

type Props = {
  order: Order;
};

const BillingNShippingInfo = ({ order }: Props) => {
  if (!order) return <>No order found</>;
  return (
    <>
      <div className="accordion mb-2" id="accordionAddress">
        <div className="accordion-item">
          <h2 className="accordion-header" id="accordionAddress">
            <button
              className="accordion-button"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseAddress"
              aria-expanded="true"
              aria-controls="collapseAddress"
            >
              <i className="fa fa-truck me-2" aria-hidden="true"></i> Billing & shipping
            </button>
          </h2>
          <div
            id="collapseAddress"
            className="accordion-collapse collapse show"
            aria-labelledby="accordionAddress"
            data-bs-parent="#accordionAddress"
          >
            <div className="accordion-body">
              <div className="border border-1 shadow-sm p-3 mb-3 bg-body rounded">
                <div className="d-flex flex-row gap-2 rounded">
                  <div className="col-6 ">
                    <AddressTable address={order.billingAddressVm} isShowOnGoogleMap={false} />
                  </div>
                  <div className="col-6 ">
                    <AddressTable address={order.shippingAddressVm} isShowOnGoogleMap={true} />
                  </div>
                </div>
              </div>
              <div className="border border-1 shadow-sm p-3 mb-3 bg-body rounded">
                <h4 className="mb-3">Shipment</h4>
                <Table striped bordered hover>
                  <thead>
                    <tr>
                      <th>Shipment #</th>
                      <th>Order #</th>
                      <th>Tracking number</th>
                      <th>Total weight</th>
                      <th>Date shipped</th>
                      <th>Date delivered</th>
                      <th className={`${styles['print-hidden']}`}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>100</td>
                      <td>{order.id}</td>
                      <td></td>
                      <td>2.00 [lb(s)]</td>
                      <td>3/13/2017 4:20:10 AM</td>
                      <td>8-24-2001</td>
                      <td style={{ width: '12%' }} className={`${styles['print-hidden']}`}>
                        <Stack direction="horizontal" gap={3}>
                          <Link href={`/sales/orders/${order.id}/edit`}>
                            <button className="btn btn-outline-primary btn-sm" type="button">
                              <i className="fa fa-eye me-2" aria-hidden="true"></i>
                              View
                            </button>
                          </Link>
                        </Stack>
                      </td>
                    </tr>
                  </tbody>
                </Table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default BillingNShippingInfo;
