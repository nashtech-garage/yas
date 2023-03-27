import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import slugify from 'slugify';
import { CheckBox } from '../../../common/items/Input';

import { Input, Switch } from '../../../common/items/Input';
import { Country } from '../models/Country';

type Props = {
  register: UseFormRegister<Country>;
  errors: FieldErrorsImpl<Country>;
  setValue: UseFormSetValue<Country>;
  trigger: UseFormTrigger<Country>;
  country?: Country;
};

const CountryGeneralInformation = ({ register, errors, setValue, trigger, country }: Props) => {
  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={country?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Country name is required' },
        }}
        error={errors.name?.message}
      />
      <Input
        labelText="Code3"
        field="code3"
        defaultValue={country?.code3}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Country name is required' },
        }}
        error={errors.code3?.message}
      />

      <CheckBox
        labelText="isBillingEnabled"
        field="isBillingEnabled"
        register={register}
        defaultChecked={country?.isBillingEnabled}
      />
      <CheckBox
        labelText="IisShippingEnabled"
        field="isShippingEnabled"
        register={register}
        defaultChecked={country?.isShippingEnabled}
      />
      <CheckBox
        labelText="isCityEnabled"
        field="isCityEnabled"
        register={register}
        defaultChecked={country?.isCityEnabled}
      />
      <CheckBox
        labelText="isZipCodeEnabled"
        field="isZipCodeEnabled"
        register={register}
        defaultChecked={country?.isZipCodeEnabled}
      />
      <CheckBox
        labelText="isDistrictEnabled"
        field="isDistrictEnabled"
        register={register}
        defaultChecked={country?.isDistrictEnabled}
      />
    </>
  );
};

export default CountryGeneralInformation;
