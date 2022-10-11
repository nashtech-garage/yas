import { useForm, SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';

import { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import { Tab, Tabs } from 'react-bootstrap';
import { Check, Input, Text } from '../../../common/items/Input';
import { ProductPost } from '../../../modules/catalog/models/ProductPost.js';
import Link from 'next/link';
import { toast } from 'react-toastify';
import { getCategories } from '../../../modules/catalog/services/CategoryService';
import { getBrands } from '../../../modules/catalog/services/BrandService';
import { createProduct } from '../../../modules/catalog/services/ProductService';
import { Category } from '../../../modules/catalog/models/Category';
import { Brand } from '../../../modules/catalog/models/Brand';
import { CategoryGet } from '../../../modules/catalog/models/CategoryGet';

const test = [
  {
    id: 1,
    name: 'Cate 1',
    slug: 'Slug-1',
    parentId: null,
  },
  {
    id: 2,
    name: 'Cate 1',
    slug: 'Slug-1',
    parentId: null,
  },
  {
    id: 3,
    name: 'Cate 1',
    slug: 'Slug-1',
    parentId: null,
  },
  {
    id: 4,
    name: 'Cate 1',
    slug: 'Slug-1',
    parentId: null,
  },
  {
    id: 5,
    name: 'Cate 1',
    slug: 'Slug-1',
    parentId: null,
  },
];

const ProductCreate: NextPage = () => {
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  const [productImageURL, setProductImageURL] = useState<string[]>();
  const [thumbnail, setThumbnail] = useState<File>();
  const [productImages, setProductImages] = useState<FileList>();
  const [selectedCate, setSelectedCate] = useState<number[]>([]);
  const [categories, setCategories] = useState<CategoryGet[]>([]);
  const [brands, setBrands] = useState<Brand[]>([]);

  const {
    register,
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductPost>();

  useEffect(() => {
    getCategories().then((data) => {
      setCategories(data);
    });

    getBrands().then((data) => {
      setBrands(data);
    });
  }, []);

  const onProductImageSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const files = event.target.files;
      setProductImages(files);
      let length = files.length;

      let urls: string[] = [];

      for (let i = 0; i < length; i++) {
        const file = files[i];
        urls.push(URL.createObjectURL(file));
      }
      setProductImageURL([...urls]);
    }
  };

  const onThumbnailSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];
      setThumbnail(i);
      setThumbnailURL(URL.createObjectURL(i));
    }
  };

  const onSubmitForm: SubmitHandler<ProductPost> = async (data) => {
    data.brandId = data.brandId == 0 ? undefined : data.brandId;
    await createProduct(data, thumbnail, productImages)
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
    console.log(temp);
  };
  return (
    <div className="create-product">
      <h2>Create Product</h2>

      <form onSubmit={handleSubmit(onSubmitForm)}>
        <Tabs defaultActiveKey={'general'} className="mb-3">
          <Tab eventKey={'general'} title="General Information">
            <Input label="name" register={register} error={errors.name?.message} />
            <Input label="slug" register={register} error={errors.slug?.message} />
            <Input label="sku" register={register} error={errors.sku?.message} />
            <Input label="gtin" register={register} error={errors.gtin?.message} />
            <Input label="description" register={register} error={errors.description?.message} />
            <Input
              label="shortDescription"
              register={register}
              error={errors.shortDescription?.message}
            />
            <Input
              label="specification"
              register={register}
              error={errors.specification?.message}
            />
            <Input label="price" register={register} error={errors.price?.message} type="number" />
            <div className="mb-3">
              <label className="form-label" htmlFor="brand">
                BRAND
              </label>
              <select {...register('brandId')} id="brand" className="form-control">
                <option value="0" disabled hidden>
                  Select Brand
                </option>
                {(brands || []).map((brand) => (
                  <option value={brand.id} key={brand.id}>
                    {brand.name}
                  </option>
                ))}
              </select>
            </div>

            <Check label="isAllowedToOrder" register={register} />
            <Check label="isPublished" register={register} />
            <Check label="isFeatured" register={register} />
          </Tab>
          <Tab eventKey={'image'} title="Product Images">
            <div className="mb-3">
              <label className="form-label" htmlFor="thumbnail">
                Thumbnail
              </label>
              <input
                className={`form-control`}
                type="file"
                id="thumbnail"
                onChange={onThumbnailSelected}
              />

              <img style={{ width: '150px' }} src={thumbnailURL} />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="product-image">
                Product Image
              </label>
              <input
                className="form-control"
                type="file"
                id="product-image"
                onChange={onProductImageSelected}
                multiple
              />
              {productImageURL?.map((productImageUrl, index) => (
                <img style={{ width: '150px' }} src={productImageUrl} key={index} alt="Image" />
              ))}
            </div>
          </Tab>
          <Tab eventKey={'variation'} title="Product Variations">
            needs to be completed
          </Tab>
          <Tab eventKey={'attribute'} title="Product Attributes">
            needs to be completed
          </Tab>
          <Tab eventKey={'category'} title="Category Mapping">
            {(test || []).map((cate) => (
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
            needs to be completed
          </Tab>
          <Tab eventKey={'cross-sell'} title="Cross-sell Product">
            needs to be completed
          </Tab>
          <Tab eventKey={'seo'} title="SEO">
            <Input label="metaTitle" register={register} />
            <Text label="metaKeyword" register={register} />
            <Text label="metaDescription" register={register} />
          </Tab>
        </Tabs>
        <div>
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
