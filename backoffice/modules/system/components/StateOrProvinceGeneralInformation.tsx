import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import slugify from 'slugify';
import { Input } from 'common/items/Input';
import { StateOrProvince } from '../models/StateOrProvince';

type Props = {
  register: UseFormRegister<StateOrProvince>;
  errors: FieldErrorsImpl<StateOrProvince>;
  setValue: UseFormSetValue<StateOrProvince>;
  trigger: UseFormTrigger<StateOrProvince>;
  stateOrProvince?: StateOrProvince;
};

const StateOrProvinceGeneralInformation = ({
  register,
  errors,
  setValue,
  trigger,
  stateOrProvince,
}: Props) => {
  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={stateOrProvince?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'State Or Province name is required' },
        }}
        error={errors.name?.message}
      />
      <Input
        labelText="Code"
        field="code"
        defaultValue={stateOrProvince?.code}
        register={register}
        registerOptions={{
          required: { value: true, message: 'The code is required' },
        }}
        error={errors.code?.message}
      />
      <Input
        labelText="Type"
        field="type"
        defaultValue={stateOrProvince?.type}
        register={register}
        registerOptions={{
          required: { value: true, message: 'The type is required' },
        }}
        error={errors.type?.message}
      />
    </>
  );
};

export default StateOrProvinceGeneralInformation;
