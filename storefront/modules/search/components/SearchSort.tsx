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
  isFilter: boolean;
  setSearchParams: (_searchParams: SearchParams) => void;
  setPageNo: (_pageNo: number) => void;
};

const SearchSort = ({
  totalElements,
  searchParams,
  isFilter,
  setSearchParams,
  setPageNo,
}: SearchSortProps) => {
  const router = useRouter();
  const [sortType, setSortType] = useState<ESortType | undefined>(searchParams.sortType);

  useEffect(() => {
    setSearchParams({ ...searchParams, sortType, page: undefined });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sortType]);

  const handleSelectSortType = async (sortType: ESortType) => {
    setSortType(sortType);
    setPageNo(0);
    router.query.sortType = SortType[sortType];
    delete router.query.page;
    await router.replace(
      {
        pathname: router.pathname,
        query: router.query,
      },
      undefined,
      { shallow: true }
    );
  };

  return (
    <div className={styles['search-sort']}>
      <div className={styles['search-total']}>
        <span>{totalElements}</span> products are found by keyword &quot;
        <span>{searchParams.keyword}</span>
        &quot; {isFilter && 'with filter'}
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
