import { useRouter } from 'next/router';
import { useState } from 'react';

import { Aggregations } from '../models/Aggregations';
import { SearchParams } from '../models/SearchParams';

import styles from '@/styles/modules/search/SearchFilter.module.css';

export interface SearchFilterProps {
  aggregations: Aggregations;
  searchParams: SearchParams;
  setSearchParams: (_searchParams: SearchParams) => void;
  setPageNo: (_pageNo: number) => void;
}

const SearchFilter = ({
  aggregations,
  searchParams,
  setSearchParams,
  setPageNo,
}: SearchFilterProps) => {
  const router = useRouter();
  const [rangePrice, setRangePrice] = useState<{
    min: number;
    max: number;
  }>({
    min: searchParams.minPrice ?? 0,
    max: searchParams.maxPrice ?? 0,
  });
  const [category, setCategory] = useState<string | undefined>(searchParams.category);

  const handleChangePrice = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    if (value != '' && /^\d+$/.test(value) && +value > 0) {
      if (name === 'min') {
        setRangePrice({ ...rangePrice, min: +value });
      } else {
        setRangePrice({ ...rangePrice, max: +value });
      }
    } else {
      if (name === 'min') {
        setRangePrice({ ...rangePrice, min: 0 });
      } else {
        setRangePrice({ ...rangePrice, max: 0 });
      }
    }
  };

  const handleClickApplyPriceRange = () => {
    if (rangePrice.min > 0 && rangePrice.max > 0) {
      if (rangePrice.min < rangePrice.max) {
        setSearchParams({
          ...searchParams,
          minPrice: rangePrice.min,
          maxPrice: rangePrice.max,
          page: undefined,
        });
        setPageNo(0);
        router.query.minPrice = rangePrice.min.toString();
        router.query.maxPrice = rangePrice.max.toString();
        delete router.query.page;
        router.push(router, undefined, { shallow: true });
      }
    } else if (rangePrice.min > 0 && rangePrice.max === 0) {
      setSearchParams({
        ...searchParams,
        minPrice: rangePrice.min,
        maxPrice: undefined,
        page: undefined,
      });
      setPageNo(0);
      router.query.minPrice = rangePrice.min.toString();
      delete router.query.maxPrice;
      delete router.query.page;
      router.push(router, undefined, { shallow: true });
    } else if (rangePrice.min === 0 && rangePrice.max > 0) {
      setSearchParams({
        ...searchParams,
        minPrice: undefined,
        maxPrice: rangePrice.max,
        page: undefined,
      });
      setPageNo(0);
      delete router.query.minPrice;
      router.query.maxPrice = rangePrice.max.toString();
      delete router.query.page;
      router.push(router, undefined, { shallow: true });
    }
  };

  const handleFilterCateogry = (e: React.ChangeEvent<HTMLInputElement>, item: string) => {
    if (e.target.checked) {
      setCategory(item);
      setSearchParams({
        ...searchParams,
        category: item,
      });
      setPageNo(0);
      router.query.category = item.toLowerCase();
      delete router.query.page;
      router.push(router, undefined, { shallow: true });
    } else {
      setCategory(undefined);
      setSearchParams({
        ...searchParams,
        category: undefined,
      });
      setPageNo(0);
      delete router.query.category;
      delete router.query.page;
      router.push(router, undefined, { shallow: true });
    }
  };

  const handleClearAll = () => {
    setRangePrice({ min: 0, max: 0 });
    setSearchParams({
      ...searchParams,
      minPrice: undefined,
      maxPrice: undefined,
      category: undefined,
    });
    setCategory(undefined);
    setPageNo(0);
    delete router.query.minPrice;
    delete router.query.maxPrice;
    delete router.query.category;
    delete router.query.page;
    router.push(router, undefined, { shallow: true });
  };

  return (
    <div className={styles['search-filter']}>
      <div className={styles['title']}>
        <i className="bi bi-funnel"></i>
        <span>Search filter</span>
      </div>

      {aggregations['categories'] && Object.keys(aggregations['categories']).length > 0 && (
        <div className={styles['filter-group']}>
          <div className={styles['filter-group__title']}>Category</div>
          <div className={styles['filter-group__list']}>
            {Object.keys(aggregations['categories']).map((item, index) => (
              <div className={styles['filter-group__list-item']} key={item}>
                <input
                  type="checkbox"
                  id={`category-${index}`}
                  value={item.toLowerCase()}
                  checked={category?.toLowerCase() === item.toLowerCase()}
                  onChange={(e) => handleFilterCateogry(e, item)}
                />
                <label htmlFor={`category-${index}`}>
                  {item} ({aggregations['categories'][item]})
                </label>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className={styles['filter-group']}>
        <div className={styles['filter-group__title']}>Price range</div>
        <div className={styles['filter-group__range']}>
          <input
            name="min"
            type="text"
            placeholder="₫ From"
            value={!rangePrice.min ? '' : rangePrice.min}
            onChange={handleChangePrice}
          />
          <span>-</span>
          <input
            name="max"
            type="text"
            placeholder="₫ To"
            value={!rangePrice.max ? '' : rangePrice.max}
            onChange={handleChangePrice}
          />
        </div>
        <button className={styles['filter-group__button']} onClick={handleClickApplyPriceRange}>
          Apply
        </button>
      </div>
      <div className={styles['filter-group']}>
        <button className={styles['filter-group__button']} onClick={handleClearAll}>
          Clear All
        </button>
      </div>
    </div>
  );
};

export default SearchFilter;
