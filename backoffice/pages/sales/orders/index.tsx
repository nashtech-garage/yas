import type { NextPage } from 'next';
import { useState, useEffect } from 'react';
import { Stack, Table } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import moment from 'moment';
import { useForm, SubmitHandler } from 'react-hook-form';
import queryString from 'query-string';
import { exportCsvFile, getOrders } from 'modules/order/services/OrderService';
import { OrderSearchForm } from 'modules/order/models/OrderSearchForm';
import { DEFAULT_PAGE_SIZE } from '@constants/Common';
import { Order } from 'modules/order/models/Order';
import OrderSearch from 'modules/order/components/OrderSearch';
import { formatPriceVND } from 'utils/formatPrice';
import Link from 'next/link';
import { toastError, toastSuccess } from '@commonServices/ToastService';

const Orders: NextPage = () => {
  const { register, watch, handleSubmit } = useForm<OrderSearchForm>();
  const [isLoading, setLoading] = useState(false);

  const [orderList, setOrderList] = useState<Order[]>([]);
  const [orderIdList, setOrderIdList] = useState<number[]>([]);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(1);
  const orderPageSize = DEFAULT_PAGE_SIZE;

  const watchAllFields = watch(); // when pass nothing as argument, you are watching everything

  const handleGetOrders = () => {
    const params = queryString.stringify({
      ...watchAllFields,
      pageNo: pageNo,
      pageSize: orderPageSize,
      createdFrom: moment(watchAllFields.createdFrom).format(),
      createdTo: moment(watchAllFields.createdTo).format(),
    });

    getOrders(params)
      .then((res) => {
        setOrderList(res.orderList);
        setTotalPage(res.totalPages);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    setLoading(true);
    handleGetOrders();
    setLoading(false);
  }, [pageNo]);

  useEffect(() => {
    setLoading(true);
    handleGetOrders();
    setLoading(false);
  }, []);

  const onSubmitSearch: SubmitHandler<OrderSearchForm> = async (data) => {
    handleGetOrders();
    setPageNo(0);
  };
  const handlePageChange = ({ selected }: any) => {
    setPageNo(selected);
  };

  const handleExportCsv = (idList: number[] | null) => {
    const params = idList
      ? queryString.stringify({
          orderIdList: idList,
        })
      : queryString.stringify({
          orderIdList: orderIdList,
        });
    exportCsvFile(params)
      .then((res) => {
        const downloadLink = document.createElement('a');
        downloadLink.href = res.url; // URL of the file

        // Simulate a click event to trigger the download
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);

        toastSuccess('Export CSV successfully!');
      })
      .catch((ex) => {
        toastError('Export CSV failed!');
        console.log(ex);
      });
  };

  const handleClickCheckbox = (value: number) => {
    const isExist = orderIdList.includes(value);

    console.log('checked', value, isExist);

    if (isExist) {
      setOrderIdList(orderIdList.filter((id) => id !== value));
    } else {
      setOrderIdList([...orderIdList, value]);
    }
  };

  const handleClickCheckAll = (e: any) => {
    const { checked } = e.target;
    const newOrderIdList = checked
      ? orderList
          .filter((order) => typeof order.id === 'number') // Filter out undefined values
          .map((order) => order.id as number)
      : [];

    setOrderIdList(newOrderIdList);

    //set all checkbox to checked
    const checkboxes = document.querySelectorAll<HTMLInputElement>('input[type="checkbox"]');
    checkboxes.forEach((checkbox) => {
      checkbox.checked = checked;
    });
  };

  if (isLoading) return <p>Loading...</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-6">
          <h2 className="text-danger font-weight-bold mb-3">Order Management</h2>
        </div>
        <div className="col-md-6 text-right">
          <div className="btn-group me-2">
            <button type="button" className="btn btn-success">
              <i className="fa fa-download" aria-hidden="true"></i> Export
            </button>
            <button type="button" className="btn btn-success " data-bs-toggle="dropdown">
              <i className="fa fa-caret-down" aria-hidden="true"></i>
            </button>
            <ul className="dropdown-menu">
              <li>
                <a
                  className="dropdown-item"
                  href="#"
                  onClick={() => {
                    handleExportCsv(
                      orderList
                        .filter((order) => typeof order.id === 'number') // Filter out undefined values
                        .map((order) => order.id as number)
                    );
                  }}
                >
                  Export to Excel (all found)
                </a>
              </li>
              <li>
                <a className="dropdown-item" href="#" onClick={() => handleExportCsv(null)}>
                  Export to Excel (selected)
                </a>
              </li>
            </ul>
          </div>

          <button type="button" className="btn btn-warning me-2">
            <i className="fa fa-upload me-2" aria-hidden="true"></i> Import
          </button>
          <button type="button" className="btn btn-info me-2">
            <i className="fa fa-print me-2" aria-hidden="true"></i> Print PDF Invoice
          </button>
        </div>
      </div>
      {/* Filter */}

      <div className="accordion" id="accordionExample">
        <div className="accordion-item">
          <h2 className="accordion-header" id="headingOne">
            <button
              className="accordion-button"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseOne"
              aria-expanded="true"
              aria-controls="collapseOne"
            >
              Search
            </button>
          </h2>
          <div
            id="collapseOne"
            className="accordion-collapse collapse show"
            aria-labelledby="headingOne"
            data-bs-parent="#accordionExample"
          >
            <div className="accordion-body">
              <form onSubmit={(event) => void handleSubmit(onSubmitSearch)(event)}>
                <OrderSearch register={register} />
              </form>
            </div>
          </div>
        </div>
      </div>

      <Table striped bordered hover>
        <thead>
          <tr>
            <th className="d-flex justify-content-center">
              <input
                className="form-check-input"
                type="checkbox"
                checked={orderIdList.length === orderList?.length}
                id="checkAll"
                onClick={(Event) => handleClickCheckAll(Event)}
                disabled={orderList === undefined || orderList == null}
              />
            </th>
            <th>Order Id</th>
            <th>Order status</th>
            <th>Payment status</th>
            <th>Shipping status</th>
            <th>Phone</th>
            <th>Created on</th>
            <th>Order Total</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(orderList) &&
            orderList.map((order) => (
              <tr key={order.id}>
                <td className="d-flex justify-content-center">
                  <input
                    className="form-check-input mb-3"
                    type="checkbox"
                    onChange={(e) => handleClickCheckbox(Number(e.target.value))}
                    value={order.id}
                    id={`selectOrder${order.id}`}
                  />
                </td>
                <td>{order.id}</td>
                <td>
                  <span
                    className={
                      order.orderStatus == `COMPLETED`
                        ? `border border-success btn-sm text-success`
                        : order.orderStatus == `PENDING`
                        ? `border border-warning btn-sm text-warning`
                        : `border border-info btn-sm text-info`
                    }
                  >
                    {order.orderStatus}
                  </span>
                </td>
                <td>
                  <div
                    className={
                      order.paymentStatus &&
                      (order.paymentStatus == `COMPLETED`
                        ? `border border-success btn-sm text-success`
                        : order.paymentStatus == `PENDING`
                        ? `border border-warning btn-sm text-warning`
                        : `border border-info btn-sm text-info`)
                    }
                  >
                    {order.paymentStatus}
                  </div>
                </td>
                <td>
                  <span
                    className={
                      order.deliveryStatus &&
                      (order.deliveryStatus == `COMPLETED`
                        ? `border border-success btn-sm text-success`
                        : order.deliveryStatus == `PENDING`
                        ? `border border-warning btn-sm text-warning`
                        : `border border-info btn-sm text-info`)
                    }
                  >
                    {order.deliveryStatus}
                  </span>
                </td>
                <td>{order.billingAddressVm.phone}</td>
                <td>{moment(order.createdOn).format('MMMM Do YYYY, h:mm:ss a')}</td>
                <td>{formatPriceVND(order.totalPrice)}</td>
                <td style={{ width: '10%' }}>
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
            ))}
        </tbody>
      </Table>

      <ReactPaginate
        forcePage={pageNo}
        previousLabel={'Previous'}
        nextLabel={'Next'}
        pageCount={totalPage}
        onPageChange={handlePageChange}
        containerClassName={'pagination-container'}
        previousClassName={'previous-btn'}
        nextClassName={'next-btn'}
        disabledClassName={'pagination-disabled'}
        activeClassName={'pagination-active'}
      />
    </>
  );
};

export default Orders;
