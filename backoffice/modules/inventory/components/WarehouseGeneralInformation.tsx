import { OptionSelect } from '@commonItems/OptionSelect';
import { Country } from '@locationModels/Country';
import { District } from '@locationModels/District';
import { StateOrProvince } from '@locationModels/StateOrProvince';
import { getCountries } from '@locationServices/CountryService';
import { getDistricts } from '@locationServices/DistrictService';
import { getStatesOrProvinces } from '@locationServices/StateOrProvinceService';
import { Input } from 'common/items/Input';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import { WarehouseDetail } from '../models/WarehouseDetail';

type Props = {
  register: UseFormRegister<WarehouseDetail>;
  errors: FieldErrorsImpl<WarehouseDetail>;
  setValue: UseFormSetValue<WarehouseDetail>;
  trigger: UseFormTrigger<WarehouseDetail>;
  warehouseDetail?: WarehouseDetail;
};

const WarehouseGeneralInformation = ({
  register,
  errors,
  setValue,
  trigger,
  warehouseDetail,
}: Props) => {
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
    if (warehouseDetail) {
      getStatesOrProvinces(warehouseDetail.countryId).then((data) => {
        setStatesOrProvinces(data);
      });
      getDistricts(warehouseDetail.stateOrProvinceId).then((data) => {
        setDistricts(data);
      });
    }
  }, [id]);

  const onCountryChange = async (event: any) => {
    getStatesOrProvinces(event.target.value).then((data) => {
      setStatesOrProvinces(Array.isArray(data) ? data : [data]);
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
  if (id && !warehouseDetail) return <p>No warehouse</p>;
  if (id && (countries.length == 0 || statesOrProvinces.length == 0 || districts.length == 0))
    return <></>;
  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={warehouseDetail?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Name is required' },
        }}
        error={errors.name?.message}
      />
      <Input
        labelText="Contact Name"
        field="contactName"
        defaultValue={warehouseDetail?.contactName}
        register={register}
        error={errors.contactName?.message}
      />
      <Input
        labelText="Phone"
        field="phone"
        defaultValue={warehouseDetail?.phone}
        register={register}
        error={errors.phone?.message}
      />
      <Input
        labelText="Address Line 1"
        field="addressLine1"
        defaultValue={warehouseDetail?.addressLine1}
        register={register}
        error={errors.addressLine1?.message}
      />
      <Input
        labelText="Address Line 2"
        field="addressLine2"
        defaultValue={warehouseDetail?.addressLine2}
        register={register}
        error={errors.addressLine2?.message}
      />
      <Input
        labelText="City"
        field="city"
        defaultValue={warehouseDetail?.city}
        register={register}
        error={errors.city?.message}
      />
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
        defaultValue={warehouseDetail?.countryId}
      />

      <OptionSelect
        labelText="State or province"
        field="stateOrProvinceId"
        placeholder="Select state or province"
        options={statesOrProvinces}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Please select state or ' },
          onChange: onStateOrProvinceChange,
        }}
        error={errors.stateOrProvinceId?.message}
        defaultValue={warehouseDetail?.stateOrProvinceId}
      />

      <OptionSelect
        labelText="District"
        register={register}
        field="districtId"
        options={districts}
        placeholder="Select district"
        registerOptions={{
          required: { value: true, message: 'Please select district' },
        }}
        defaultValue={warehouseDetail?.districtId}
      />

      <Input
        labelText="Postal Code"
        field="zipCode"
        defaultValue={warehouseDetail?.zipCode}
        register={register}
        error={errors.zipCode?.message}
      />
    </>
  );
};

export default WarehouseGeneralInformation;
