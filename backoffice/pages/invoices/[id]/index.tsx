import { Order } from 'modules/invoice/models/Order';
import { OrderProduct } from 'modules/invoice/models/OrderProduct';
import { getInvoice } from 'modules/invoice/services/InvoiceService';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';

const InvoiceDetail = () => {
  const [invoice, setInvoice] = useState<Order>();
  const [products, setProducts] = useState<OrderProduct[]>([]);
  const [totalPrice, setTotalPrice] = useState<number>(0);
  const [tax, setTax] = useState<number>(0);
  const router = useRouter();
  const { id } = router.query;
  useEffect(() => {
    if (id) {
      getInvoice(+id).then((data) => {
        console.log(data);
        setInvoice(data);
        setProducts(data.orderItemVms);
        setTotalPrice(data.totalPrice);
        setTax(data.tax);
      });
    }
  }, [id]);

  if (invoice == null && products.length == 0) return <>Invoice is not existed</>;
  return (
    <>
      <h2>Invoice Details: #{invoice?.id}</h2>
      <div>
        <div>
          <h5>Order Status</h5>
          <p>{invoice?.orderStatus}</p>
        </div>
        <div>
          <h5>Payment Status</h5>
          <p>{invoice?.paymentStatus}</p>
        </div>
        <div style={{ marginBottom: '5px' }}>
          <h5>Shipping Address</h5>
          <p>Contact name: {invoice?.shippingAddressVm.contactName}</p>
          <p>Phone: {invoice?.shippingAddressVm.phone}</p>
          <p>City: {invoice?.shippingAddressVm.city}</p>
          <p>Address: {invoice?.shippingAddressVm.addressLine1}</p>
        </div>
        <div>
          <h5>Billing Address</h5>
          <p>Contact name: {invoice?.billingAddressVm.contactName}</p>
          <p>Phone: {invoice?.billingAddressVm.phone}</p>
          <p>City: {invoice?.billingAddressVm.city}</p>
          <p>Address: {invoice?.billingAddressVm.addressLine1}</p>
        </div>
        <div>
          <h5>Products</h5>
          <Table striped bordered hover>
            <thead>
              <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Subtotal</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => {
                return (
                  <tr key={product.productId}>
                    <td>{product.productId}</td>
                    <td>{product.productName}</td>
                    <td>{product.productPrice}</td>
                    <td>{product.quantity}</td>
                    <td>{product.productPrice * product.quantity}</td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </div>
        <div>
          <h5>Total</h5>
          <p>{totalPrice + totalPrice * tax}</p>
        </div>
      </div>
    </>
  );
};

export default InvoiceDetail;
