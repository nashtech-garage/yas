import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue } from 'react-hook-form';

import { ProductAttribute } from '@catalogModels/ProductAttribute';
import { ProductAttributeForm as ProductAttributeFormModel } from '@catalogModels/ProductAttributeForm';
import { ProductAttributeGroup } from '@catalogModels/ProductAttributeGroup';
import { getProductAttributeGroups } from '@catalogServices/ProductAttributeGroupService';
import { Input } from '@commonItems/Input';

type ProductAttributeFormProps = {
  register: UseFormRegister<ProductAttributeFormModel>;
  errors: FieldErrorsImpl<ProductAttributeFormModel>;
  setValue: UseFormSetValue<ProductAttributeFormModel>;
  productAttribute?: ProductAttribute;
};

const ProductAttributeForm = ({
  register,
  errors,
  setValue,
  productAttribute = {
    id: 0,
    name: '',
    productAttributeGroup: '',
  },
}: ProductAttributeFormProps) => {
  const [productAttributeGroup, setProductAttributeGroup] = useState<ProductAttributeGroup[]>([]);

  useEffect(() => {
    getProductAttributeGroups().then((data) => {
      setProductAttributeGroup(data);
    });
  }, []);

  useEffect(() => {
    if (productAttributeGroup.length > 0) {
      setValue('name', productAttribute.name);
      setValue(
        'productAttributeGroupId',
        productAttributeGroup
          .find((e) => e.name === productAttribute.productAttributeGroup)
          ?.id.toString() ?? '0'
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productAttributeGroup]);

  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={productAttribute.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Product attribute name is required' },
          onChange: (event) => setValue('name', event.target.value),
        }}
        error={errors.name?.message}
      />
      <div className="mb-3">
        <label className="form-label" htmlFor="productAG">
          Group
        </label>
        <select
          className="form-control"
          name="groupId"
          id="groupId"
          onChange={(event) => setValue('productAttributeGroupId', event.target.value)}
        >
          <option value={0}></option>
          {productAttributeGroup.map((productAttributeGroup) => (
            <option
              value={productAttributeGroup.id}
              key={productAttributeGroup.id}
              selected={productAttributeGroup.name === productAttribute.productAttributeGroup}
            >
              {productAttributeGroup.name}
            </option>
          ))}
        </select>
      </div>
    </>
  );
};

export default ProductAttributeForm;
