import type { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { useRouter } from 'next/router';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { ProductAttributeGroup } from '../../../../modules/catalog/models/ProductAttributeGroup';
import {
  getProductAttributeGroup,
  updateProductAttributeGroup,
} from '../../../../modules/catalog/services/ProductAttributeGroupService';
import { PRODUCT_ATTRIBUTE_GROUPS_URL } from '../../../../constants/Common';
import { useUpdatingContext } from '../../../../common/hooks/UseToastContext';
import ProductAttributeGroupGeneralInformation from '../../../../modules/catalog/components/ProductAttributeGroupGeneralInformation';
import CustomToast from '../../../../common/items/CustomToast';

const ProductAttributeGroupEdit: NextPage = () => {
  const { toastVariant, toastHeader, showToast, setShowToast, handleUpdatingResponse } =
    useUpdatingContext();
  const router = useRouter();
  const { id } = router.query;
  const [productAttributeGroup, setProductAttributeGroup] = useState<ProductAttributeGroup>();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm<ProductAttributeGroup>();
  const [isLoading, setLoading] = useState(false);

  const handleSubmitGroup = async (event: ProductAttributeGroup) => {
    let productAttributeGroup: ProductAttributeGroup = {
      id: 0,
      name: event.name,
    };
    if (id) {
      updateProductAttributeGroup(+id, productAttributeGroup).then((response) => {
        handleUpdatingResponse(response, PRODUCT_ATTRIBUTE_GROUPS_URL);
      });
    }
  };
  useEffect(() => {
    if (id) {
      setLoading(true);
      getProductAttributeGroup(+id).then((data) => {
        if (data.id) {
          setProductAttributeGroup(data);
          setLoading(false);
        } else {
          toast(data?.detail);
          setLoading(false);
          router.push(PRODUCT_ATTRIBUTE_GROUPS_URL);
        }
      });
    }
  }, [id]);
  if (isLoading) return <p>Loading...</p>;
  if (!productAttributeGroup) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Product Attribute Group</h2>
          <form onSubmit={handleSubmit(handleSubmitGroup)} name="form">
            <ProductAttributeGroupGeneralInformation
              register={register}
              errors={errors}
              productAttributeGroup={productAttributeGroup}
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

export default ProductAttributeGroupEdit;
