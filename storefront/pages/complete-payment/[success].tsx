import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';
import { Button, Container } from 'react-bootstrap';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import Link from 'next/link';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'Complete Payment',
    url: '/complete-payment',
  },
];

const CompletePayment = () => {
  return (
    <Container>
      <section className="spad">
        <div className="container">
          <BreadcrumbComponent props={crumb} />
          <div className="complete-payment">
            <div className="payment-result">
              <div className="payment-success">
                <h1>
                  <i className="bi bi-check2"></i> YOUR ORDER PAID SUCCESSFUL
                </h1>
              </div>
            </div>
            <div>
              <Link href={'/'}>
                <Button className="back-to-home-btn">
                  <i className="bi bi-house-fill"></i> CONTINUE SHOPPING
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </section>
    </Container>
  );
};

export default CompletePayment;
