import { Container } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useForm, SubmitHandler } from 'react-hook-form';
import OrderForm from 'modules/order/components/OrderForm';
import { OrderPost } from 'modules/order/models/OrderPost';
import CheckOutDetail from 'modules/order/components/CheckOutDetail';
import Banner from 'common/items/Banner';
import { OrderItemPost } from '@/modules/order/models/OrderItemPost';
import { useEffect, useState } from 'react';

type Props = {
  orderItems: OrderItemPost[];
  couponCode?: string;
};

const Checkout = ({ orderItems, couponCode }: Props) => {
  console.log(orderItems);

  const {
    handleSubmit,
    register,
    formState: { errors },
  } = useForm<OrderPost>();

  const onSubmitForm: SubmitHandler<OrderPost> = async (data) => {
    console.log(data);

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
