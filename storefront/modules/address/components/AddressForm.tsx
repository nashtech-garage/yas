import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister } from 'react-hook-form';
import { Input } from '../../../common/items/Input';
import { OptionSelect } from '../../../common/items/OptionSelect';
import { Address } from '../../../modules/address/models/AddressModel';
import { Country } from '../../country/models/Country';
import { District } from '../../district/models/District';
import { StateOrProvince } from '../../stateAndProvince/models/StateOrProvince';
import { useRouter } from 'next/router';
import { getCountries } from '../../country/services/CountryService';
import { getStatesOrProvinces } from '../../stateAndProvince/services/StatesOrProvicesService';
import { getDistricts } from '../../district/services/DistrictService';

type AddressFormProps = {
  register: UseFormRegister<Address>;
  errors: FieldErrorsImpl<Address>;
  address: Address | undefined;
};
const AddressForm = ({ register, errors, address }: AddressFormProps) => {
  const router = useRouter();
  const { id } = router.query;

  const [countries, setCountries] = useState<Country[]>([]);
  const [statesOrProvinces, setStatesOrProvinces] = useState<StateOrProvince[]>([]);
  const [districts, setDistricts] = useState<District[]>([]);

  useEffect(() => {
    getCountries().then((data) => {
      setCountries(data);
    });
  }, []);

  useEffect(() => {
    if (address) {
      getStatesOrProvinces(address.countryId).then((data) => {
        setStatesOrProvinces(data);
      });
      getDistricts(address.stateOrProvinceId).then((data) => {
        setDistricts(data);
      });
    }
  }, [id]);

  const onCountryChange = async (event: any) => {
    getStatesOrProvinces(event.target.value).then((data) => {
      setStatesOrProvinces(data);
      getDistricts(event.target.value).then((data) => {
        if (data) {
          setDistricts(data);
        } else {
          setDistricts([]);
        }
      });
    });
  };

  const onStateOrProvinceChange = async (event: any) => {
    getDistricts(event.target.value).then((data) => {
      setDistricts(data);
    });
  };

  if (id && (countries.length == 0 || statesOrProvinces.length == 0 || districts.length == 0))
    return <></>;
  return (
    <div className="container">
      <div className="row">
        <div className="col-lg-6 col-md-12">
          <Input
            labelText="Contact name"
            register={register}
            field="contactName"
            defaultValue={address?.contactName}
          />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input
            labelText="Phone number"
            register={register}
            field="phone"
            defaultValue={address?.phone}
          />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input
            labelText="Address"
            register={register}
            field="addressLine1"
            defaultValue={address?.addressLine1}
          />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input labelText="City" register={register} field="city" defaultValue={address?.city} />
        </div>
        <div className="col-lg-6 col-md-12">
          <Input
            labelText="Zip code"
            register={register}
            field="zipCode"
            defaultValue={address?.zipCode}
          />
        </div>
        <div className="col-lg-6 col-md-12">
          <OptionSelect
            labelText="Country"
            field="countryId"
            placeholder="Select country"
            options={countries}
            register={register}
            registerOptions={{
              required: { value: true, message: 'Please select country' },
              onChange: onCountryChange,
            }}
            error={errors.countryId?.message}
            defaultValue={address?.countryId}
          />
        </div>
        <div className="col-lg-6 col-md-12">
          <OptionSelect
            labelText="State Or Province"
            register={register}
            field="stateOrProvinceId"
            options={statesOrProvinces}
            placeholder="Select state or province"
            defaultValue={address?.stateOrProvinceId}
            registerOptions={{
              required: { value: true, message: 'Please select state or province' },
              onChange: onStateOrProvinceChange,
            }}
          />
        </div>
        <div className="col-lg-6 col-md-12">
          <OptionSelect
            labelText="District"
            register={register}
            field="districtId"
            options={districts}
            placeholder="Select district"
            defaultValue={address?.districtId}
            registerOptions={{
              required: { value: true, message: 'Please select district' },
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default AddressForm;
