import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import slugify from 'slugify';
import { Input } from '../../../common/items/Input';
import { SLUG_FIELD_PATTERN } from '../constants/validationPattern';
import { StateOrProvince } from '../models/StateOrProvince';

type Props = {
  register: UseFormRegister<StateOrProvince>;
  errors: FieldErrorsImpl<StateOrProvince>;
  setValue: UseFormSetValue<StateOrProvince>;
  trigger: UseFormTrigger<StateOrProvince>;
  stateOrProvince?: StateOrProvince;
};

const StateOrProvinceGeneralInformation = ({ register, errors, setValue, trigger, stateOrProvince }: Props) => {
  const onNameChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    setValue('slug', slugify(event.target.value, { lower: true, strict: true }));
    await trigger('slug');
    await trigger('name');
    await trigger('isPublish');
  };
  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={stateOrProvince?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'State Or Province name is required' },
          onChange: onNameChange,
        }}
        error={errors.name?.message}
      />
       <Input
              labelText="Code"
              field="code"
              defaultValue={stateOrProvince?.code}
              register={register}
              registerOptions={{
                required: { value: true, message: 'State Or Province name is required' },
                onChange: onNameChange,
              }}
              error={errors.code?.message}
            />
             <Input
                    labelText="Type"
                    field="type"
                    defaultValue={stateOrProvince?.type}
                    register={register}
                    registerOptions={{
                      required: { value: true, message: 'State Or Province name is required' },
                      onChange: onNameChange,
                    }}
                    error={errors.name?.message}
                  />


    </>
  );
};

export default StateOrProvinceGeneralInformation;
