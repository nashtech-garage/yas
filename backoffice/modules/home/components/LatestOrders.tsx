import { Order } from 'modules/order/models/Order';
import { getLatestOrders } from 'modules/order/services/OrderService';
import moment from 'moment';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';

const LatestOrders = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const res = await getLatestOrders(5);
        setOrders(res);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  let tableContent;

  if (loading) {
    tableContent = (
      <tr>
        <td colSpan={5}>Loading...</td>
      </tr>
    );
  } else if (!orders || orders.length === 0) {
    tableContent = (
      <tr>
        <td colSpan={5}>No Orders available</td>
      </tr>
    );
  } else {
    tableContent = orders.map((order) => (
      <tr key={order.id}>
        <td className="id-column">{order.id}</td>
        <td className="identify-column">{order.email}</td>
        <td>{order.orderStatus}</td>
        <td className="created-on-column">
          {moment(order.createdOn).format('MMMM Do YYYY, h:mm:ss a')}
        </td>
        <td className="details-column">
          <Link href={`/sales/orders/${order.id}/edit`}>
            <button className="btn btn-outline-primary btn-sm" type="button">
              Details
            </button>
          </Link>
        </td>
      </tr>
    ));
  }

  return (
    <>
      <h2 className="text-danger font-weight-bold mb-3">List of the 5 latest Orders</h2>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Email</th>
            <th>Status</th>
            <th>Created On</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>{tableContent}</tbody>
      </Table>
    </>
  );
};

export default LatestOrders;
