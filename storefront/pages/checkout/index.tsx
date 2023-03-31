import Head from 'next/head';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';
import { Container } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useForm, SubmitHandler } from 'react-hook-form';
import OrderForm from 'modules/order/components/OrderForm';
import { OrderPost } from 'modules/order/models/OrderPost';
import CheckOutDetail from 'modules/order/components/CheckOutDetail';
import Banner from 'common/items/Banner';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'Checkout',
    url: '/checkout',
  },
];

const Checkout = () => {
  const {
    handleSubmit,
    register,
    formState: { errors },
  } = useForm<OrderPost>();

  const onSubmitForm: SubmitHandler<OrderPost> = async (data) => {
    toast.error('Place Order Failed');
  };

  return (
    <>
      <Banner title="Checkout" />
      <Container>
        <section className="checkout spad">
          <div className="container">
            <div className="row">
              <div className="col-lg-12">
                <h6>
                  <span className="icon_tag_alt"></span> Have a coupon? <a href="#">Click here</a>{' '}
                  to enter your code
                </h6>
              </div>
            </div>
            <div className="checkout__form">
              <h4>Billing Details</h4>
              <form onSubmit={handleSubmit(onSubmitForm)}>
                <div className="row">
                  <div className="col-lg-8 col-md-6">
                    <OrderForm register={register} errors={errors} />
                  </div>
                  <div className="col-lg-4 col-md-6">
                    <CheckOutDetail />
                  </div>
                </div>
              </form>
            </div>
          </div>
        </section>
      </Container>
    </>
  );
};

export default Checkout;
