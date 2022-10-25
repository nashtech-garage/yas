import { ProductPost } from '../models/ProductPost';
import { UseFormSetValue, UseFormGetValues } from 'react-hook-form';
import { useEffect, useState } from 'react';
import { getCategories } from '../services/CategoryService';
import { Category } from '../models/Category';
import { useRouter } from 'next/router';
import { getProduct } from '../services/ProductService';
import { Product } from '../models/Product';
type Props = {
  setValue: UseFormSetValue<ProductPost>;
  getValue: UseFormGetValues<ProductPost>;
};

const ProductCategoryMapping = ({ setValue, getValue }: Props) => {
  const [categories, setCategories] = useState<Category[]>([]);
  let [checkCategory, setCheckCategory] = useState<string[]>([]);
  let listCheckCategory: string[] = [];
  let childrenOfCategoryId: string[] = [];
  let parentOfCategoryId: string[] = [];
  let categoryIds: number[] = [];
  const [product, setProduct] = useState<Product>();

  //Get ID
  const router = useRouter();
  const { id } = router.query;

  useEffect(() => {
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
    });
  }, []);

  function findChildrenOfCategoryId(id: number) {
    for (let value of categories) {
      if (value.parentId === id) {
        childrenOfCategoryId.push(String(value?.id));
        findChildrenOfCategoryId(value?.id);
      }
    }
  }
  function findParentCategoryOfProduct(id: number) {
    for (let value of categories) {
      if (value?.id === id) {
        parentOfCategoryId.push(String(value?.id));
        findParentCategoryOfProduct(Number(value?.parentId));
      }
    }
  }
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
      }
    }
    return categoryHierarchy;
  }
  const onSaveUpdateCategory = (e: any) => {
    if (e.target.checked) {
      setCheckCategory([e.target.value, ...checkCategory]);
      listCheckCategory = [e.target.value, ...checkCategory];
      const category = categories.find((element) => element.id === Number(e.target.value));
      if (category?.parentId !== -1) {
        parentOfCategoryId = [];
        findParentCategoryOfProduct(Number(category?.parentId));
        listCheckCategory = checkCategory.concat(parentOfCategoryId, e.target.value);
        setCheckCategory(listCheckCategory);
      }
    } else {
      checkCategory = checkCategory.filter((item) => item !== e.target.value);
      childrenOfCategoryId = [];
      findChildrenOfCategoryId(Number(e.target.value));
      for (let value of childrenOfCategoryId) {
        checkCategory = checkCategory.filter((item) => item !== value);
      }
      listCheckCategory = Array.from(new Set(checkCategory));
      setCheckCategory(listCheckCategory);
    }
    listCheckCategory = Array.from(new Set(listCheckCategory));
    for (let value of listCheckCategory) {
      categoryIds.push(Number(value));
    }
    let defaultCategoryIds: number[] = [];
    if (product) {
      Array.from(product.categories).forEach((category) => {
        defaultCategoryIds = [...defaultCategoryIds, category.id];
      });
    }
    categoryIds.length > 0
      ? setValue('categoryIds', categoryIds)
      : setValue('categoryIds', defaultCategoryIds);
  };

  const onCategoriesSelected = (event: React.MouseEvent<HTMLElement>, id: number) => {
    let temp = getValue('categoryIds') || [];
    let index = temp.indexOf(id);
    if (index > -1) {
      temp = temp.filter((item) => item !== id);
    } else {
      temp.push(id);
    }
    setValue('categoryIds', temp);
  };

  return (
    <>
      {id ? (
        <div className="choice-category">
          <ul style={{ listStyleType: 'none' }}>
            {categories.map((category, index) => (
              <li key={category.id}>
                <input
                  value={category.id || ''}
                  type="checkbox"
                  name="category"
                  checked={checkedTrue(category.id) === true ? true : false}
                  id={`checkbox-${category.id}`}
                  onChange={onSaveUpdateCategory}
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
        </div>
      ) : (
        (categories || []).map((cate) => (
          <div className="mb-3" key={cate.id}>
            <input
              type="checkbox"
              id={cate.slug}
              onClick={(event) => onCategoriesSelected(event, cate.id)}
            />
            <label className="form-check-label ps-3" htmlFor={cate.slug}>
              {' '}
              {CategoriesHierarchy(cate.id)}
            </label>
          </div>
        ))
      )}
    </>
  );
};

export default ProductCategoryMapping;
