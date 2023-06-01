import moment from 'moment';
import { Order } from '../models/Order';
import { formatPriceVND } from 'utils/formatPrice';
type Props = {
  order: Order;
};

const OrderBriefInfo = ({ order }: Props) => {
  if (!order) return <>No order found</>;
  return (
    <>
      <div className="accordion mb-2" id="accordionOrderInfo">
        <div className="accordion-item">
          <h2 className="accordion-header" id="headingOne">
            <button
              className="accordion-button"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseOrderInfo"
              aria-expanded="true"
              aria-controls="collapseOrderInfo"
            >
              <i className="fa fa-info me-2" aria-hidden="true"></i> Info
            </button>
          </h2>
          <div
            id="collapseOrderInfo"
            className="accordion-collapse collapse show"
            aria-labelledby="headingOne"
            data-bs-parent="#accordionOrderInfo"
          >
            <div className="accordion-body">
              <div className="border border-1 shadow-sm p-3 mb-3 bg-body rounded">
                <div className="d-flex flex-column">
                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Order Id:{' '}
                    </h6>
                    <p className="col-8 ms-3">{order.id}</p>
                  </div>
                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Created on:{' '}
                    </h6>
                    <p className="col-8 ms-3">
                      {moment(order.createdOn).format('MMMM Do YYYY, h:mm:ss a')}
                    </p>{' '}
                  </div>
                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Customer email:{' '}
                    </h6>
                    <p className="col-8 ms-3">{order.email}</p>
                  </div>
                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Order status:{' '}
                    </h6>
                    <p
                      className={`col-8 ms-3 font-weight-bold ${
                        order.orderStatus == `COMPLETED`
                          ? `text-success`
                          : order.orderStatus == `PENDING`
                          ? `text-warning`
                          : `text-info`
                      }`}
                    >
                      {order.orderStatus}
                    </p>
                  </div>
                </div>
              </div>
              <div className="border border-1 shadow-sm p-3 mb-3 bg-body rounded">
                <div className="d-flex flex-column">
                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Order shipping:
                    </h6>
                    <p className="col-8 ms-3">
                      $0.00 <b>excl tax</b>
                    </p>
                  </div>

                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Order tax:
                    </h6>
                    <p className="col-8 ms-3">$0.00</p>
                  </div>
                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Order total:
                    </h6>

                    <p className="col-8 ms-3">{formatPriceVND(order.totalPrice)}</p>
                  </div>
                  <div className="flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Profit:
                    </h6>
                    <p className="col-8 ms-3"></p>
                  </div>
                  <div className="d-flex justify-content-center me-5">
                    <button className="me-5 btn btn-dark">Edit Order totals</button>
                  </div>
                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Payment method :
                    </h6>
                    <p className="col-8 font-weight-bold ms-3">{order.paymentMethod}</p>
                  </div>
                  <div className="d-flex flex-row">
                    <h6 className="col-4 d-flex flex-row-reverse text-dark font-weight-bold mb-3">
                      Payment status :{' '}
                    </h6>
                    <p className="col-8 font-weight-bold ms-3">{order.paymentStatus}</p>
                  </div>
                  <div className="d-flex justify-content-center me-5">
                    <button className="me-2 btn btn-dark">Refund</button>
                    <button className="me-1 btn btn-dark">Partial refund</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default OrderBriefInfo;
