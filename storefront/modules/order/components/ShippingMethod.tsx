import { useEffect, useState } from 'react';
import visaImage from '../../../asset/images/visa.png';
import paypal from '@/asset/images/paypall.png';
import mastercard from '@/asset/images/mastercard.png';

const ShippingMethod = () => {
  useEffect(() => {}, []);

  return (
    <div className="checkout__order mb-4">
      <h4>Shipping Method</h4>
      <div className="payment-method d-flex flex-justify-content gap-4 ms-3">
        <div className="form-check d-flex align-items-center">
          <input className="form-check-input me-1" type="radio" name="shippmentMethod" id="visa" />
          <label className="form-check-label" htmlFor="visa">
            <img src={visaImage.src} alt="visa" width={50} />
          </label>
        </div>
        <div className="form-check d-flex align-items-center ">
          <input
            className="form-check-input me-1"
            type="radio"
            name="shippmentMethod"
            id="paypal"
          />
          <label className="form-check-label" htmlFor="paypal">
            <img src={paypal.src} alt="paypal" width={70} className="" />
          </label>
        </div>
        <div className="form-check d-flex align-items-center">
          <input
            className="form-check-input me-1"
            type="radio"
            name="shippmentMethod"
            id="mastercard"
          />
          <label className="form-check-label" htmlFor="mastercard">
            <img src={mastercard.src} alt="paypal" width={80} />
          </label>
        </div>
      </div>
    </div>
  );
};

export default ShippingMethod;
