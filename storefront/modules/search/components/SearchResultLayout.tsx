import { useRouter } from 'next/router';
import ReactPaginate from 'react-paginate';

import { Aggregations } from '../models/Aggregations';
import { SearchParams } from '../models/SearchParams';
import SearchFilter from './SearchFilter';
import SearchSort from './SearchSort';

import styles from '@/styles/modules/search/SearchPage.module.css';

type SearchResultLayoutProps = {
  children: React.ReactNode;
  aggregations: Aggregations;
  searchParams: SearchParams;
  setSearchParams: (_searchParams: SearchParams) => void;
  pageNo: number;
  setPageNo: (_pageNo: number) => void;
  totalPage: number;
  totalElements: number;
  isFilter: boolean;
};

const SearchResultLayout = ({
  children,
  aggregations,
  searchParams,
  setSearchParams,
  pageNo,
  setPageNo,
  totalPage,
  totalElements,
  isFilter,
}: SearchResultLayoutProps) => {
  const router = useRouter();

  return (
    <>
      <SearchFilter
        aggregations={aggregations}
        searchParams={searchParams}
        setSearchParams={setSearchParams}
      />

      <div className={styles['search-result']}>
        <SearchSort
          totalElements={totalElements}
          keyword={searchParams.keyword}
          searchParams={searchParams}
          isFilter={isFilter}
          setSearchParams={setSearchParams}
          setPageNo={setPageNo}
        />

        {children}

        {totalPage > 1 && (
          <ReactPaginate
            forcePage={pageNo}
            previousLabel={'Previous'}
            nextLabel={'Next'}
            pageCount={totalPage}
            onPageChange={({ selected }) => {
              setPageNo(selected);
              setSearchParams({ ...searchParams, page: selected });
              router.query.page = selected.toString();
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
            }}
            containerClassName={'pagination-container'}
            previousClassName={'previous-btn'}
            nextClassName={'next-btn'}
            disabledClassName={'pagination-disabled'}
            activeClassName={'pagination-active'}
          />
        )}
      </div>
    </>
  );
};

export default SearchResultLayout;
