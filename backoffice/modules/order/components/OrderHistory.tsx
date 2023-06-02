import { Order } from '../models/Order';
import { Stack, Table } from 'react-bootstrap';
import Link from 'next/link';
type Props = {
  order: Order;
};

const OrderHistory = ({ order }: Props) => {
  if (!order) return <>No order found</>;
  return (
    <>
      <div className="accordion mb-2" id="accordionProductInfo">
        <div className="accordion-item">
          <h2 className="accordion-header" id="headingOne">
            <button
              className="accordion-button"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseProductInfo"
              aria-expanded="true"
              aria-controls="collapseProductInfo"
            >
              <i className="fa fa-sticky-note-o me-2" aria-hidden="true"></i> Order History
            </button>
          </h2>
          <div
            id="collapseProductInfo"
            className="accordion-collapse collapse show"
            aria-labelledby="headingOne"
            data-bs-parent="#accordionProductInfo"
          >
            <div className="accordion-body">
              <Table striped bordered hover>
                <thead>
                  <tr>
                    <th>Created on </th>
                    <th>Note </th>
                    <th>Attached file </th>
                    <th>Display to customer</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>03/13/2017 4:20:10 AM</td>
                    <td>Order delivered</td>
                    <td>No file attached</td>
                    <td>
                      <i
                        className="fa fa-times text-danger d-flex justify-content-center fs-3"
                        aria-hidden="true"
                      ></i>
                    </td>
                    <td style={{ width: '12%' }}>
                      <Stack direction="vertical" gap={3}>
                        <Link href={`/sales/orders/${order.id}/edit`}>
                          <button className="btn btn-outline-primary btn-sm" type="button">
                            <i className="fa fa-pencil me-2" aria-hidden="true"></i>
                            Edit
                          </button>
                        </Link>
                        <Link href={`/sales/orders/${order.id}/edit`}>
                          <button className="btn btn-outline-danger btn-sm" type="button">
                            <i className="fa fa-trash me-2" aria-hidden="true"></i>
                            Delete
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
    </>
  );
};

export default OrderHistory;
