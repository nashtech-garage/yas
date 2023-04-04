import { Input } from 'common/items/Input';
import { FieldErrorsImpl, UseFormRegister } from 'react-hook-form';
import { OrderPost } from '../models/OrderPost';
import { getMyProfile } from '@/modules/profile/services/ProfileService';
import { Customer } from '@/modules/profile/models/Customer';
import { useEffect, useState } from 'react';
import { toastError } from '@/modules/catalog/services/ToastService';

type Props = {
  register: UseFormRegister<OrderPost>;
  errors: FieldErrorsImpl<OrderPost>;
};

const OrderForm = ({ register, errors }: Props) => {
  const [customer, setCustomer] = useState<Customer>();

  useEffect(() => {
    getMyProfile()
      .then((data) => {
        setCustomer(data);
      })
      .catch(() => {
        toastError('Please login to checkout');
      });
  }, []);

  return (
    <>
      <div className="row">
        <div className="col-lg-6">
          <div className="checkout__input">
            <div className="mb-3">
              <label className="form-label" htmlFor="firstName">
                First Name <span className="text-danger">*</span>
              </label>
              <input
                type="text"
                className={`form-control`}
                defaultValue={customer?.firstName}
                disabled={true}
              />
            </div>
          </div>
        </div>
        <div className="col-lg-6">
          <div className="checkout__input">
            <div className="mb-3">
              <label className="form-label" htmlFor="firstName">
                Last Name <span className="text-danger">*</span>
              </label>
              <input
                type="text"
                className={`form-control`}
                defaultValue={customer?.lastName}
                disabled={true}
              />
            </div>
          </div>
        </div>
      </div>
      <div className="checkout__input">
        <Input
          labelText="Address"
          field="addressId"
          register={register}
          registerOptions={{
            required: { value: true, message: 'This field is required' },
          }}
          placeholder="Apartment, suite, unite ect, ..v.v"
          error={errors.addressId?.message}
        />
      </div>
      <div className="checkout__input">
        <div className="mb-3">
          <label className="form-label" htmlFor="stateOrProvince">
            State or Province <span className="text-danger">*</span>
          </label>
          <input type="text" className={`form-control`} defaultValue={customer?.firstName} />
        </div>
      </div>
      <div className="checkout__input">
        <div className="mb-3">
          <label className="form-label" htmlFor="district">
            District <span className="text-danger">*</span>
          </label>
          <input type="text" className={`form-control`} defaultValue={customer?.firstName} />
        </div>
      </div>
      <div className="checkout__input">
        <div className="mb-3">
          <label className="form-label" htmlFor="country">
            Country <span className="text-danger">*</span>
          </label>
          <input type="text" className={`form-control`} defaultValue={customer?.firstName} />
        </div>
      </div>
      <div className="checkout__input">
        <div className="mb-3">
          <label className="form-label" htmlFor="postalCode">
            Postcode / ZIP <span className="text-danger">*</span>
          </label>
          <input type="text" className={`form-control`} defaultValue={customer?.firstName} />
        </div>
      </div>
      <div className="row">
        <div className="col-lg-6">
          <div className="checkout__input">
            <Input
              labelText="Phone"
              field="phone"
              register={register}
              registerOptions={{
                required: { value: true, message: 'This field is required' },
              }}
              error={errors.phone?.message}
              defaultValue={customer?.email}
            />
          </div>
        </div>
        <div className="col-lg-6">
          <div className="checkout__input">
            <Input
              type="email"
              labelText="Email"
              field="email"
              register={register}
              registerOptions={{
                required: { value: true, message: 'This field is required' },
              }}
              defaultValue={customer?.firstName}
              error={errors.email?.message}
            />
          </div>
        </div>
      </div>

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

export default OrderForm;
