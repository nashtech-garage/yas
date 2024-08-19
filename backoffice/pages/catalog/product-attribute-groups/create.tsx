import type { NextPage } from 'next';
import React from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { useRouter } from 'next/router';
import { ProductAttributeGroup } from '../../../modules/catalog/models/ProductAttributeGroup';
import { PRODUCT_ATTRIBUTE_GROUPS_URL } from '../../../constants/Common';
import { handleCreatingResponse } from '../../../common/services/ResponseStatusHandlingService';
import ProductAttributeGroupGeneralInformation from '../../../modules/catalog/components/ProductAttributeGroupGeneralInformation';
import { createProductAttributeGroup } from '../../../modules/catalog/services/ProductAttributeGroupService';

const ProductAttributeGroupCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductAttributeGroup>();

  const handleSubmitGroup = async (event: ProductAttributeGroup) => {
    let productAttributeGroup: ProductAttributeGroup = {
      id: 0,
      name: event.name,
    };
    let response = await createProductAttributeGroup(productAttributeGroup);

    if (response.status === 201) {
      router.replace(PRODUCT_ATTRIBUTE_GROUPS_URL);
    }

    handleCreatingResponse(response);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create Product Attribute Group</h2>
          <form onSubmit={handleSubmit(handleSubmitGroup)}>
            <ProductAttributeGroupGeneralInformation
              register={register}
              errors={errors}
            ></ProductAttributeGroupGeneralInformation>
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            &emsp;
            <Link href="/catalog/product-attribute-groups">
              <button className="btn btn-outline-secondary">Cancel</button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default ProductAttributeGroupCreate;
