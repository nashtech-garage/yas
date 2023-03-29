import { UseFormRegister } from 'react-hook-form';
import { Input } from '../../../common/items/Input';
import { Address } from '../../../modules/address/models/AddressModel';

type AddressFormProps = {
  register: UseFormRegister<Address>;
};
const AddressForm = ({ register }: AddressFormProps) => {
  return (
    <div className="container">
      <div className="row">
        <div className="col-lg-6 col-md-12">
          <Input labelText="Contact name" register={register} field="contactName" />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input labelText="Phone number" register={register} field="phone" />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input labelText="Address" register={register} field="addressLine1" />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input labelText="City" register={register} field="city" />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input labelText="Zip code" register={register} field="zipCode" />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input labelText="District" register={register} field="district" />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input labelText="State Or Province" register={register} field="stateOrProvince" />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input labelText="Country" register={register} field="country" />
        </div>
      </div>
    </div>
  );
};

export default AddressForm;
