import clsx from 'clsx';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import { SearchParams } from '../models/SearchParams';
import { ESortType, SortType } from '../models/SortType';

import styles from '@/styles/modules/search/SearchSort.module.css';

type SearchSortProps = {
  totalElements: number;
  keyword: string;
  searchParams: SearchParams;
  setSearchParams: (_searchParams: SearchParams) => void;
};

const SearchSort = ({ totalElements, searchParams, setSearchParams }: SearchSortProps) => {
  const router = useRouter();
  const [sortType, setSortType] = useState<ESortType | undefined>(searchParams.sortType);

  useEffect(() => {
    setSearchParams({ ...searchParams, sortType });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sortType]);

  const handleSelectSortType = (sortType: ESortType) => {
    setSortType(sortType);
    router.query.sortType = SortType[sortType];
    router.push(router, undefined, { shallow: true });
  };

  return (
    <div className={styles['search-sort']}>
      <div className={styles['search-total']}>
        <span>{totalElements}</span> products are found by keyword &quot;
        <span>{searchParams.keyword}</span>
        &quot;
      </div>

      <div className={styles['search-action']}>
        <div>Sort by</div>
        <div className={styles['search-action-sort']}>
          <div className={styles['search-current']}>
            <span>
              {(sortType === ESortType.default || sortType === undefined) && 'Default'}
              {sortType === ESortType.priceAsc && 'Price: Low to High'}
              {sortType === ESortType.priceDesc && 'Price: High to Low'}
            </span>{' '}
            <i className="bi bi-chevron-down"></i>
          </div>
          <div className={styles['search-dropdown']}>
            <div
              className={clsx(
                styles['search-dropdown-item'],
                (sortType === ESortType.default || sortType === undefined) && styles['active']
              )}
              onClick={() => handleSelectSortType(ESortType.default)}
            >
              Default
            </div>
            <div
              className={clsx(
                styles['search-dropdown-item'],
                sortType === ESortType.priceAsc && styles['active']
              )}
              onClick={() => handleSelectSortType(ESortType.priceAsc)}
            >
              Price: Low to High
            </div>
            <div
              className={clsx(
                styles['search-dropdown-item'],
                sortType === ESortType.priceDesc && styles['active']
              )}
              onClick={() => handleSelectSortType(ESortType.priceDesc)}
            >
              Price: High to Low
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SearchSort;
