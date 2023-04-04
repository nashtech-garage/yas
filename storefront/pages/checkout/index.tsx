import { Container } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useForm, SubmitHandler } from 'react-hook-form';
import OrderForm from '@/modules/order/components/AddressCheckoutSection';
import { OrderPost } from 'modules/order/models/OrderPost';
import CheckOutDetail from 'modules/order/components/CheckOutDetail';
import { OrderItemPost } from '@/modules/order/models/OrderItemPost';
import { useEffect, useState } from 'react';
import { getCart, getCartProductThumbnail } from '../../modules/cart/services/CartService';
import AddressCheckoutSection from '@/modules/order/components/AddressCheckoutSection';

const Checkout = () => {
  const [orderItems, setOrderItems] = useState<OrderItemPost[]>([]);

  const [loaded, setLoaded] = useState(false);

  const loadItems = () => {
    getCart().then((data) => {
      const cartDetails = data.cartDetails;
      const productIds = cartDetails.map((item) => item.productId);
      getProductThumbnails(productIds).then((results) => {
        const newItems: OrderItemPost[] = [];
        results.forEach((result) => {
          newItems.push({
            productId: result.id,
            quantity: cartDetails.find((detail) => detail.productId === result.id)?.quantity!,
            productName: result.name,
            productPrice: result.price!,
          });
        });
        setOrderItems(newItems);
      });
    });
  };

  const getProductThumbnails = (productIds: number[]) => {
    return getCartProductThumbnail(productIds);
  };

  useEffect(() => {
    if (!loaded) {
      loadItems();
      setLoaded(true);
    }
  }, []);

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
      <Container>
        <section className="checkout spad">
          <div className="container">
            <div className="checkout__form">
              <form onSubmit={handleSubmit(onSubmitForm)}>
                <div className="row">
                  <div className="col-lg-8 col-md-6">
                    <AddressCheckoutSection register={register} errors={errors} />
                  </div>
                  <div className="col-lg-4 col-md-6">
                    <CheckOutDetail orderItems={orderItems} />
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
