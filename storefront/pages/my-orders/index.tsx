import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';
import { EOrderStatus } from '@/modules/order/models/EOrderStatus';
import { Container, Tab, Tabs } from 'react-bootstrap';
import OrderStatusTab from '@/modules/order/components/OrderStatusTab';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { getOrderStatusTitle } from '@/utils/orderUtil';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'My Orders',
    url: '#',
  },
];

const MyOrders = () => {
  const orderStatuses = Object.keys(EOrderStatus);

  return (
    <>
      <Container>
        <section className="my-order spad">
          <div className="container">
            <BreadcrumbComponent props={crumb} />
            <Tabs defaultActiveKey={'ALL'} className="mb-3">
              <Tab key="ALL" eventKey={'ALL'} title={getOrderStatusTitle(null)}>
                <OrderStatusTab orderStatus={null}></OrderStatusTab>
              </Tab>
              {orderStatuses.map((orderStatus) => (
                <Tab
                  key={orderStatus}
                  eventKey={orderStatus}
                  title={getOrderStatusTitle(orderStatus as EOrderStatus)}
                >
                  <OrderStatusTab orderStatus={orderStatus as EOrderStatus}></OrderStatusTab>
                </Tab>
              ))}
            </Tabs>
          </div>
        </section>
      </Container>
    </>
  );
};

export default MyOrders;
