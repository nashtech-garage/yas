import type { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { getProduct, updateProduct } from '../../../../modules/catalog/services/ProductService';
import { SubmitHandler, useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import Link from 'next/link';
import { ProductPut } from '../../../../modules/catalog/models/ProductPut';
import { Product } from '../../../../modules/catalog/models/Product';

const EditSEOOfProduct: NextPage = () => {
  const [isLoading, setLoading] = useState(false);
  const [product, setProduct] = useState<Product>();
  const [metaKeyword, setMetaKeyword] = useState<string>();
  const [metaDescription, setMetaDescription] = useState<string>();

  const router = useRouter();
  const { id } = router.query;

  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm();

  useEffect(() => {
    setLoading(true);
    if (id) {
      getProduct(+id).then((data) => {
        setProduct(data);
        setMetaKeyword(data.metaKeyword);
        setMetaDescription(data.metaDescription);
        setLoading(false);
      });
    }
  }, []);
  const onMetaKeywordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setMetaKeyword(event.target.value);
  };
  const onMetaDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setMetaDescription(event.target.value);
  };
  const onSubmit: SubmitHandler<ProductPut> = (data) => {
    let defaultCategoryIds: number[] = [];
    if (product) {
      Array.from(product.categories).forEach((category) => {
        defaultCategoryIds = [...defaultCategoryIds, category.id];
      });
    }
    data.name = product?.name;
    data.slug = product?.slug;
    data.shortDescription = product?.shortDescription;
    data.description = product?.description;
    data.specification = product?.specification;
    data.sku = product?.sku;
    data.gtin = product?.gtin;
    data.price = product?.price;
    data.isAllowedToOrder = product?.isAllowedToOrder;
    data.isPublished = product?.isPublished;
    data.isFeatured = product?.isFeatured;
    data.brandId = product?.brandId;
    data.categoryIds = defaultCategoryIds;
    data.metaKeyword = metaKeyword;
    data.metaDescription = metaDescription;
    data.thumbnailMediaId = undefined;
    data.productImageIds = undefined;

    if (id) {
      updateProduct(+id, data).then(async (res) => {
        if (res.ok) {
          router.push('/catalog/products');
        } else {
          res.json().then((error) => {
            toast(error.detail);
          });
        }
      });
    }
  };

  if (isLoading) return <p>Loading...</p>;
  if (!product) return <p>No Product</p>;

  return (
    <>
      <div className="choice-SEO" style={{ marginTop: '50px' }}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="mb-3">
            <label className="form-label" htmlFor="meta-keyword">
              Meta Keyword
            </label>
            <input
              defaultValue={product.metaKeyword}
              {...register('metaKeyword')}
              onChange={onMetaKeywordChange}
              className="form-control"
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
              onChange={onMetaDescriptionChange}
              className="form-control"
              id="meta-description"
              name="metaDescription"
            />
            <p className="error-field">
              <>{errors.metaDescription?.message}</>
            </p>
          </div>
          {metaDescription === '' || metaKeyword === '' ? (
            <button className="btn btn-primary" type="submit" disabled>
              Save
            </button>
          ) : (
            <button className="btn btn-primary" type="submit">
              Save
            </button>
          )}
          <Link href={`/catalog/products`}>
            <button className="btn btn-outline-secondary" style={{ marginLeft: '30px' }}>
              Cancel
            </button>
          </Link>
        </form>
      </div>
    </>
  );
};

export default EditSEOOfProduct;
