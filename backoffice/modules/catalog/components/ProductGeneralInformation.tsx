import { useEffect, useState } from 'react';
import { Check, Input, Text } from '../../../common/items/Input';
import { Brand } from '../models/Brand';
import { getBrands } from '../services/BrandService';
import { UseFormRegister, FieldErrorsImpl } from 'react-hook-form';
import { ProductPost } from '../models/ProductPost';

type Props = {
  register: UseFormRegister<ProductPost>;
  errors: FieldErrorsImpl<ProductPost>
};

const ProductGeneralInformation = ({ register, errors }: Props) => {
  const [brands, setBrands] = useState<Brand[]>([]);
  useEffect(() => {
    getBrands().then((data) => {
      setBrands(data);
    });
  }, []);
  return (
    <>
      <Input
        labelText="Product Name"
        field="name"
        register={register}
        error={errors.name?.message}
      />
      <Input labelText="Slug" field="slug" register={register} error={errors.slug?.message} />
      <Input labelText="SKU" field="sku" register={register} error={errors.sku?.message} />
      <Input labelText="GTIN" field="gtin" register={register} error={errors.gtin?.message} />
      <Text
        labelText="Description"
        field="description"
        register={register}
        error={errors.description?.message}
      />
      <Text
        labelText="Short Description"
        field="shortDescription"
        register={register}
        error={errors.shortDescription?.message}
      />
      <Text
        labelText="Specification"
        field="specification"
        register={register}
        error={errors.specification?.message}
      />
      <Input
        labelText="Price"
        field="price"
        register={register}
        error={errors.price?.message}
        type="number"
      />
      <div className="mb-3">
        <label className="form-label" htmlFor="brand">
          Brand
        </label>
        <select {...register('brandId')} id="brand" className="form-control">
          <option value="0" hidden >
            Select Brand
          </option>
          {(brands || []).map((brand) => (
            <option value={brand.id} key={brand.id}>
              {brand.name}
            </option>
          ))}
        </select>
      </div>

      <Check labelText="Is Allowed To Order" field="isAllowedToOrder" register={register} />
      <Check labelText="Is Published" field="isPublished" register={register} />
      <Check labelText="Is Featured" field="isFeatured" register={register} />
    </>
  );
};

export default ProductGeneralInformation;
