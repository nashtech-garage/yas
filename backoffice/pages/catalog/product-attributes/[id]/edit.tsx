import { ProductAttribute } from '../../../../modules/catalog/models/ProductAttribute';
import type { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { ProductAttributeGroup } from '../../../../modules/catalog/models/ProductAttributeGroup';
import { getProductAttributeGroups } from '../../../../modules/catalog/services/ProductAttributeGroupService';
import { useRouter } from 'next/router';
import {
  getProductAttribute,
  updateProductAttribute,
} from '../../../../modules/catalog/services/ProductAttributeService';
import { useUpdatingContext } from '../../../../common/hooks/UseToastContext';
import CustomToast from '../../../../common/items/CustomToast';
import { PRODUCT_ATTRIBUTE_URL } from '../../../../constants/Common';

interface ProductAttributeId {
  name: string;
  productAttributeGroupId: string;
}

const ProductAttributeEdit: NextPage = () => {
  const [productAttributeGroup, setProductAttributeGroup] = useState<ProductAttributeGroup[]>([]);
  const [getProductAttributeVm, setGetProductAttributeVm] = useState<ProductAttribute>();
  const [idProductAttributeGroup, setIdProductAttributeGroupGroup] = useState(String);
  const { toastVariant, toastHeader, showToast, setShowToast, handleUpdatingResponse } = useUpdatingContext();

  const router = useRouter();
  let { id }: any = router.query;
  const [isLoading, setLoading] = useState(false);
  const handleSubmitProductAttribute = async (event: any) => {
    event.preventDefault();
    if (id !== undefined) {
      let productAttributeId: ProductAttributeId = {
        name: event.target.name.value,
        productAttributeGroupId: idProductAttributeGroup,
      };
      updateProductAttribute(parseInt(id), productAttributeId).then((response) => {
        handleUpdatingResponse(response, PRODUCT_ATTRIBUTE_URL);
      });
    }
  };

  useEffect(() => {
    setLoading(true);
    if (id !== undefined) {
      getProductAttribute(parseInt(id)).then((data) => {
        setGetProductAttributeVm(data);
        setLoading(false);
      });
    }
  }, [id]);
  useEffect(() => {
    getProductAttributeGroups().then((data) => {
      setProductAttributeGroup(data);
    });
  }, []);
  if (isLoading) return <p>Loading...</p>;

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Product Attribute</h2>
          <form onSubmit={handleSubmitProductAttribute}>
            <div className="mb-3">
              <div className="mb-3">
                <label className="form-label" htmlFor="name">
                  Name
                </label>
                <input
                  className="form-control"
                  required
                  type="text"
                  id="name"
                  name="name"
                  defaultValue={getProductAttributeVm ? getProductAttributeVm.name : ''}
                />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="productAG">
                Group
              </label>
              <select
                className="form-control"
                name="groupId"
                id="groupId"
                onChange={(event) => setIdProductAttributeGroupGroup(event.target.value)}
              >
                <option value={0}>{getProductAttributeVm?.productAttributeGroup}</option>
                {productAttributeGroup.map((productAttributeGroup) =>
                  productAttributeGroup.name === getProductAttributeVm?.productAttributeGroup ? (
                    ''
                  ) : (
                    <option value={productAttributeGroup.id} key={productAttributeGroup.id}>
                      {productAttributeGroup.name}
                    </option>
                  )
                )}
              </select>
            </div>
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

export default ProductAttributeEdit;
