import { useForm, SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';

import { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import { Tab, Tabs, Table } from 'react-bootstrap';
import { Check, Input, Text } from '../../../common/items/Input';
import { ProductPost } from '../../../modules/catalog/models/ProductPost.js';
import Link from 'next/link';
import { toast } from 'react-toastify';
import { getCategories } from '../../../modules/catalog/services/CategoryService';
import { getBrands } from '../../../modules/catalog/services/BrandService';
import { createProduct } from '../../../modules/catalog/services/ProductService';
import { Brand } from '../../../modules/catalog/models/Brand';
import { CategoryGet } from '../../../modules/catalog/models/CategoryGet';
import ProductGeneralInformation from '../../../modules/catalog/components/ProductGeneralInformation';
import ProductSEO from '../../../modules/catalog/components/ProductSEO';
import ProductImage from '../../../modules/catalog/components/ProductImage';
import ProductVariation from '../../../modules/catalog/components/ProductVariation';
import {RelatedProduct, CrossSellProduct} from '../../../modules/catalog/components/RelatedProduct';

const ProductCreate: NextPage = () => {

  const [selectedCate, setSelectedCate] = useState<number[]>([]);
  const [categories, setCategories] = useState<CategoryGet[]>([]);

  const {
    register,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm<ProductPost>();

  useEffect(() => {
    getCategories().then((data) => {
      setCategories(data);
    });

   
  }, []);


  const onSubmitForm: SubmitHandler<ProductPost> = async (data) => {
    data.brandId = data.brandId == 0 ? undefined : data.brandId;
    await createProduct(data, getValues("thumbnail"), getValues("productImages"))
      .then((res) => {
        location.replace('/catalog/products');
      })
      .catch((err) => {
        toast.info('Cannot Create Product. Try Later!');
      });
  };

  const onCategoriesSelected = (event: React.MouseEvent<HTMLElement>, id: number) => {
    let temp = selectedCate;
    let index = temp.indexOf(id);
    if (index > -1) {
      temp.splice(index, 1);
    } else {
      temp.push(id);
    }
    setSelectedCate(temp);
  };

  return (
    <div className="create-product">
      <h2>Create Product</h2>

      <form onSubmit={handleSubmit(onSubmitForm)}>
        <Tabs defaultActiveKey={'general'} className="mb-3">
          <Tab eventKey={'general'} title="General Information">
            <ProductGeneralInformation register={register} errors={errors} />
          </Tab>
          <Tab eventKey={'image'} title="Product Images">
            <ProductImage />
          </Tab>
          <Tab eventKey={'variation'} title="Product Variations">
            <ProductVariation getValue={getValues} />
          </Tab>

          <Tab eventKey={'attribute'} title="Product Attributes">
            needs to be completed
          </Tab>
          <Tab eventKey={'category'} title="Category Mapping">
            {(categories || []).map((cate) => (
              <div className="mb-3" key={cate.id}>
                <input
                  type="checkbox"
                  id={cate.slug}
                  onClick={(event) => onCategoriesSelected(event, cate.id)}
                />
                <label className="form-check-label ps-3" htmlFor={cate.slug}>
                  {cate.name}
                </label>
              </div>
            ))}
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
