type AddressFormProps = {
  isDisplay: boolean;
};
const AddressForm = ({ isDisplay }: AddressFormProps) => {
  return (
    <div className={`shipping_address_new ${isDisplay ? `` : `d-none`}`}>
      <div className="row">
        <div className="col-lg-6">
          <div className="checkout__input">
            <div className="mb-3">
              <label className="form-label" htmlFor="name">
                Contact name<span className="text-danger">*</span>
              </label>
              <input type="text" className={`form-control`} />
            </div>
          </div>
        </div>
        <div className="col-lg-6">
          <div className="checkout__input">
            <div className="mb-3">
              <label className="form-label" htmlFor="phone">
                Phone <span className="text-danger">*</span>
              </label>
              <input type="text" className={`form-control`} />
            </div>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-lg-6">
          <div className="checkout__input">
            <div className="mb-3">
              <label className="form-label" htmlFor={`select-option`}>
                Country <span className="text-danger">*</span>
              </label>
              <select className={`form-select `} style={{ height: 50 }}>
                <option disabled hidden value="">
                  {'Select ...'}
                </option>
              </select>
            </div>
          </div>
        </div>
        <div className="col-lg-6">
          <div className="checkout__input">
            <div className="mb-3">
              <label className="form-label" htmlFor={`select-option`}>
                State or Province <span className="text-danger">*</span>
              </label>
              <select className={`form-select `} style={{ height: 50 }}>
                <option disabled hidden value="">
                  {'Select ...'}
                </option>
              </select>
            </div>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-lg-6">
          <div className="checkout__input">
            <div className="mb-3">
              <label className="form-label" htmlFor={`select-option`}>
                District <span className="text-danger">*</span>
              </label>
              <select className={`form-select `} style={{ height: 50 }}>
                <option disabled hidden value="">
                  {'Select ...'}
                </option>
              </select>
            </div>
          </div>
        </div>
        <div className="col-lg-6">
          <div className="checkout__input">
            <div className="mb-3">
              <label className="form-label" htmlFor="address">
                Postal Code / ZIP <span className="text-danger">*</span>
              </label>
              <input type="text" className={`form-control`} />
            </div>
          </div>
        </div>
      </div>
      <div className="checkout__input">
        <div className="mb-3">
          <label className="form-label" htmlFor="address">
            Address <span className="text-danger">*</span>
          </label>
          <input type="text" className={`form-control`} />
        </div>
      </div>
    </div>
  );
};

export default AddressForm;
