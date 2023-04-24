import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';

import ProductAttributeForm from '@catalogComponents/ProductAttributeForm';
import { ProductAttributeForm as ProductAttributeFormModel } from '@catalogModels/ProductAttributeForm';
import { createProductAttribute } from '@catalogServices/ProductAttributeService';
import { handleCreatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { PRODUCT_ATTRIBUTE_URL } from '@constants/Common';

const ProductAttributeCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductAttributeFormModel>();

  const handleSubmitProductAttribute = (data: ProductAttributeFormModel) => {
    const productAttribute = {
      ...data,
      productAttributeGroupId:
        data.productAttributeGroupId === '0' ? '' : data.productAttributeGroupId,
    };
    createProductAttribute(productAttribute).then((response) => {
      handleCreatingResponse(response);
      router.replace(PRODUCT_ATTRIBUTE_URL);
    });
  };

  return (
    <div className="row mt-5">
      <div className="col-md-8">
        <h2>Create Product Attribute</h2>
        <form onSubmit={handleSubmit(handleSubmitProductAttribute)}>
          <ProductAttributeForm errors={errors} register={register} setValue={setValue} />
          <button className="btn btn-primary" type="submit">
            Save
          </button>
          <Link href="/catalog/product-attributes">
            <button className="btn btn-primary" style={{ marginLeft: '30px' }}>
              Cancel
            </button>
          </Link>
        </form>
      </div>
    </div>
  );
};

export default ProductAttributeCreate;
