import type { NextPage } from 'next';
import React from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { useRouter } from 'next/router';
import { createProductAttributeGroup } from '../../../modules/catalog/services/ProductAttributeGroupService';
import { ProductAttributeGroup } from '../../../modules/catalog/models/ProductAttributeGroup';
import { PRODUCT_ATTRIBUTE_GROUPS_URL } from '../../../constants/Common';
import { handleCreatingResponse } from '../../../modules/catalog/services/ResponseStatusHandlingService';

const ProductAttributeGroupCreate: NextPage = () => {
  const router = useRouter();
  const { register, handleSubmit, formState } = useForm();
  const { errors } = formState;
  const handleSubmitGroup = async (event: any) => {
    let productAttributeGroup: ProductAttributeGroup = {
      id: 0,
      name: event.name,
    };
    let response = await createProductAttributeGroup(productAttributeGroup);
    handleCreatingResponse(response);
    router.replace(PRODUCT_ATTRIBUTE_GROUPS_URL);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create Product Attribute Group</h2>
          <form onSubmit={handleSubmit(handleSubmitGroup)}>
            <div className="mb-3">
              <div className="mb-3">
                <label className="form-label">Name</label>
                <input
                  className="form-control"
                  {...register('name', { required: true })}
                  type="text"
                  id="name"
                  name="name"
                />
                {errors.name && errors.name.type == 'required' && (
                  <p className="text-danger">Please enter the name product attribute group</p>
                )}
              </div>
            </div>
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
