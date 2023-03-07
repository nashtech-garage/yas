import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import slugify from 'slugify';

import { Input } from '../../../common/items/Input';
import { Brand } from '../models/Brand';
import { getBrand } from '../services/BrandService';
import { SLUG_FIELD_PATTERN } from '../constants/validationPattern';

type Props = {
  register: UseFormRegister<Brand>;
  errors: FieldErrorsImpl<Brand>;
  setValue: UseFormSetValue<Brand>;
  trigger: UseFormTrigger<Brand>;
};

const BrandGeneralInformation = ({ register, errors, setValue, trigger }: Props) => {
  const router = useRouter();
  const { id } = router.query;
  const [brand, setBrand] = useState<Brand>();
  const [isLoading, setLoading] = useState(false);

  useEffect(() => {
    if (id) {
      setLoading(true);
      getBrand(+id).then((data) => {
        setBrand(data);
        setLoading(false);
      });
    }
  }, [id]);

  const onNameChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    setValue('slug', slugify(event.target.value, { lower: true, strict: true }));
    await trigger('slug');
    await trigger('name');
  };
  if (isLoading) return <p>Loading...</p>;
  if (!brand) {
    return <></>;
  } else {
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
  }
};

export default BrandGeneralInformation;
