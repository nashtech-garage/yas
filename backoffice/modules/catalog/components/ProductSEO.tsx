import { FieldErrorsImpl, UseFormRegister } from 'react-hook-form';

import { Input, TextArea } from '../../../common/items/Input';
import { Product } from '../models/Product';
import { ProductPost } from '../models/ProductPost';

type Props = {
  product?: Product;
  register: UseFormRegister<ProductPost>;
  errors: FieldErrorsImpl<ProductPost>;
};

const ProductSEO = ({ product, register, errors }: Props) => {
  return (
    <>
      {product ? (
        <>
          <div>
            <Input labelText="Meta Title" field="metaTitle" register={register} />
            <div className="mb-3">
              <label className="form-label" htmlFor="meta-keyword">
                Meta Keyword
              </label>
              <input
                defaultValue={product.metaKeyword}
                {...register('metaKeyword')}
                className={`form-control ${errors.metaKeyword ? 'border-danger' : ''}`}
                type="text"
                id="meta-keyword"
                name="metaKeyword"
              />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="meta-description">
                Meta Description
              </label>
              <input
                defaultValue={product.metaDescription}
                {...register('metaDescription')}
                type="text"
                className="form-control"
                id="meta-description"
                name="metaDescription"
              />
            </div>
          </div>
        </>
      ) : (
        <>
          <div>
            <Input labelText="Meta Title" field="metaTitle" register={register} />
            <TextArea labelText="Meta Keywords" field="metaKeyword" register={register} />
            <TextArea labelText="Meta Description" field="metaDescription" register={register} />
          </div>
        </>
      )}
    </>
  );
};

export default ProductSEO;
