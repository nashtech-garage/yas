import type { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { ProductOption } from '../../../../modules/catalog/models/ProductOption';
import {
  getProductOption,
  updateProductOption,
} from '../../../../modules/catalog/services/ProductOptionService';
import { useRouter } from 'next/router';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { PRODUCT_OPTIONS_URL } from '../../../../constants/Common';
import { useUpdatingContext } from '../../../../common/hooks/UseToastContext';
import ProductOptionGeneralInformation from '../../../../modules/catalog/components/ProductOptionGeneralInformation';

const ProductOptionEdit: NextPage = () => {
  const { handleUpdatingResponse } = useUpdatingContext();
  const [isLoading, setLoading] = useState(false);
  const router = useRouter();
  const { id } = router.query;
  const [productOption, setProductOption] = useState<ProductOption>();
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
    if (id) {
      updateProductOption(+id, productOption).then((response) => {
        handleUpdatingResponse(response, PRODUCT_OPTIONS_URL);
      });
    }
  };
  useEffect(() => {
    if (id) {
      setLoading(true);
      getProductOption(+id).then((data) => {
        if (data.id) {
          setProductOption(data);
          setLoading(false);
        } else {
          toast(data?.detail);
          setLoading(false);
          router.push(PRODUCT_OPTIONS_URL);
        }
      });
    }
  }, [id]);
  if (isLoading) return <p>Loading...</p>;
  if (!productOption) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Product Option</h2>
          <form onSubmit={handleSubmit(handleSubmitOption)} name="form">
            <ProductOptionGeneralInformation
              register={register}
              errors={errors}
              productOption={productOption}
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

export default ProductOptionEdit;
