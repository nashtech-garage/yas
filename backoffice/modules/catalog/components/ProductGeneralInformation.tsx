import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue } from 'react-hook-form';
import { toast } from 'react-toastify';
import slugify from 'slugify';

import { TaxClass } from '@taxModels/TaxClass';
import { getTaxClasses } from '@taxServices/TaxClassService';
import { CheckBox, Input, Select, TextArea } from '../../../common/items/Input';
import { OptionSelect } from '../../../common/items/OptionSelect';
import TextEditor from '../../../common/items/TextEditor';
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
  const [taxClasses, setTaxClasses] = useState<TaxClass[]>([]);

  useEffect(() => {
    getBrands().then((data) => {
      setBrands(data);
    });
    getTaxClasses().then((data) => {
      setTaxClasses(data);
    });
  }, []);

  useEffect(() => {
    // In case of updating we load product base on id
    if (id) {
      setLoading(true);
      getProduct(+id)
        .then((data) => {
          setProduct(data);
          setValue('taxClassId', data.taxClassId ?? '');
          setValue('description', data.description ?? '');
          setValue('specification', data.specification ?? '');
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
      <TextEditor
        labelText="Description"
        field="description"
        defaultValue={product?.description}
        error={errors.description?.message}
        setValue={(value) => {
          setValue('description', value);
        }}
      />
      <TextArea
        labelText="Short Description"
        field="shortDescription"
        register={register}
        error={errors.shortDescription?.message}
        defaultValue={product?.shortDescription}
      />
      <TextEditor
        labelText="Specification"
        field="specification"
        defaultValue={product?.specification}
        error={errors.specification?.message}
        setValue={(value) => {
          setValue('specification', value);
        }}
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

      <Input
        labelText="Weight"
        field="weight"
        defaultValue={product?.weight}
        register={register}
        error={errors.price?.message}
        type="number"
        registerOptions={{
          required: { value: true, message: 'Product weight is required' },
          validate: { positive: (v) => v > 0 || 'Weight must be greater than 0' },
        }}
      />

      <Select
        labelText="Dimension Unit"
        field="dimensionUnit"
        placeholder="Select Unit"
        defaultValue={product?.dimensionUnit}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Dimension Unit is required' },
        }}
        error={errors.dimensionUnit?.message}
        options={[
          { value: 'CM', label: 'Centimeters' },
          { value: 'INCH', label: 'Inches' },
        ]}
      />

      <Input
        labelText="Length"
        field="length"
        defaultValue={product?.length}
        register={register}
        error={errors.price?.message}
        type="number"
        registerOptions={{
          required: { value: true, message: 'Product length is required' },
          validate: { positive: (v) => v > 0 || 'Length must be greater than 0' },
        }}
      />

      <Input
        labelText="Width"
        field="width"
        defaultValue={product?.width}
        register={register}
        error={errors.price?.message}
        type="number"
        registerOptions={{
          required: { value: true, message: 'Product width is required' },
          validate: { positive: (v) => v > 0 || 'Width must be greater than 0' },
        }}
      />

      <Input
        labelText="Height"
        field="height"
        defaultValue={product?.height}
        register={register}
        error={errors.price?.message}
        type="number"
        registerOptions={{
          required: { value: true, message: 'Product height is required' },
          validate: { positive: (v) => v > 0 || 'Height must be greater than 0' },
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
      <CheckBox
        labelText="Stock Tracking Enabled"
        field="stockTrackingEnabled"
        register={register}
        defaultChecked={product?.stockTrackingEnabled}
      />
      <OptionSelect
        labelText="Tax Class"
        field="taxClassId"
        placeholder="Select tax class"
        options={taxClasses}
        register={register}
        registerOptions={{ required: { value: true, message: 'Please select tax class' } }}
        error={errors.taxClassId?.message}
        defaultValue={product?.taxClassId}
      />
    </>
  );
};

export default ProductGeneralInformation;
