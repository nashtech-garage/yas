import { Order } from 'modules/invoice/models/Order';
import { OrderItemList } from 'modules/invoice/models/OrderList';
import { getInvoices } from 'modules/invoice/services/InvoiceService';
import moment from 'moment';
import { NextPage } from 'next';
import { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';
import Link from 'next/link';
import { INVOICE_URL } from '@constants/Common';

const InvoiceList: NextPage = () => {
  const [content, setContent] = useState<OrderItemList[]>([]);

  useEffect(() => {
    getInvoices().then((data) => {
      setContent(data.orderContent);
    });
  }, []);

  if (content.length == 0) return <>Order List is empty</>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Invoices</h2>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Id</th>
            <th>Created On</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {content.map((order) => (
            <tr key={order.id}>
              <td>{order.id}</td>
              <td>{moment(order.createdOn).format('DD/MM/YYYY hh:mm:ss')}</td>
              <td>{order.orderStatus}</td>
              <td>
                <Link href={`${INVOICE_URL}/${order.id}`}>
                  <button className="btn btn-outline-primary btn-sm" type="button">
                    Detail
                  </button>
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      {/* <ReactPaginate
        forcePage={pageNo}
        previousLabel={'Previous'}
        nextLabel={'Next'}
        pageCount={totalPage}
        onPageChange={changePage}
        containerClassName={'paginationBtns'}
        previousLinkClassName={'previousBtn'}
        nextClassName={'nextBtn'}
        disabledClassName={'paginationDisabled'}
        activeClassName={'paginationActive'}
      /> */}
    </>
  );
};

export default InvoiceList;
