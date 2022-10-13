import { ProductPost } from '../models/ProductPost';
import { UseFormSetValue, UseFormGetValues } from 'react-hook-form';
import { useEffect, useState } from 'react';
import { CategoryGet } from '../models/CategoryGet';
import { getCategories } from '../services/CategoryService';

type Props = {
  setValue: UseFormSetValue<ProductPost>;
  getValue: UseFormGetValues<ProductPost>;
};
const ProductCategoryMapping = ({ setValue, getValue }: Props) => {
  const [selectedCate, setSelectedCate] = useState<number[]>([]);
  const [categories, setCategories] = useState<CategoryGet[]>([]);

  useEffect(() => {
    getCategories().then((data) => {
      setCategories(data);
    });
  }, []);

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
    </>
  );
};

export default ProductCategoryMapping;
