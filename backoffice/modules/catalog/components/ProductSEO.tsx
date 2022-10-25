import { ProductPost } from '../models/ProductPost';
import { UseFormRegister, FieldErrorsImpl } from 'react-hook-form';
import { Input, Text } from '../../../common/items/Input';
import { useRouter } from 'next/router';
import { getProduct } from '../services/ProductService';
import { Product } from '../models/Product';
import { useEffect, useState } from 'react';

type Props = {
  register: UseFormRegister<ProductPost>;
  errors: FieldErrorsImpl<ProductPost>;
};

const ProductSEO = ({ register, errors }: Props) => {
  const router = useRouter();
  const { id } = router.query;

  const [product, setProduct] = useState<Product>();

  useEffect(() => {
    if (id) {
      getProduct(+id).then((data) => {
        setProduct(data);
      });
    }
  }, []);

  if (id) {
    if (!product) {
      return <p>No product</p>;
    } else {
      return (
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
      );
    }
  } else {
    return (
      <>
        <div>
          <Input labelText="Meta Title" field="metaTitle" register={register} />
          <Text labelText="Meta Keywords" field="metaKeyword" register={register} />
          <Text labelText="Meta Description" field="metaDescription" register={register} />
        </div>
      </>
    );
  }
};

export default ProductSEO;
