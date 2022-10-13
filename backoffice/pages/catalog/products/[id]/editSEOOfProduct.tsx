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
  const [product, setProduct] = useState<Product>();
  const [metaKeyword, setMetaKeyword] = useState<string>();
  const [metaDescription, setMetaDescription] = useState<string>();
  const router = useRouter();
  const { id } = router.query;
  const {
    formState: { errors },
    handleSubmit,
  } = useForm();

  const onMetaKeywordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setMetaKeyword(event.target.value);
  };
  const onMetaDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setMetaDescription(event.target.value);
  };

  useEffect(() => {
    if (id) {
      getProduct(+id).then((data) => {
        setProduct(data);
        setMetaKeyword(data.metaKeyword);
        setMetaDescription(data.metaDescription);
      });
    }
  }, []);

  const onSubmit: SubmitHandler<ProductPut> = (data) => {
    let defaultCategoryIds: number[] = [];
    if (product) {
      Array.from(product.categories).forEach((category) => {
        defaultCategoryIds = [...defaultCategoryIds, category.id];
      });
    }
    data.sku = product?.sku;
    data.gtin = product?.gtin;
    data.price = product?.price;
    data.isAllowedToOrder = product?.isAllowedToOrder;
    data.name = product?.name;
    data.slug = product?.slug;
    data.shortDescription = product?.shortDescription;
    data.description = product?.description;
    data.specification = product?.specification;
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
              onChange={onMetaKeywordChange}
              className="form-control"
              type="text"
              id="meta-keyword"
              name="metaKeyword"
            />
          </div>
          <div className="mb-3">
            <label className="form-label" htmlFor="meta-description">
              Meta Description
            </label>
            <input
              defaultValue={product.metaDescription}
              type="text"
              onChange={onMetaDescriptionChange}
              className="form-control"
              id="meta-description"
              name="metaDescription"
            />
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
