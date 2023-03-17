import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';

import { Input } from '../../../common/items/Input';
import { ProductAttributeGroup } from '../models/ProductAttributeGroup';

type Props = {
  register: UseFormRegister<ProductAttributeGroup>;
  errors: FieldErrorsImpl<ProductAttributeGroup>;
  productAttributeGroup?: ProductAttributeGroup;
};

const ProductAttributeGroupGeneralInformation = ({
  register,
  errors,
  productAttributeGroup,
}: Props) => {
  return (
    <Input
      labelText="Name"
      field="name"
      defaultValue={productAttributeGroup?.name}
      register={register}
      registerOptions={{
        required: { value: true, message: 'Product attribute group name is required' },
      }}
      error={errors.name?.message}
    />
  );
};

export default ProductAttributeGroupGeneralInformation;
