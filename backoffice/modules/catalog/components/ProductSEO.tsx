import { ProductPost } from '../models/ProductPost';
import { UseFormRegister, FieldErrorsImpl } from 'react-hook-form';
import { Input, Text } from '../../../common/items/Input';
type Props = {
  register: UseFormRegister<ProductPost>;
  errors: FieldErrorsImpl<ProductPost>;
};

const ProductSEO = ({ register, errors }: Props) => {
  return (
    <>
      <Input labelText="Meta Title" field="metaTitle" register={register} />
      <Text labelText="Meta Keywords" field="metaKeyword" register={register} />
      <Text labelText="Meta Description" field="metaDescription" register={register} />
    </>
  );
};

export default ProductSEO;
