const CheckOutDetail = () => {
  return (
    <>
      <div className="checkout__order">
        <h4>Your Order</h4>
        <div className="checkout__order__products">
          Products <span>Total</span>
        </div>
        <ul>
          <li>
            Dell XPS 15 9550 <span>$75.99</span>
          </li>
          <li>
            iPad Pro Wi-Fi 4G 128GB <span>$151.99</span>
          </li>
        </ul>
        <div className="checkout__order__subtotal">
          Delivery Fee <span>$750.99</span>
        </div>
        <div className="checkout__order__subtotal">
          Tax <span>$750.99</span>
        </div>
        <div className="checkout__order__total">
          Total <span>$750.99</span>
        </div>
        <div className="checkout__input__checkbox">
          <label htmlFor="acc-or">
            Agree to Terms and Conditions
            <input type="checkbox" id="acc-or" />
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
        <div className="checkout__input__checkbox">
          <label htmlFor="payment">
            COD
            <input type="checkbox" id="payment" />
            <span className="checkmark"></span>
          </label>
        </div>
        <div className="checkout__input__checkbox">
          <label htmlFor="paypal">
            Paypal
            <input type="checkbox" id="paypal" />
            <span className="checkmark"></span>
          </label>
        </div>
        <button type="submit" className="site-btn">
          PLACE ORDER
        </button>
      </div>
    </>
  );
};

export default CheckOutDetail;
