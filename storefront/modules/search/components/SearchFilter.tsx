import { useRouter } from 'next/router';
import { useState } from 'react';

import { Aggregations } from '../models/Aggregations';
import { SearchParams } from '../models/SearchParams';

import styles from '@/styles/modules/search/SearchFilter.module.css';

export interface SearchFilterProps {
  aggregations: Aggregations;
  searchParams: SearchParams;
  setSearchParams: (_searchParams: SearchParams) => void;
}

const SearchFilter = ({ aggregations, searchParams, setSearchParams }: SearchFilterProps) => {
  const router = useRouter();
  const [rangePrice, setRangePrice] = useState<{
    min: number;
    max: number;
  }>({
    min: searchParams.minPrice ?? 0,
    max: searchParams.maxPrice ?? 0,
  });
  const [category, setCategory] = useState<string | undefined>(searchParams.category);
  const [brand, setBrand] = useState<string | undefined>(searchParams.brand);

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
    const { min, max } = rangePrice;

    if (min > 0 && max > 0) {
      if (min < max) {
        setSearchParams({ ...searchParams, minPrice: min, maxPrice: max });
        router.query.minPrice = min.toString();
        router.query.maxPrice = max.toString();
        delete router.query.page;
      }
    } else if (min > 0 && max === 0) {
      setSearchParams({ ...searchParams, minPrice: min, maxPrice: undefined });
      router.query.minPrice = min.toString();
      delete router.query.maxPrice;
      delete router.query.page;
    } else if (min === 0 && max > 0) {
      setSearchParams({ ...searchParams, minPrice: undefined, maxPrice: max });
      router.query.maxPrice = max.toString();
      delete router.query.minPrice;
      delete router.query.page;
    }
    router
      .replace(
        {
          pathname: router.pathname,
          query: router.query,
        },
        undefined,
        { shallow: true }
      )
      .then((res) => console.log(res))
      .catch((err) => console.log(err));
  };

  const handleFilter = (
    e: React.ChangeEvent<HTMLInputElement>,
    item: string,
    fieldName: string
  ) => {
    const { checked } = e.target;
    const currentParams = searchParams[fieldName as keyof SearchParams] as string | undefined;
    let queries = currentParams ? currentParams.split(',') : [];

    if (checked) {
      queries.push(item);
    } else {
      queries = queries.filter((param) => param !== item);
    }

    const updatedQuery = queries.join(',');

    if (fieldName === 'brand') {
      setBrand(queries.length > 0 ? updatedQuery : undefined);
    } else if (fieldName === 'category') {
      setCategory(queries.length > 0 ? updatedQuery : undefined);
    }

    if (queries.length > 0) {
      setSearchParams({ ...searchParams, [fieldName]: updatedQuery });
      router.query[fieldName] = updatedQuery;
    } else {
      setSearchParams({ ...searchParams, [fieldName]: undefined });
      delete router.query[fieldName];
    }

    delete router.query.page;
    router
      .replace(
        {
          pathname: router.pathname,
          query: router.query,
        },
        undefined,
        { shallow: true }
      )
      .then((res) => console.log(res))
      .catch((err) => console.log(err));
  };

  const handleClearAll = () => {
    if (rangePrice.min || rangePrice.max || category || brand) {
      setRangePrice({ min: 0, max: 0 });
      setSearchParams({
        ...searchParams,
        minPrice: undefined,
        maxPrice: undefined,
        category: undefined,
        brand: undefined,
      });
      setCategory(undefined);
      setBrand(undefined);
      delete router.query.minPrice;
      delete router.query.maxPrice;
      delete router.query.category;
      delete router.query.brand;
      delete router.query.page;
      router
        .replace(
          {
            pathname: router.pathname,
            query: router.query,
          },
          undefined,
          { shallow: true }
        )
        .then((res) => console.log(res))
        .catch((err) => console.log(err));
    }
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
                  checked={category?.toLowerCase().includes(item.toLowerCase())}
                  onChange={(e) => handleFilter(e, item, 'category')}
                />
                <label htmlFor={`category-${index}`}>
                  {item} ({aggregations['categories'][item]})
                </label>
              </div>
            ))}
          </div>
        </div>
      )}

      {aggregations['brands'] && Object.keys(aggregations['brands']).length > 0 && (
        <div className={styles['filter-group']}>
          <div className={styles['filter-group__title']}>Brand</div>
          <div className={styles['filter-group__list']}>
            {Object.keys(aggregations['brands']).map((item, index) => (
              <div className={styles['filter-group__list-item']} key={item}>
                <input
                  type="checkbox"
                  id={`brand-${index}`}
                  value={item.toLowerCase()}
                  checked={brand?.toLowerCase().includes(item.toLowerCase())}
                  onChange={(e) => handleFilter(e, item, 'brand')}
                />
                <label htmlFor={`brand-${index}`}>
                  {item.charAt(0).toUpperCase() + item.slice(1)} ({aggregations['brands'][item]})
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
