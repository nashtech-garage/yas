import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

import ProductAttributeForm from '@catalogComponents/ProductAttributeForm';
import { ProductAttribute } from '@catalogModels/ProductAttribute';
import { ProductAttributeForm as ProductAttributeFormModel } from '@catalogModels/ProductAttributeForm';
import {
  getProductAttribute,
  updateProductAttribute,
} from '@catalogServices/ProductAttributeService';
import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { PRODUCT_ATTRIBUTE_URL } from '@constants/Common';

const ProductAttributeEdit: NextPage = () => {
  const router = useRouter();
  const { id } = router.query;

  const {
    register,
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductAttributeFormModel>();
  const [productAttribute, setProductAttribute] = useState<ProductAttribute>();
  const [isLoading, setLoading] = useState(true);

  const handleSubmitProductAttribute = (data: ProductAttributeFormModel) => {
    if (productAttribute && productAttribute.id !== undefined) {
      const productAttributeData = {
        ...data,
        productAttributeGroupId:
          data.productAttributeGroupId === '0' ? '' : data.productAttributeGroupId,
      };
      updateProductAttribute(productAttribute.id, productAttributeData).then((response) => {
        handleUpdatingResponse(response);
        router.replace(PRODUCT_ATTRIBUTE_URL);
      });
    }
  };

  useEffect(() => {
    if (id) {
      getProductAttribute(parseInt(id as string))
        .then((data) => {
          setProductAttribute(data);
          setLoading(false);
        })
        .catch((error) => {
          console.log(error);
          router.push(PRODUCT_ATTRIBUTE_URL);
        });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  if (isLoading) return <p>Loading...</p>;

  return (
    <div className="row mt-5">
      <div className="col-md-8">
        <h2>Edit Product Attribute</h2>
        <form onSubmit={handleSubmit(handleSubmitProductAttribute)}>
          <ProductAttributeForm
            errors={errors}
            register={register}
            setValue={setValue}
            productAttribute={productAttribute}
          />
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

export default ProductAttributeEdit;
