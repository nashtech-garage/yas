import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import { CheckBox } from 'common/items/Input';
import { useEffect, useState } from 'react';
import { Input } from 'common/items/Input';
import { TaxRate } from '../models/TaxRate';
import { TaxClass } from '../models/TaxClass';
import { Country } from '@locationModels/Country';
import { StateOrProvince } from '@locationModels/StateOrProvince';
import { getCountries } from '@locationServices/CountryService';
import { getTaxClasses } from '@taxServices/TaxClassService';
import { getStateOrProvincesByCountry } from '@locationServices/StateOrProvinceService';
import { OptionSelect } from '@commonItems/OptionSelect';

type Props = {
  register: UseFormRegister<TaxRate>;
  errors: FieldErrorsImpl<TaxRate>;
  setValue: UseFormSetValue<TaxRate>;
  trigger: UseFormTrigger<TaxRate>;
  taxRate?: TaxRate;
};

const TaxRateGeneralInformation = ({ register, errors, setValue, trigger, taxRate }: Props) => {
  const [taxClasses, setTaxClasses] = useState<TaxClass[]>([]);
  const [countries, setCountries] = useState<Country[]>([]);
  const [stateOrProvinces, setStateOrProvinces] = useState<StateOrProvince[]>([]);
  const [countryId, setCountryId] = useState<number>(0);

  useEffect(() => {
    getTaxClasses().then((data) => {
      setTaxClasses(data);
    });
  }, []);

  useEffect(() => {
    getCountries().then((data) => {
      setCountries(data);
      setCountryId(data[0].id);
    });
  }, []);

  useEffect(() => {
      getStateOrProvincesByCountry(countryId).then((data) => {
        setStateOrProvinces(data);
      });
  }, [countryId]);

  return (
    <>
      <OptionSelect
        labelText="Tax Class"
        field="taxClassId"
        placeholder="Select Tax Class"
        options={taxClasses}
        register={register}
        registerOptions={{ required: { value: true, message: 'Please select tax class' } }}
        error={errors.taxClassId?.message}
        defaultValue={taxRate?.taxClassId}
      />
      <OptionSelect
        labelText="Country"
        field="countryId"
        placeholder="Select country"
        options={countries}
        register={register}
        registerOptions={{ required: { value: true, message: 'Please select country' } }}
        error={errors.countryId?.message}
        defaultValue={taxRate?.countryId}
      />

      <OptionSelect
        labelText="State Or Province"
        field="stateOrProvinceId"
        placeholder="Select state or province"
        options={stateOrProvinces}
        register={register}
        registerOptions={{ required: { value: true, message: 'Please select state or province' } }}
        error={errors.stateOrProvinceId?.message}
        defaultValue={taxRate?.stateOrProvinceId}
      />
      <Input
        labelText="Rate"
        field="rate"
        defaultValue={taxRate?.rate}
        register={register}
        error={errors.rate?.message}
      />
      <Input
        labelText="Zip Code"
        field="zipCode"
        defaultValue={taxRate?.zipCode}
        register={register}
        error={errors.zipCode?.message}
      />
    </>
  );
};

export default TaxRateGeneralInformation;
