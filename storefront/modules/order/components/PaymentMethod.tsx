import { useEffect, useState } from 'react';
import visaImage from '../../../asset/images/visa.png';
import paypal from '@/asset/images/paypall.png';
import mastercard from '@/asset/images/mastercard.png';

type Props = {
  paymentMethod: string;
  setPaymentMethod: (paymentMethod: string) => void;
};

const PaymentMethod = ({ paymentMethod, setPaymentMethod }: Props) => {
  return (
    <div className="checkout__order mb-4">
      <h4>Payment method</h4>
      <div className="payment-method d-flex flex-justify-content gap-4 ms-3">
        <div className="form-check d-flex align-items-center">
          <input
            className="form-check-input me-1"
            type="radio"
            name="paymentMethod"
            id="visa"
            checked={paymentMethod === 'VISA'}
            onChange={() => setPaymentMethod('VISA')}
          />
          <label className="form-check-label" htmlFor="visa">
            <img src={visaImage.src} alt="visa" width={50} />
          </label>
        </div>
        <div className="form-check d-flex align-items-center ">
          <input
            className="form-check-input me-1"
            type="radio"
            name="paymentMethod"
            id="PAYPAL"
            checked={paymentMethod === 'PAYPAL'}
            onChange={() => setPaymentMethod('PAYPAL')}
          />
          <label className="form-check-label" htmlFor="paypal">
            <img src={paypal.src} alt="paypal" width={70} />
          </label>
        </div>
        <div className="form-check d-flex align-items-center">
          <input
            className="form-check-input me-1"
            type="radio"
            name="paymentMethod"
            id="MASTER_CARD"
            checked={paymentMethod === 'MASTER_CARD'}
            onChange={() => setPaymentMethod('MASTER_CARD')}
          />
          <label className="form-check-label" htmlFor="mastercard">
            <img src={mastercard.src} alt="paypal" width={80} />
          </label>
        </div>
      </div>
      <div className="mt-3">
        <div
          className="d-flex align-items-center justify-content-center me-4 btn btn-danger dropdown-toggle "
          onClick={() => {
            setPaymentMethod('other');
          }}
        >
          <input
            className="form-check-input me-2"
            type="radio"
            name="paymentMethod"
            id="other"
            checked={paymentMethod === 'other'}
          />
          <label className="form-check-label" htmlFor="other">
            Other
          </label>
        </div>
      </div>
    </div>
  );
};

export default PaymentMethod;
