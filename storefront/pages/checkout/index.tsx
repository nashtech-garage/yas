import { Container } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useForm, SubmitHandler } from 'react-hook-form';
import { Order } from '@/modules/order/models/Order';
import CheckOutDetail from 'modules/order/components/CheckOutDetail';
import { OrderItem } from '@/modules/order/models/OrderItem';
import { useEffect, useState } from 'react';
import { getCart, getCartProductThumbnail } from '../../modules/cart/services/CartService';
import { useRouter } from 'next/router';
import { getMyProfile } from '@/modules/profile/services/ProfileService';
import { Input } from 'common/items/Input';
import { Address } from '@/modules/address/models/AddressModel';
import AddressForm from '@/modules/address/components/AddressForm';
import { createOrder } from '@/modules/order/services/OrderService';
import * as yup from 'yup';

const addressSchema = yup.object().shape({
  contactName: yup.string().required('Contact name is required'),
  phone: yup.string().required('Phone number is required'),
  countryId: yup.string().required('Country is required'),
  stateOrProvinceId: yup.string().required('State or province is required'),
  districtId: yup.string().required('District is required'),
  addressLine1: yup.string().required('Address is required'),
  zipCode: yup.string().required('Zip code is required'),
});

const Checkout = () => {
  const router = useRouter();
  const {
    handleSubmit,
    register,
    formState: { errors },
    watch,
  } = useForm<Order>();
  const {
    handleSubmit: handleSubmitShippingAddress,
    register: registerShippingAddress,
    formState: { errors: errorsShippingAddress },
    watch: watchShippingAddress,
  } = useForm<Address>();

  const {
    handleSubmit: handleSubmitBillingAddress,
    register: registerBillingAddress,
    formState: { errors: errorsBillingAddress },
    watch: watchBillingAddress,
  } = useForm<Address>();

  const [orderItems, setOrderItems] = useState<OrderItem[]>([]);
  const [loaded, setLoaded] = useState(false);
  const [sameAddress, setSameAddress] = useState<boolean>(true);
  let order = watch();

  const [addShippingAddress, setAddShippingAddress] = useState<boolean>(false);
  const [addBillingAddress, setAddBillingAddress] = useState<boolean>(false);

  useEffect(() => {
    getMyProfile()
      .then(() => {
        if (!loaded) {
          loadItems();
          setLoaded(true);
        }
      })
      .catch(() => {
        router.push({ pathname: `/login` });
      });
  }, []);

  const loadItems = () => {
    getCart().then((data) => {
      const cartDetails = data.cartDetails;
      const productIds = cartDetails.map((item) => item.productId);
      getProductThumbnails(productIds).then((results) => {
        const newItems: OrderItem[] = [];
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

  const handleSaveNewShippingAddress = (data: Address) => {
    // call api to save new address
    console.log('save shipping address');
  };

  const handleSaveNewBillingAddress = (data: Address) => {
    // call api to save new address
    console.log('save billing address');
  };

  const onSubmitForm: SubmitHandler<Order> = async (data) => {
    //handle ShippAddress
    if (addShippingAddress && (await addressSchema.isValid(watchShippingAddress()))) {
      handleSaveNewShippingAddress(watchShippingAddress());
      order.shippingAddressPostVm = watchShippingAddress();
    } else if (addShippingAddress) {
      toast.error("Please fill in shipping address's information");
      return;
    }

    //handle BillingAddress
    if (addBillingAddress && (await addressSchema.isValid(watchBillingAddress()))) {
      handleSaveNewBillingAddress(watchBillingAddress());
      order.billingAddressPostVm = watchBillingAddress();
    } else if (addBillingAddress) {
      toast.error("Please fill in billing address's information");
      return;
    } else if (sameAddress) {
      order.billingAddressPostVm = watchShippingAddress();
    }

    order.email = 'Test@gmail.com';
    order.note = data.note;
    order.tax = 0;
    order.discount = 0;
    order.numberItem = orderItems.reduce((result, item) => result + item.quantity!, 0);
    order.totalPrice = orderItems.reduce(
      (result, item) => result + item.quantity * item.productPrice!,
      0
    );
    order.deliveryFee = 0;
    order.couponCode = '';
    order.deliveryMethod = 'YAS_EXPRESS';
    order.paymentMethod = 'COD';
    order.paymentStatus = 'PENDING';
    order.orderItemPostVms = orderItems;
    console.log(order);
    await createOrder(order)
      .then(() => {
        toast.success('Place order successfully');
      })
      .catch(() => {
        toast.error('Place order failed');
      });
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
                    <h4>Shipping Address</h4>
                    <div className="checkout__input">
                      <button
                        type="button"
                        className="btn btn-outline-primary  fw-bold btn-sm me-2"
                        onClick={() => setAddShippingAddress(false)}
                      >
                        Change address <i className="bi bi-plus-circle-fill"></i>
                      </button>
                      <button
                        type="button"
                        className={`btn btn-outline-primary  fw-bold btn-sm ${
                          addShippingAddress ? `active` : ``
                        }`}
                        onClick={() => setAddShippingAddress(true)}
                      >
                        Add new address <i className="bi bi-plus-circle-fill"></i>
                      </button>
                    </div>
                    <div className={`shipping_address ${addShippingAddress ? `d-none` : ``}`}>
                      <div className="row">
                        <div className="col-lg-6">
                          <div className="checkout__input">
                            <div className="mb-3">
                              <label className="form-label" htmlFor="firstName">
                                Name <span className="text-danger">*</span>
                              </label>
                              <input
                                type="text"
                                className={`form-control`}
                                defaultValue={`This feild will update when "User address book is ready"`}
                                disabled={true}
                              />
                            </div>
                          </div>
                        </div>
                        <div className="col-lg-6">
                          <div className="checkout__input">
                            <div className="mb-3">
                              <label className="form-label" htmlFor="firstName">
                                Phone <span className="text-danger">*</span>
                              </label>
                              <input
                                type="text"
                                className={`form-control`}
                                defaultValue={`This feild will update when "User address book is ready"`}
                                disabled={true}
                              />
                            </div>
                          </div>
                        </div>
                      </div>
                      <div className="checkout__input">
                        <div className="mb-3">
                          <label className="form-label" htmlFor="firstName">
                            Address <span className="text-danger">*</span>
                          </label>
                          <input
                            type="text"
                            className={`form-control`}
                            defaultValue={`This feild will update when "User address book is ready"`}
                            disabled={true}
                          />
                        </div>
                      </div>
                    </div>
                    <AddressForm
                      isDisplay={addShippingAddress}
                      register={registerShippingAddress}
                      errors={errorsShippingAddress}
                      address={undefined}
                    />

                    <h4>Billing Address</h4>
                    <div className="row mb-4">
                      <div className="col-lg-6">
                        <div className="checkout__input__checkbox">
                          <label htmlFor="same_as_shipping">
                            Selected Billing Address same as Shipping Address
                            <input
                              type="checkbox"
                              id="same_as_shipping"
                              onChange={() => {
                                setSameAddress(!sameAddress);
                                setAddBillingAddress(false);
                              }}
                              checked={sameAddress}
                            />
                            <span className="checkmark"></span>
                          </label>
                        </div>
                      </div>
                      <div className="col-lg-6">
                        <button
                          type="button"
                          className={`btn btn-outline-primary  fw-bold btn-sm ${
                            addBillingAddress ? `active` : ``
                          }`}
                          onClick={() => {
                            setAddBillingAddress(true);
                            setSameAddress(false);
                          }}
                        >
                          Add new address <i className="bi bi-plus-circle-fill"></i>
                        </button>
                      </div>
                    </div>
                    <AddressForm
                      isDisplay={addBillingAddress}
                      register={registerBillingAddress}
                      errors={errorsBillingAddress}
                      address={undefined}
                    />
                    <div className="checkout__input">
                      <Input
                        type="text"
                        labelText="Order Notes"
                        field="note"
                        register={register}
                        placeholder="Notes about your order, e.g. special notes for delivery."
                        error={errors.note?.message}
                      />
                    </div>
                    <div className="checkout__input">
                      <Input
                        type="text"
                        labelText="Email"
                        field="email"
                        register={register}
                        defaultValue="Test@gmail.com"
                        error={errors.note?.message}
                        disabled={true}
                      />
                    </div>
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
