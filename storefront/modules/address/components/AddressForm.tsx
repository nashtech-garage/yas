import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue } from 'react-hook-form';
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
  setValue: UseFormSetValue<Address>;
  errors: FieldErrorsImpl<Address>;
  address: Address | undefined;
  isDisplay?: boolean | true;
};
const AddressForm = ({ register, errors, address, isDisplay, setValue }: AddressFormProps) => {
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
    setValue('countryName', event.target.selectedOptions[0].text);
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
    setValue('stateOrProvinceName', event.target.selectedOptions[0].text);
    getDistricts(event.target.value).then((data) => {
      setDistricts(data);
    });
  };

  return (
    <>
      {' '}
      <div className={`shipping_address_new ${isDisplay ? `` : `d-none`}`}>
        <div className="row">
          <div className="col-lg-6">
            <div className="checkout__input">
              <Input
                labelText="Contact name"
                register={register}
                field="contactName"
                registerOptions={{
                  required: { value: true, message: 'This feild is required' },
                }}
                defaultValue={address?.contactName}
              />
            </div>
          </div>
          <div className="col-lg-6">
            <div className="checkout__input">
              <Input
                labelText="Phone number"
                register={register}
                field="phone"
                registerOptions={{
                  required: { value: true, message: 'This feild is required' },
                }}
                defaultValue={address?.phone}
              />
            </div>
          </div>
        </div>
        <div className="row">
          <div className="col-lg-4">
            <div className="checkout__input">
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
          </div>
          <div className="col-lg-8">
            <div className="checkout__input">
              <div className="mb-3">
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
            </div>
          </div>
        </div>
        <div className="row">
          <div className="col-lg-6">
            <div className="checkout__input">
              <Input
                labelText="City"
                register={register}
                field="city"
                placeholder="Please skip this field if you are not in a city"
                defaultValue={address?.city}
              />
            </div>
          </div>
          <div className="col-lg-6">
            <div className="checkout__input">
              <OptionSelect
                labelText="District"
                register={register}
                field="districtId"
                options={districts}
                placeholder="Select district"
                defaultValue={address?.districtId}
                registerOptions={{
                  required: { value: true, message: 'Please select district' },
                  onChange: (event: any) => {
                    setValue('districtName', event.target.selectedOptions[0].text);
                  },
                }}
              />
            </div>
          </div>
        </div>
        <div className="row mb-5">
          <div className="col-lg-10">
            <div className="checkout__input">
              <Input
                labelText="Address"
                register={register}
                field="addressLine1"
                registerOptions={{
                  required: { value: true, message: 'This feild is required' },
                }}
                defaultValue={address?.addressLine1}
              />
            </div>
          </div>
          <div className="col-lg-2">
            <div className="checkout__input">
              <Input
                labelText="Zip code"
                register={register}
                field="zipCode"
                registerOptions={{
                  required: { value: true, message: 'This feild is required' },
                }}
                defaultValue={address?.zipCode}
              />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default AddressForm;
