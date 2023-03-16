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
import CustomToast from '../../../../common/items/CustomToast';

const ProductOptionEdit: NextPage = () => {
  const { toastVariant, toastHeader, showToast, setShowToast, handleUpdatingResponse } =
    useUpdatingContext();
  const router = useRouter();
  const { id } = router.query;
  const [productOption, setProductOption] = useState<ProductOption>();
  const { register, handleSubmit, formState, setValue } = useForm();
  const { errors } = formState;
  const handleSubmitOption = async (event: any) => {
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
      getProductOption(+id).then((data) => {
        setProductOption(data);
        setValue('name', data.name);
      });
    }
  }, [id]);
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Product Option</h2>
          <form onSubmit={handleSubmit(handleSubmitOption)} name="form">
            <div className="mb-3">
              <div className="mb-3">
                <label className="form-label">Name</label>
                <input
                  className="form-control"
                  {...register('name', { required: true })}
                  type="text"
                  id="name"
                  name="name"
                  defaultValue={productOption?.name}
                />
                {errors.name && errors.name.type == 'required' && (
                  <p className="text-danger">Please enter the name product option</p>
                )}
              </div>
            </div>
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
      {showToast && (
        <CustomToast
          variant={toastVariant}
          header={toastHeader}
          show={showToast}
          setShow={setShowToast}
        ></CustomToast>
      )}
    </>
  );
};

export default ProductOptionEdit;
