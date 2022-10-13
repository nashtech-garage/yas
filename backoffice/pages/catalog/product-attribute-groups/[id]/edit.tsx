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
const ProductAttributeGroupEdit: NextPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const [productAttributeGroup, setProductAttributeGroup] = useState<ProductAttributeGroup>();
  const { register, handleSubmit, formState, setValue } = useForm();
  const { errors } = formState;
  const handleSubmitGroup = async (event: any) => {
    let productAttributeGroup: ProductAttributeGroup = {
      id: 0,
      name: event.name,
    };
    if (id) {
      updateProductAttributeGroup(+id, productAttributeGroup).then((response) => {
        if (response.status === 204) {
          toast.success('Update successfully');
          router.replace('/catalog/product-attribute-groups');
        } else if (response.title === 'Not found') {
          toast.error(response.detail);
          router.replace('/catalog/product-attribute-groups');
        } else if (response.title === 'Bad request') {
          toast.error(response.detail);
        } else {
          toast.error('Update failed');
          router.replace('/catalog/product-attribute-groups');
        }
      });
    }
  };
  useEffect(() => {
    if (id) {
      getProductAttributeGroup(+id).then((data) => {
        setProductAttributeGroup(data);
        setValue('name', data.name);
      });
    }
  }, [id]);
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Product Attribute Group</h2>
          <form onSubmit={handleSubmit(handleSubmitGroup)} name="form">
            <div className="mb-3">
              <div className="mb-3">
                <label className="form-label">Name</label>
                <input
                  className="form-control"
                  {...register('name', { required: true })}
                  type="text"
                  id="name"
                  name="name"
                  defaultValue={productAttributeGroup?.name}
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
            <Link href="/catalog/product-options">
              <button className="btn btn-outline-secondary">Cancel</button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default ProductAttributeGroupEdit;
