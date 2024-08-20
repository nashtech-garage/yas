import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

import ProductOptionGeneralInformation from '@catalogComponents/ProductOptionGeneralInformation';
import { ProductOption } from '@catalogModels/ProductOption';
import { getProductOption, updateProductOption } from '@catalogServices/ProductOptionService';
import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { toastError } from '@commonServices/ToastService';
import { PRODUCT_OPTIONS_URL, ResponseStatus } from '@constants/Common';

const ProductOptionEdit: NextPage = () => {
  const router = useRouter();
  const { id } = router.query;

  const [isLoading, setLoading] = useState(false);
  const [productOption, setProductOption] = useState<ProductOption>();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductOption>();

  useEffect(() => {
    if (id) {
      setLoading(true);
      getProductOption(+id)
        .then((data) => {
          if (data.id) {
            setProductOption(data);
            setLoading(false);
          } else {
            toastError(data?.detail);
            setLoading(false);
            router.push(PRODUCT_OPTIONS_URL).catch((err) => console.log(err));
          }
        })
        .catch((err) => console.log(err));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleSubmitOption = async (event: ProductOption) => {
    let productOption: ProductOption = {
      id: 0,
      name: event.name,
    };
    if (id) {
      const response = await updateProductOption(+id, productOption);
      if (response.status === ResponseStatus.SUCCESS) {
        router.replace(PRODUCT_OPTIONS_URL).catch((err) => console.log(err));
      }
      handleUpdatingResponse(response);
    }
  };

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
