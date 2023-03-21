import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue } from 'react-hook-form';
import { toast } from 'react-toastify';
import slugify from 'slugify';

import { CheckBox, Input, TextArea } from '../../../common/items/Input';
import { OptionSelect } from '../../../common/items/OptionSelect';
import { Brand } from '../models/Brand';
import { FormProduct } from '../models/FormProduct';
import { Product } from '../models/Product';
import { getBrands } from '../services/BrandService';
import { getProduct } from '../services/ProductService';

type Props = {
  register: UseFormRegister<FormProduct>;
  errors: FieldErrorsImpl<FormProduct>;
  setValue: UseFormSetValue<FormProduct>;
};

const ProductGeneralInformation = ({ register, errors, setValue }: Props) => {
  //Get ID
  const router = useRouter();
  const { id } = router.query;

  const [brands, setBrands] = useState<Brand[]>([]);

  //Get product detail
  const [product, setProduct] = useState<Product>();
  const [isLoading, setLoading] = useState(false);

  useEffect(() => {
    getBrands().then((data) => {
      setBrands(data);
    });
  }, []);

  useEffect(() => {
    // In case of updating we load product base on id
    if (id) {
      setLoading(true);
      getProduct(+id)
        .then((data) => {
          setProduct(data);
          setLoading(false);
        })
        .catch((error) => {
          toast('Load product failed. Please check the error log');
          location.replace('/catalog/products');
        });
    }
  }, [id]);

  if (isLoading) return <p>Loading...</p>;
  if (id && !product) return <p>No product</p>;
  return (
    <>
      <Input
        labelText="Product name"
        field="name"
        defaultValue={product?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Product name is required' },
          onChange: (event) =>
            setValue('slug', slugify(event.target.value, { lower: true, strict: true })),
        }}
        error={errors.name?.message}
      />
      <Input
        labelText="Slug"
        field="slug"
        defaultValue={product?.slug}
        register={register}
        error={errors.slug?.message}
      />
      <Input
        labelText="SKU"
        field="sku"
        defaultValue={product?.sku}
        register={register}
        error={errors.sku?.message}
      />
      <Input
        labelText="GTIN"
        field="gtin"
        defaultValue={product?.gtin}
        register={register}
        error={errors.gtin?.message}
      />
      <TextArea
        labelText="Description"
        field="description"
        register={register}
        error={errors.description?.message}
        defaultValue={product?.description}
      />
      <TextArea
        labelText="Short Description"
        field="shortDescription"
        register={register}
        error={errors.shortDescription?.message}
        defaultValue={product?.shortDescription}
      />
      <TextArea
        labelText="Specification"
        field="specification"
        register={register}
        error={errors.specification?.message}
        defaultValue={product?.specification}
      />
      <Input
        labelText="Price"
        field="price"
        defaultValue={product?.price}
        register={register}
        error={errors.price?.message}
        type="number"
        registerOptions={{
          required: { value: true, message: 'Product price is required' },
          validate: { positive: (v) => v > 0 || 'Price must be greater than 0' },
        }}
      />

      <OptionSelect
        labelText="Brand"
        field="brandId"
        placeholder="Select brand"
        options={brands}
        register={register}
        registerOptions={{ required: { value: true, message: 'Please select brand' } }}
        error={errors.brandId?.message}
        defaultValue={product?.brandId}
      />

      <CheckBox
        labelText="Is Allowed To Order"
        field="isAllowedToOrder"
        register={register}
        defaultChecked={product?.isAllowedToOrder}
      />
      <CheckBox
        labelText="Is Published"
        field="isPublished"
        register={register}
        defaultChecked={product?.isPublished}
      />
      <CheckBox
        labelText="Is Featured"
        field="isFeatured"
        register={register}
        defaultChecked={product?.isFeatured}
      />
      <CheckBox
        labelText="Is Visible Individually"
        field="isVisibleIndividually"
        register={register}
        defaultChecked={product?.isVisible}
      />
    </>
  );
};

export default ProductGeneralInformation;
