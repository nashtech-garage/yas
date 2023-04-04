import { Input } from 'common/items/Input';
import { FieldErrorsImpl, UseFormRegister } from 'react-hook-form';
import { OrderPost } from '../models/OrderPost';
import { getMyProfile } from '@/modules/profile/services/ProfileService';
import { Customer } from '@/modules/profile/models/Customer';
import { useEffect, useState } from 'react';
import { toastError } from '@/modules/catalog/services/ToastService';
import AddressForm from '@/modules/address/components/AddressFormV2';

type Props = {
  register: UseFormRegister<OrderPost>;
  errors: FieldErrorsImpl<OrderPost>;
};

const AddressCheckoutSection = ({ register, errors }: Props) => {
  const [customer, setCustomer] = useState<Customer>();
  const [addAddressShipping, setAddAddressShipping] = useState<boolean>(false);
  const [addAddressBilling, setAddAddressBilling] = useState<boolean>(false);
  const [sameAddress, setSameAddress] = useState<boolean>(true);

  useEffect(() => {
    getMyProfile()
      .then((data) => {
        setCustomer(data);
      })
      .catch(() => {
        toastError('Please login to checkout');
      });
  }, []);

  const handleSelectBillingAddress = (e: any) => {
    if (e.target.checked) {
      setSameAddress(true);
      setAddAddressBilling(false);
    } else {
      setSameAddress(false);
    }
  };

  return (
    <>
      <h4>Shipping Address</h4>
      <div className="checkout__input">
        <button
          type="button"
          className="btn btn-outline-primary  fw-bold btn-sm me-2"
          onClick={() => setAddAddressShipping(false)}
        >
          Change address <i className="bi bi-plus-circle-fill"></i>
        </button>
        <button
          type="button"
          className={`btn btn-outline-primary  fw-bold btn-sm ${
            addAddressShipping ? `active` : ``
          }`}
          onClick={() => setAddAddressShipping(true)}
        >
          Add new address <i className="bi bi-plus-circle-fill"></i>
        </button>
      </div>
      <div className={`shipping_address ${addAddressShipping ? `d-none` : ``}`}>
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
      <AddressForm isDisplay={addAddressShipping} />

      <h4>Billing Address</h4>
      <div className="row mb-4">
        <div className="col-lg-6">
          <div className="checkout__input__checkbox">
            <label htmlFor="same_as_shipping">
              Selected Shipping Address same as Billing Address
              <input
                type="checkbox"
                id="same_as_shipping"
                onChange={handleSelectBillingAddress}
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
              addAddressBilling ? `active` : ``
            }`}
            onClick={() => {
              setAddAddressBilling(true);
              setSameAddress(false);
            }}
          >
            Add new address <i className="bi bi-plus-circle-fill"></i>
          </button>
        </div>
      </div>
      <AddressForm isDisplay={addAddressBilling} />
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
    </>
  );
};

export default AddressCheckoutSection;
