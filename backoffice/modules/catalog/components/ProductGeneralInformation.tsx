import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue } from 'react-hook-form';
import slugify from 'slugify';

import { CheckBox, Input, TextArea } from '../../../common/items/Input';
import { OptionSelect } from '../../../common/items/OptionSelect';
import { Brand } from '../models/Brand';
import { ProductPost } from '../models/ProductPost';
import { getBrands } from '../services/BrandService';

type Props = {
  register: UseFormRegister<ProductPost>;
  errors: FieldErrorsImpl<ProductPost>;
  setValue: UseFormSetValue<ProductPost>;
};

const ProductGeneralInformation = ({ register, errors, setValue }: Props) => {
  const [brands, setBrands] = useState<Brand[]>([]);

  useEffect(() => {
    getBrands().then((data) => {
      setBrands(data);
    });
  }, []);

  return (
    <>
      <Input
        labelText="Product name"
        field="name"
        register={register}
        registerOptions={{
          required: { value: true, message: 'Product name is required' },
          onChange: (event) => setValue('slug', slugify(event.target.value, { lower: true })),
        }}
        error={errors.name?.message}
      />
      <Input labelText="Slug" field="slug" register={register} error={errors.slug?.message} />
      <Input labelText="SKU" field="sku" register={register} error={errors.sku?.message} />
      <Input labelText="GTIN" field="gtin" register={register} error={errors.gtin?.message} />
      <TextArea
        labelText="Description"
        field="description"
        register={register}
        error={errors.description?.message}
      />
      <TextArea
        labelText="Short Description"
        field="shortDescription"
        register={register}
        error={errors.shortDescription?.message}
      />
      <TextArea
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
        registerOptions={{ min: { value: 0, message: 'Price must be greater than 0' } }}
      />

      <OptionSelect
        labelText="Brand"
        field="brandId"
        placeholder="Select brand"
        options={brands}
        register={register}
        registerOptions={{ required: { value: true, message: 'Please select brand' } }}
        error={errors.brandId?.message}
      />

      <CheckBox labelText="Is Allowed To Order" field="isAllowedToOrder" register={register} />
      <CheckBox labelText="Is Published" field="isPublished" register={register} />
      <CheckBox labelText="Is Featured" field="isFeatured" register={register} />
      <CheckBox
        labelText="Is Visible Individually"
        field="isVisibleIndividually"
        register={register}
      />
    </>
  );
};

export default ProductGeneralInformation;
