import { PaymentProvider } from '@/modules/payment/models/PaymentProvider';
import { formatPrice } from '@/utils/formatPrice';
import { useEffect, useState } from 'react';
import { getEnabledPaymentProviders } from '../../payment/services/PaymentProviderService';
import { OrderItem } from '../models/OrderItem';
type Props = {
  orderItems: OrderItem[];
  disablePaymentProcess: boolean;
  setPaymentMethod: (method: string | null) => void;
};

const CheckOutDetail = ({ orderItems, disablePaymentProcess, setPaymentMethod }: Props) => {
  const [totalPrice, setTotalPrice] = useState(0);
  const [discountAmount, setDiscountAmount] = useState(0);
  const [taxAmount, setTaxAmount] = useState(0);
  const [disableCheckout, setDisableCheckout] = useState<boolean>(true);
  const [paymentProviders, setPaymentProviders] = useState<PaymentProvider[]>([]);
  const [selectedPayment, setSelectedPayment] = useState<string | null>(null);

  useEffect(() => {
    const fetchPaymentProviders = async () => {
      try {
        const providers = await getEnabledPaymentProviders();
        setPaymentProviders(providers);
      } catch (error) {
        console.error('Error fetching payment providers:', error);
      }
    };

    fetchPaymentProviders();
  }, []);

  useEffect(() => {
    if (paymentProviders.length > 0 && selectedPayment === null) {
      setSelectedPayment(paymentProviders[0].id);
      setPaymentMethod(paymentProviders[0].id);
    }
  }, [paymentProviders]);

  const paymentProviderChange = (id: string) => {
    setSelectedPayment(selectedPayment === id ? null : id);
    setPaymentMethod(selectedPayment === id ? null : id);
  };

  useEffect(() => {
    const totalPrice = orderItems
      .map((item) => calculateProductPrice(item))
      .reduce((accumulator, currentValue) => accumulator + currentValue, 0);
    setTotalPrice(totalPrice);
    const totalDiscountAmount = orderItems
      .map((item) => item.discountAmount ?? 0)
      .reduce((accumulator, discount) => accumulator + discount, 0);
    setDiscountAmount(totalDiscountAmount);

    const totalTaxAmount = orderItems
      .map((item) => item.taxAmount ?? 0)
      .reduce((accumulator, discount) => accumulator + discount, 0);
    setTaxAmount(totalTaxAmount);
  }, [orderItems]);

  const calculateProductPrice = (item: OrderItem) => {
    return item.productPrice * item.quantity - (item.discountAmount ?? 0);
  };

  const handleAgreeTerms = (e: any) => {
    if (e.target.checked) {
      setDisableCheckout(false);
    } else {
      setDisableCheckout(true);
    }
  };

  return (
    <>
      <div className="checkout__order">
        <h4>Your Order</h4>
        <div className="checkout__order__products row">
          <div className="col-lg-6">Products</div>
          <div className="col-lg-4">Quantity</div>
          <div className="col-lg-2">Price</div>
        </div>

        {orderItems?.map((item) => (
          <div key={item.productId} className="row">
            <div className="col-lg-6">{item.productName} </div>
            <div className="col-lg-3 d-flex justify-content-center">{item.quantity}</div>
            <div className="col-lg-3"> {formatPrice(item.productPrice)}</div>
          </div>
        ))}

        <div className="checkout__order__subtotal">
          Discount <span>{formatPrice(discountAmount)}</span>
        </div>
        <div className="checkout__order__subtotal">
          Tax <span>{formatPrice(taxAmount)}</span>
        </div>
        <div className="checkout__order__total">
          Total <span>{formatPrice(totalPrice)}</span>
        </div>
        <div className="checkout__order__payment__providers">
          <h4>Payment Method</h4>
          {paymentProviders.map((provider) => (
            <div
              className={`payment__provider__item ${
                selectedPayment === provider.id ? 'payment__provider__item__active' : ''
              }`}
              key={provider.id}
            >
              <label>
                <input
                  type="radio"
                  name="paymentMethod"
                  value={provider.id}
                  checked={selectedPayment === provider.id}
                  onChange={() => paymentProviderChange(provider.id)}
                />
                {provider.name}
              </label>
            </div>
          ))}
        </div>
        <div className="checkout__input__checkbox">
          <label htmlFor="acc-or">
            Agree to Terms and Conditions
            <input type="checkbox" id="acc-or" onChange={handleAgreeTerms} />
            <span className="checkmark"></span>
          </label>
        </div>
        <p className="mb-2">
          “I agree to the terms and conditions as set out by the user agreement.{' '}
          <a className="text-primary" href="./conditions">
            Learn more
          </a>
          ”
        </p>

        <button
          type="submit"
          className="site-btn"
          disabled={disablePaymentProcess ? true : disableCheckout}
          style={
            disablePaymentProcess || disableCheckout
              ? { cursor: 'not-allowed', backgroundColor: 'gray' }
              : { cursor: 'pointer' }
          }
        >
          Process to Payment
        </button>
      </div>
    </>
  );
};

export default CheckOutDetail;
