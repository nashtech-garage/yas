import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';

import { Input } from '../../../common/items/Input';
import { ProductOption } from '../models/ProductOption';

type Props = {
  register: UseFormRegister<ProductOption>;
  errors: FieldErrorsImpl<ProductOption>;
  productOption?: ProductOption;
};

const ProductOptionGeneralInformation = ({ register, errors, productOption }: Props) => {
  return (
    <Input
      labelText="Name"
      field="name"
      defaultValue={productOption?.name}
      register={register}
      registerOptions={{
        required: { value: true, message: 'Product Option name is required' },
      }}
      error={errors.name?.message}
    />
  );
};

export default ProductOptionGeneralInformation;
