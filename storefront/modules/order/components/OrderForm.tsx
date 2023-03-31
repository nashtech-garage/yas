import { Input } from 'common/items/Input';
import { FieldErrorsImpl, UseFormRegister } from 'react-hook-form';
import { OrderPost } from '../models/OrderPost';

type Props = {
  register: UseFormRegister<OrderPost>;
  errors: FieldErrorsImpl<OrderPost>;
};

const OrderForm = ({ register, errors }: Props) => {
  return (
    <>
      <div className="row">
        <div className="col-lg-6">
          <div className="checkout__input">
            <Input
              labelText="First Name"
              field="firstName"
              register={register}
              registerOptions={{
                required: { value: true, message: 'This field is required' },
              }}
              error={errors.firstName?.message}
            />
          </div>
        </div>
        <div className="col-lg-6">
          <div className="checkout__input">
            <Input
              labelText="Last name"
              field="lastName"
              register={register}
              registerOptions={{
                required: { value: true, message: 'This field is required' },
              }}
              error={errors.lastName?.message}
            />
          </div>
        </div>
      </div>
      <div className="checkout__input">
        <Input
          labelText="Address"
          field="address"
          register={register}
          registerOptions={{
            required: { value: true, message: 'This field is required' },
          }}
          error={errors.address?.message}
        />
        <Input
          labelText=""
          field="address"
          register={register}
          placeholder="Apartment, suite, unite ect (optinal)"
        />
      </div>
      <div className="checkout__input">
        <Input
          labelText="State / Province / City"
          field="stateOrProvince"
          register={register}
          registerOptions={{
            required: { value: true, message: 'This field is required' },
          }}
          error={errors.stateOrProvince?.message}
        />
      </div>
      <div className="checkout__input">
        <Input
          labelText="District"
          field="district"
          register={register}
          registerOptions={{
            required: { value: true, message: 'This field is required' },
          }}
          error={errors.district?.message}
        />
      </div>
      <div className="checkout__input">
        <Input
          labelText="Country"
          field="country"
          register={register}
          registerOptions={{
            required: { value: true, message: 'This field is required' },
          }}
          error={errors.country?.message}
        />
      </div>
      <div className="checkout__input">
        <Input
          labelText="Postcode / ZIP"
          field="postalCode"
          register={register}
          registerOptions={{
            required: { value: true, message: 'This field is required' },
          }}
          error={errors.postalCode?.message}
        />
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
