import type { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { getCategories } from '../../../../modules/catalog/services/CategoryService';
import { Category } from '../../../../modules/catalog/models/Category';
import { getProduct, updateProduct } from '../../../../modules/catalog/services/ProductService';
import { SubmitHandler, useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import Link from 'next/link';
import { ProductPut } from '../../../../modules/catalog/models/ProductPut';
import { Product } from '../../../../modules/catalog/models/Product';

const EditCategoryOfProduct: NextPage = () => {
  const [isLoading, setLoading] = useState(false);
  const [categories, setCategories] = useState<Category[]>([]);
  let [checkCategory, setCheckCategory] = useState<string[]>([]);
  let [listCheckCategory] = useState<string[]>([]);
  let [childrenOfCategoryId] = useState<string[]>([]);
  let [categoryIds] = useState<number[]>([]);

  const [product, setProduct] = useState<Product>();
  const router = useRouter();
  const { id } = router.query;
  const { handleSubmit } = useForm();

  function findChildrenOfCategoryId(id: number) {
    for (let i = 0; i < categories.length; i++) {
      if (categories[i].parentId === id) {
        childrenOfCategoryId.push(String(categories[i]?.id));
        findChildrenOfCategoryId(categories[i]?.id);
      }
    }
  }

  const onSaveListCategory = (e: any) => {
    if (e.target.checked) {
      setCheckCategory([e.target.value, ...checkCategory]);
      const category = categories.find((element) => element.id === Number(e.target.value));
      let id = e.target.value;
      if (category?.parentId !== -1) {
        let idParent: number | undefined = category?.parentId;
        while (true) {
          const parent = categories.find((element) => element.id === idParent);
          checkCategory.push(String(id));
          setCheckCategory([String(idParent), ...checkCategory]);
          id = idParent;
          if (parent?.parentId === -1) break;
          else {
            idParent = parent?.parentId;
            continue;
          }
        }
      }
    } else {
      listCheckCategory = [];
      checkCategory = checkCategory.filter((item) => item !== e.target.value);
      childrenOfCategoryId = [];
      findChildrenOfCategoryId(Number(e.target.value));
      for (let i = 0; i < childrenOfCategoryId.length; i++) {
        checkCategory = checkCategory.filter((item) => item !== childrenOfCategoryId[i]);
      }
      listCheckCategory = Array.from(new Set(checkCategory));
      setCheckCategory(listCheckCategory);
    }
  };
  function checkedTrue(id: number) {
    const found = checkCategory.find((element) => element === id.toString());
    if (found === undefined) return false;
    return true;
  }
  function CategoriesHierarchy(id: number): string | undefined {
    const category = categories.find((element) => element.id === id);
    const parentCategory = categories.find((element) => element.id === category?.parentId);
    if (parentCategory === undefined) return category?.name.toString();
    let categoryHierarchy = category?.name.toString();
    let idParent: number | undefined = parentCategory.id;
    while (true) {
      const parent = categories.find((element) => element.id === idParent);
      categoryHierarchy = parent?.name + ' >>' + categoryHierarchy;
      if (parent?.parentId === -1) {
        break;
      } else {
        idParent = parent?.parentId;
        continue;
      }
    }
    return categoryHierarchy;
  }

  useEffect(() => {
    setLoading(true);
    if (id) {
      getProduct(+id).then((data) => {
        setProduct(data);
        listCheckCategory = [];
        if (checkCategory.length === 0) {
          data.categories.map((item: any) => {
            listCheckCategory.push(item.id.toString());
          });
          setCheckCategory(listCheckCategory);
        }
      });
    }
    getCategories().then((data) => {
      setCategories(data);
      setLoading(false);
    });
  }, []);

  const onSubmit: SubmitHandler<ProductPut> = (data) => {
    checkCategory = Array.from(new Set(checkCategory));
    for (let i = 0; i < checkCategory.length; i++) {
      categoryIds.push(Number(checkCategory[i]));
    }
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
    data.categoryIds = categoryIds.length > 0 ? categoryIds : defaultCategoryIds;
    data.metaKeyword = product?.metaKeyword;
    data.metaDescription = product?.metaDescription;
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
  if (!categories) return <p>No Category</p>;

  return (
    <>
      <div className="choice-category">
        <form onSubmit={handleSubmit(onSubmit)}>
          <ul style={{ listStyleType: 'none' }}>
            {categories.map((category, index) => (
              <li key={index}>
                <input
                  value={category.id || ''}
                  type="checkbox"
                  name="category"
                  checked={checkedTrue(category.id) === true ? true : false}
                  id={`checkbox-${category.id}`}
                  onChange={onSaveListCategory}
                />
                <label
                  htmlFor={`checkbox-${category.id}`}
                  style={{
                    paddingLeft: '15px',
                    fontSize: '1rem',
                    paddingTop: '10px',
                    paddingBottom: '5px',
                  }}
                >
                  {' '}
                  {CategoriesHierarchy(category.id)}
                </label>
              </li>
            ))}
          </ul>
          <button className="btn btn-primary" type="submit">
            Save
          </button>
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

export default EditCategoryOfProduct;