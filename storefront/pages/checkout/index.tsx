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
import {
  createUserAddress,
  getUserAddressDefault,
} from '@/modules/customer/services/CustomerService';
import ModalAddressList from '@/modules/order/components/ModalAddressList';
import CheckOutAddress from '@/modules/order/components/CheckOutAddress';
import PaymentMethod from '@/modules/order/components/PaymentMethod';
import ShippingMethod from '@/modules/order/components/ShippingMethod';

const phoneRegExp =
  /^((\+[1-9]{1,4}[ -]*)|(\([0-9]{2,3}\)[ -]*)|[0-9]{2,4}[ -]*)?[0-9]{3,4}?[ -]*[0-9]{3,4}?$/;
const addressSchema = yup.object().shape({
  zipCode: yup.string().required('Zip code is required'),
  addressLine1: yup.string().required('Address is required'),
  districtId: yup.string().required('District is required'),
  stateOrProvinceId: yup.string().required('State or province is required'),
  countryId: yup.string().required('Country is required'),
  phone: yup.string().matches(phoneRegExp, 'Phone number is not valid'),
  contactName: yup.string().required('Contact name is required'),
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
    register: registerShippingAddress,
    formState: { errors: errorsShippingAddress },
    watch: watchShippingAddress,
  } = useForm<Address>();

  const {
    register: registerBillingAddress,
    formState: { errors: errorsBillingAddress },
    watch: watchBillingAddress,
  } = useForm<Address>();

  const [loaded, setLoaded] = useState(false);
  const [email, setEmail] = useState<string>('');
  const [paymentMethod, setPaymentMethod] = useState<string>('visa');
  const [orderItems, setOrderItems] = useState<OrderItem[]>([]);
  let order = watch();

  const [shippingAddress, setShippingAddress] = useState<Address>();
  const [billingAddress, setBillingAddress] = useState<Address>();

  const [sameAddress, setSameAddress] = useState<boolean>(true);
  const [addShippingAddress, setAddShippingAddress] = useState<boolean>(false);
  const [addBillingAddress, setAddBillingAddress] = useState<boolean>(false);
  const [showModalShipping, setModalShipping] = useState<boolean>(false);
  const [showModalBilling, setModalBilling] = useState<boolean>(false);
  const handleCloseModalShipping = () => {
    if (shippingAddress?.id == null || shippingAddress.id == undefined) setAddShippingAddress(true);
    setModalShipping(false);
  };
  const handleCloseModalBilling = () => {
    if (billingAddress?.id == shippingAddress?.id) setSameAddress(true);
    setModalBilling(false);
  };

  useEffect(() => {
    getMyProfile()
      .then((res) => {
        if (!loaded) {
          setEmail(res.email);
          loadItems();
          setLoaded(true);
        }
      })
      .catch(() => {
        // toast.error('Load profile failed!, Please login to continue');
        router.push({ pathname: `/login` }); //NOSONAR
      });

    getUserAddressDefault()
      .then((res) => {
        setShippingAddress(res);
        setBillingAddress(res);
      })
      .catch(() => {
        setAddShippingAddress(true);
      });
  }, []);

  const loadItems = () => {
    getCart()
      .then((data) => {
        const cartDetails = data.cartDetails;
        const productIds = cartDetails.map((item) => item.productId);
        getProductThumbnails(productIds)
          .then((results) => {
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
          })
          .catch((err) => {
            console.log('Load product thumbnails fail: ' + err.message);
          });
      })
      .catch((err) => {
        console.log('Load cart failed: ' + err.message);
      });
  };

  const getProductThumbnails = (productIds: number[]) => {
    return getCartProductThumbnail(productIds);
  };

  const handleSelectShippingAddress = (address: Address) => {
    setShippingAddress(address);
  };
  const handleSelectBillingAddress = (address: Address) => {
    setBillingAddress(address);
  };

  const handleSaveNewAddress = (data: Address) => {
    createUserAddress(data).catch((e) => {
      toast.error('Save new address failed!');
    });
  };

  const onSubmitForm: SubmitHandler<Order> = async (data) => {
    let isValidate = true;

    if (addShippingAddress) {
      await addressSchema
        .validate(watchShippingAddress())
        .then(() => {
          handleSaveNewAddress(watchShippingAddress());
          order.shippingAddressPostVm = watchShippingAddress();
        })
        .catch((error) => {
          toast.error(error.message);
          isValidate = false;
        });
    } else if (shippingAddress) {
      order.shippingAddressPostVm = shippingAddress;
    }

    //handle BillingAddress
    if (addBillingAddress) {
      await addressSchema
        .validate(watchBillingAddress())
        .then(() => {
          handleSaveNewAddress(watchBillingAddress());
          order.billingAddressPostVm = watchBillingAddress();
        })
        .catch((error) => {
          toast.error(error.message);
          isValidate = false;
        });
    } else if (sameAddress) {
      order.billingAddressPostVm = order.shippingAddressPostVm;
    } else if (billingAddress) {
      order.billingAddressPostVm = billingAddress;
    }

    if (isValidate) {
      order.email = email;
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
      order.paymentMethod = paymentMethod;
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
    }
  };

  return (
    <>
      <Container>
        <section className="checkout spad">
          <div className="container">
            <div className="checkout__form">
              <form onSubmit={(event) => void handleSubmit(onSubmitForm)(event)}>
                <div className="row">
                  <div className="col-lg-8 col-md-6">
                    <h4>Shipping Address</h4>
                    <div className="checkout__input">
                      <button
                        type="button"
                        className="btn btn-outline-primary  fw-bold btn-sm me-2"
                        onClick={() => {
                          setAddShippingAddress(false);
                          setModalShipping(true);
                        }}
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
                    <CheckOutAddress address={shippingAddress!} isDisplay={!addShippingAddress} />
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
                                if (sameAddress && !addBillingAddress) setModalBilling(true);
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

                    <CheckOutAddress
                      address={billingAddress!}
                      isDisplay={!sameAddress && !addBillingAddress}
                    />

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
                        defaultValue={email}
                        error={errors.note?.message}
                        disabled={true}
                      />
                    </div>
                  </div>
                  <div className="col-lg-4 col-md-6">
                    <PaymentMethod
                      paymentMethod={paymentMethod}
                      setPaymentMethod={setPaymentMethod}
                    />
                    {/* <ShippingMethod /> */}
                    <CheckOutDetail orderItems={orderItems} />
                  </div>
                </div>
              </form>
              <ModalAddressList
                showModal={showModalShipping}
                handleClose={handleCloseModalShipping}
                handleSelectAddress={handleSelectShippingAddress}
              />
              <ModalAddressList
                showModal={showModalBilling}
                handleClose={handleCloseModalBilling}
                handleSelectAddress={handleSelectBillingAddress}
              />
            </div>
          </div>
        </section>
      </Container>
    </>
  );
};

export default Checkout;
