import { useRouter } from 'next/router';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import slugify from 'slugify';

import { Input } from '../../../common/items/Input';
import { Brand } from '../models/Brand';
import { SLUG_FIELD_PATTERN } from '../constants/validationPattern';

type Props = {
  register: UseFormRegister<Brand>;
  errors: FieldErrorsImpl<Brand>;
  setValue: UseFormSetValue<Brand>;
  trigger: UseFormTrigger<Brand>;
  brand?: Brand;
};

const BrandGeneralInformation = ({ register, errors, setValue, trigger, brand }: Props) => {
  const router = useRouter();
  const onNameChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    setValue('slug', slugify(event.target.value, { lower: true, strict: true }));
    await trigger('slug');
    await trigger('name');
  };

  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={brand?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Brand name is required' },
          onChange: onNameChange,
        }}
        error={errors.name?.message}
      />
      <Input
        labelText="Slug"
        field="slug"
        defaultValue={brand?.slug}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Slug brand is required' },
          pattern: {
            value: SLUG_FIELD_PATTERN,
            message:
              'Slug must not contain special characters except dash and all characters must be lowercase',
          },
        }}
        error={errors.slug?.message}
      />
    </>
  );
};

export default BrandGeneralInformation;
