import type { NextPage } from 'next';
import { useRouter } from 'next/router';
import React, { useEffect, useState } from 'react';
import { Product } from '../../../../modules/catalog/models/Product';
import { getProduct, updateProduct } from '../../../../modules/catalog/services/ProductService';
import { SubmitHandler, useForm } from 'react-hook-form';
import slugify from 'slugify';
import { ProductPut } from '../../../../modules/catalog/models/ProductPut';
import { Category } from '../../../../modules/catalog/models/Category';
import { Brand } from '../../../../modules/catalog/models/Brand';
import { getCategories } from '../../../../modules/catalog/services/CategoryService';
import { getBrands } from '../../../../modules/catalog/services/BrandService';
import { uploadMedia } from '../../../../modules/catalog/services/MediaService';
import Link from 'next/link';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const ProductEdit: NextPage = () => {
  //Get ID
  const router = useRouter();
  const { id } = router.query;
  //Variables
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  const [thumbnailMediaId, setThumbnailMediaId] = useState<number>();
  const [generateSlug, setGenerateSlug] = useState<string>();
  const [productImageMediaUrls, setproductImageMediaUrls] = useState<string[]>();
  const [productImageMediaIds, setProductImageMediaIds] = useState<number[]>();
  const [categoryIds, setCategoryIds] = useState<number[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [brands, setBrands] = useState<Brand[]>([]);
  //
  //Get product detail
  const [product, setProduct] = useState<Product>();
  const [isLoading, setLoading] = useState(false);
  //Form validate
  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm();

  useEffect(() => {
    setLoading(true);
    if (id) {
      getProduct(+id).then((data) => {
        if (data.id) {
          setProduct(data);
          setLoading(false);
        } else {
          //Show error
          toast(data.detail);
          location.replace('/catalog/products');
        }
      });
    }
    getCategories().then((data) => {
      setCategories(data);
    });
    getBrands().then((data) => {
      setBrands(data);
    });
  }, [id]);
  //Handle
  const onNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setGenerateSlug(slugify(event.target.value.toLowerCase()));
  };

  const onSlugChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setGenerateSlug(event.target.value.toLowerCase());
  };

  const onThumbnailSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];
      setThumbnailURL(URL.createObjectURL(i));
      uploadMedia(i)
        .then((res) => {
          setThumbnailMediaId(res.id);
        })
        .catch(() => {
          toast('Upload failed. Please try again!');
        });
    }
  };

  const onProductImageSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const files = event.target.files;
      let length = files.length;
      let urls: string[] = [];
      let ids: number[] = [];
      for (let i = 0; i < length; i++) {
        const file = files[i];
        urls.push(URL.createObjectURL(file));
        uploadMedia(file)
          .then((res) => {
            ids = [...ids, res.id];
            setProductImageMediaIds(ids);
          })
          .catch(() => {
            toast('Upload failed. Please try again!');
          });
      }
      setproductImageMediaUrls([...urls]);
    }
  };

  const onCategoryChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    let category = event.target.value;
    if (selectedCategories.indexOf(category) === -1) {
      let id = categories.find((item) => item.name === category)?.id;
      if (id) {
        setCategoryIds([...categoryIds, id]);
      }
      setSelectedCategories([category, ...selectedCategories]);
    }
  };

  const onSubmit: SubmitHandler<ProductPut> = (data) => {
    if (generateSlug) {
      data.slug = generateSlug;
    }

    let defaultCategoryIds: number[] = [];
    if (product) {
      Array.from(product.categories).forEach((category) => {
        defaultCategoryIds = [...defaultCategoryIds, category.id];
      });
    }
    data.categoryIds = categoryIds.length > 0 ? categoryIds : defaultCategoryIds;
    if(data.brandId && +data.brandId === 0){
      data.brandId = undefined;
    }
    data.thumbnailMediaId = thumbnailMediaId;
    data.productImageIds = productImageMediaIds;

    if (id) {
      updateProduct(+id, data).then(async (res) => {
        if (res.ok) {
          location.replace('/catalog/products');
        } else {
          res.json().then((error) => {
            toast(error.detail);
          });
        }
      });
    }
  };
  if (isLoading) return <p>Loading...</p>;
  if (!product) {
    return <p>No product</p>;
  } else {
    return (
      <>
        <div className="row mt-5">
          <div className="col-md-8">
            <h2>Update product: #{product.id}</h2>
            <form onSubmit={handleSubmit(onSubmit)}>
              <div className="mb-3">
                <label className="form-label" htmlFor="name">
                  Name <span style={{ color: 'red' }}>*</span>
                </label>
                <input
                  defaultValue={product.name}
                  {...register('name', { required: 'Name is required', onChange: onNameChange })}
                  className={`form-control ${errors.name ? 'border-danger' : ''}`}
                  type="text"
                  id="name"
                  name="name"
                />
                <p className="error-field">
                  <>{errors.name?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="slug">
                  Slug
                </label>
                <input
                  value={generateSlug ? generateSlug : product.slug}
                  {...register('slug', { onChange: onSlugChange })}
                  className={`form-control ${errors.slug ? 'border-danger' : ''}`}
                  type="text"
                  id="slug"
                  name="slug"
                />
                <p className="error-field">
                  <>{errors.slug?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="brand">
                  Brand
                </label>
                <select
                  className={`form-select ${errors.brandId ? 'border-danger' : ''}`}
                  id="brand"
                  {...register('brandId')}
                  defaultValue={product.brandId ? product.brandId : 0}
                >
                  <option disabled hidden value={0}>
                    Select Brand
                  </option>
                  {Array.from(brands).map((brand) => (
                    <option value={brand.id} key={brand.id}>
                      {brand.name}
                    </option>
                  ))}
                </select>
                <p className="error-field">
                  <>{errors.brandId?.message}</>
                </p>
              </div>

              <div className="mb-3">
                <label className="form-label" htmlFor="category">
                  Category
                </label>
                <select
                  className={`form-select ${errors.categoryIds ? 'border-danger' : ''}`}
                  id="category"
                  {...register('categoryIds')}
                  onChange={onCategoryChange}
                  defaultValue={0}
                >
                  <option disabled hidden value={0}>
                    Select Category
                  </option>
                  {Array.from(categories).map((category) => (
                    <option value={category?.name} key={category?.id}>
                      {category?.name}
                    </option>
                  ))}
                </select>
                <p className="error-field">
                  <>{errors.categoryIds?.message}</>
                </p>
                <div className="d-flex flex-start mt-2">
                  {selectedCategories.length > 0
                    ? selectedCategories.map((category, index) => (
                        <span className="border border-primary rounded fst-italic px-2" key={index}>
                          {category}
                        </span>
                      ))
                    : product.categories &&
                      product.categories.map((category, index) => (
                        <span className="border border-primary rounded fst-italic px-2" key={index}>
                          {category.name}
                        </span>
                      ))}
                </div>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="short-description">
                  Short Description
                </label>
                <input
                  defaultValue={product.shortDescription}
                  {...register('shortDescription')}
                  className={`form-control ${errors.shortDescription ? 'border-danger' : ''}`}
                  type="text"
                  id="short-description"
                  name="shortDescription"
                />
                <p className="error-field">
                  <>{errors.shortDescription?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="description">
                  Description
                </label>
                <textarea
                  defaultValue={product.description}
                  {...register('description')}
                  className="form-control"
                  id="description"
                  name="description"
                />
                <p className="error-field">
                  <>{errors.description?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="specification">
                  Specification
                </label>
                <textarea
                  defaultValue={product.specification}
                  {...register('specification')}
                  className={`form-control ${errors.specification ? 'border-danger' : ''}`}
                  id="specification"
                  name="specification"
                />
                <p className="error-field">
                  <>{errors.specification?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="specification">
                  Product Attributes
                </label>
                <Link href={`/catalog/products/${product.id}/productAttributes`}>
                  <button className="btn btn-primary" style={{ marginLeft: '35px' }}>
                    Product Attributes
                  </button>
                </Link>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="sku">
                  SKU
                </label>
                <input
                  defaultValue={product.sku}
                  {...register('sku')}
                  className={`form-control ${errors.sku ? 'border-danger' : ''}`}
                  type="text"
                  id="sku"
                  name="sku"
                />
                <p className="error-field">
                  <>{errors.sku?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="gtin">
                  GTIN
                </label>
                <input
                  defaultValue={product.gtin}
                  {...register('gtin')}
                  className={`form-control ${errors.gtin ? 'border-danger' : ''}`}
                  type="text"
                  id="gtin"
                  name="gtin"
                />
                <p className="error-field">
                  <>{errors.gtin?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="price">
                  Price
                </label>
                <input
                  defaultValue={product.price}
                  {...register('price', { min: 0 })}
                  className={`form-control ${errors.price ? 'border-danger' : ''}`}
                  type="number"
                  id="price"
                />
                {errors.price && errors.price.type === 'min' ? (
                  <p className="error-field">Price must be at least 0</p>
                ) : (
                  <p className="error-field">
                    <>{errors.price?.message}</>
                  </p>
                )}
              </div>
              <div className="d-flex justify-content-between">
                <div className="mb-3">
                  <label className="form-label me-3" htmlFor="is-allowed-to-order">
                    Is Allowed To Order
                  </label>
                  <input
                    defaultChecked={product.isAllowedToOrder}
                    {...register('isAllowedToOrder')}
                    type="checkbox"
                    id="is-allowed-to-order"
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label me-3" htmlFor="is-published">
                    Is Published
                  </label>
                  <input
                    defaultChecked={product.isPublished}
                    {...register('isPublished')}
                    type="checkbox"
                    id="is-published"
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label me-3" htmlFor="is-featured">
                    Is Featured
                  </label>
                  <input
                    defaultChecked={product.isFeatured}
                    {...register('isFeatured')}
                    type="checkbox"
                    id="is-featured"
                  />
                </div>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="meta-keyword">
                  Meta Keyword
                </label>
                <input
                  defaultValue={product.metaKeyword}
                  {...register('metaKeyword')}
                  className={`form-control ${errors.metaKeyword ? 'border-danger' : ''}`}
                  type="text"
                  id="meta-keyword"
                  name="metaKeyword"
                />
                <p className="error-field">
                  <>{errors.metaKeyword?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="meta-description">
                  Meta Description
                </label>
                <input
                  defaultValue={product.metaDescription}
                  {...register('metaDescription')}
                  type="text"
                  className="form-control"
                  id="meta-description"
                  name="metaDescription"
                />
                <p className="error-field">
                  <>{errors.metaDescription?.message}</>
                </p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="thumbnail">
                  Thumbnail
                </label>
                <input
                  className="form-control"
                  type="file"
                  name="thumbnail"
                  onChange={onThumbnailSelected}
                />
                <img
                  style={{ width: '150px' }}
                  src={thumbnailURL ? thumbnailURL : product.thumbnailMediaUrl}
                />
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="product-image">
                  Product Images
                </label>
                <input
                  className="form-control"
                  type="file"
                  id="product-images"
                  onChange={onProductImageSelected}
                  multiple
                />
                {productImageMediaUrls
                  ? productImageMediaUrls.map((imageUrl, index) => (
                      <img
                        style={{ width: '150px' }}
                        src={imageUrl}
                        key={index}
                        alt="Product Image"
                      />
                    ))
                  : product.productImageMediaUrls &&
                    product.productImageMediaUrls.map((imageUrl, index) => (
                      <img
                        style={{ width: '150px' }}
                        src={imageUrl}
                        key={index}
                        alt="Product Image"
                      />
                    ))}
              </div>
              <button className="btn btn-primary" type="submit">
                Submit
              </button>
            </form>
          </div>
        </div>
      </>
    );
  }
};

export default ProductEdit;
