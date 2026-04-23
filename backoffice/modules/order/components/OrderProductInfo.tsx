import { Order } from '../models/Order';
import { formatPriceUSD } from 'utils/formatPrice';
import { Stack, Table } from 'react-bootstrap';
import Link from 'next/link';
type Props = {
  order: Order;
};

const OrderProductInfo = ({ order }: Props) => {
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
              <i className="fa fa-bars me-2" aria-hidden="true"></i> Product
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
                    <th>Product name</th>
                    <th>Price </th>
                    <th>Quantity </th>
                    <th>Discount</th>
                    <th>Total</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {order.orderItemVms?.map((item) => (
                    <tr key={item.id}>
                      <td>{item.productName}</td>
                      <td>{formatPriceUSD(item.productPrice)}</td>
                      <td>{item.quantity}</td>
                      <td>{item.discountAmount}</td>
                      <td>{formatPriceUSD(item.productPrice * item.quantity)}</td>
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
                  ))}
                </tbody>
              </Table>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default OrderProductInfo;
