import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import { CheckBox } from 'common/items/Input';
import { Input } from 'common/items/Input';
import { TaxClass } from '../models/TaxClass';

type Props = {
  register: UseFormRegister<TaxClass>;
  errors: FieldErrorsImpl<TaxClass>;
  setValue: UseFormSetValue<TaxClass>;
  trigger: UseFormTrigger<TaxClass>;
  taxClass?: TaxClass;
};

const TaxClassGeneralInformation = ({ register, errors, setValue, trigger, taxClass }: Props) => {
  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={taxClass?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Tax Class name is required' },
        }}
        error={errors.name?.message}
      />
    </>
  );
};

export default TaxClassGeneralInformation;
