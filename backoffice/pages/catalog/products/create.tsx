import { SubmitHandler, useForm } from 'react-hook-form';
import { NextPage } from 'next';
import Link from 'next/link';
import React, { useEffect, useState } from 'react';
import { Tab, Tabs } from 'react-bootstrap';
import { toast } from 'react-toastify';
import {
  CrossSellProduct,
  ProductAttributes,
  ProductGeneralInformation,
  ProductImage,
  ProductSEO,
  ProductVariation,
  RelatedProduct,
  ProductCategoryMapping,
} from '../../../modules/catalog/components';
import { CategoryGet } from '../../../modules/catalog/models/CategoryGet';
import { ProductPost } from '../../../modules/catalog/models/ProductPost.js';
import { getCategories } from '../../../modules/catalog/services/CategoryService';
import { createProduct } from '../../../modules/catalog/services/ProductService';

const ProductCreate: NextPage = () => {
  const {
    register,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm<ProductPost>();

  const onSubmitForm: SubmitHandler<ProductPost> = async (data) => {
    data.brandId = data.brandId == 0 ? undefined : data.brandId;
    // await createProduct(data, getValues('thumbnail'), getValues('productImages'))
    //   .then((res) => {
    //     location.replace('/catalog/products');
    //   })
    //   .catch((err) => {
    //     toast.info('Cannot Create Product. Try Later!');
    //   });
    console.log(data);
  };

  return (
    <div className="create-product">
      <h2>Create Product</h2>

      <form onSubmit={handleSubmit(onSubmitForm)}>
        <Tabs defaultActiveKey={'general'} className="mb-3">
          <Tab eventKey={'general'} title="General Information">
            <ProductGeneralInformation register={register} errors={errors} setValue={setValue} />
          </Tab>
          <Tab eventKey={'image'} title="Product Images">
            <ProductImage setValue={setValue} />
          </Tab>
          <Tab eventKey={'variation'} title="Product Variations">
            <ProductVariation getValue={getValues} setValue={setValue} />
          </Tab>

          <Tab eventKey={'attribute'} title="Product Attributes">
            <ProductAttributes setValue={setValue} getValue={getValues} />
          </Tab>
          <Tab eventKey={'category'} title="Category Mapping">
            <ProductCategoryMapping setValue={setValue} getValue={getValues} />
          </Tab>
          <Tab eventKey={'related'} title="Related Products">
            <RelatedProduct setValue={setValue} getValue={getValues} />
          </Tab>
          <Tab eventKey={'cross-sell'} title="Cross-sell Product">
            <CrossSellProduct setValue={setValue} getValue={getValues} />
          </Tab>
          <Tab eventKey={'seo'} title="SEO">
            <ProductSEO register={register} errors={errors} />
          </Tab>
        </Tabs>
        <div className="text-center">
          <button className="btn btn-primary" type="submit">
            Create
          </button>
          <Link href="/catalog/products">
            <button className="btn btn-secondary m-3">Cancel</button>
          </Link>
        </div>
      </form>
    </div>
  );
};

export default ProductCreate;
