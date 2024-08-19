import type { NextPage } from 'next';
import React from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { createProductOption } from '../../../modules/catalog/services/ProductOptionService';
import { ProductOption } from '../../../modules/catalog/models/ProductOption';
import { useRouter } from 'next/router';
import { PRODUCT_OPTIONS_URL } from '../../../constants/Common';
import { handleCreatingResponse } from '../../../common/services/ResponseStatusHandlingService';
import ProductOptionGeneralInformation from '../../../modules/catalog/components/ProductOptionGeneralInformation';

const ProductOptionCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductOption>();
  const handleSubmitOption = async (event: ProductOption) => {
    let productOption: ProductOption = {
      id: 0,
      name: event.name,
    };
    let response = await createProductOption(productOption);

    if (response.status === 201) {
      router.replace(PRODUCT_OPTIONS_URL);
    }

    handleCreatingResponse(response);

  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create Product Option</h2>
          <form onSubmit={handleSubmit(handleSubmitOption)}>
            <ProductOptionGeneralInformation
              register={register}
              errors={errors}
            ></ProductOptionGeneralInformation>
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            &emsp;
            <Link href="/catalog/product-options">
              <button className="btn btn-outline-secondary">Cancel</button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default ProductOptionCreate;
