import { Address } from '@/modules/address/models/AddressModel';

type Props = {
  address: Address;
  isDisplay: boolean;
};

const CheckOutAddress = ({ address, isDisplay }: Props) => {
  const addressString = `${address?.addressLine1!}, ${address?.districtName!}, ${address?.city!}, ${address?.countryName!}`;

  return (
    <div className={`address ${isDisplay ? `` : `d-none`}`}>
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
                value={address?.contactName}
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
                value={address?.phone}
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
            value={addressString ?? 'Please choose address'}
            disabled={true}
          />
        </div>
      </div>
    </div>
  );
};

export default CheckOutAddress;
