import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Col, Container, Image, Row } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import { toast } from 'react-toastify';

import noResultImg from '@/asset/images/no-result.png';
import BreadcrumbComponent from '@/common/components/BreadcrumbComponent';
import ProductCard from '@/common/components/ProductCard';
import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';
import SearchFilter from '@/modules/search/components/SearchFilter';
import SearchSort from '@/modules/search/components/SearchSort';
import { ProductSearchResult } from '@/modules/search/models/ProductSearchResult';
import { SearchParams } from '@/modules/search/models/SearchParams';
import { ESortType, SortType } from '@/modules/search/models/SortType';
import { searchProducts } from '@/modules/search/services/SearchService';

import styles from '@/styles/modules/search/SearchPage.module.css';

const handleSortType = (sortType: string | string[] | undefined) => {
  if (sortType) {
    if (sortType === SortType.DEFAULT) {
      return ESortType.default;
    }
    if (sortType === SortType.PRICE_ASC) {
      return ESortType.priceAsc;
    }
    if (sortType === SortType.PRICE_DESC) {
      return ESortType.priceDesc;
    }
  }
  return undefined;
};

const SearchPage = () => {
  const router = useRouter();
  const { keyword, category, attribute, minPrice, maxPrice, sortType } = router.query;

  const [searchParams, setSearchParams] = useState<SearchParams>({
    keyword: '',
    category: undefined,
    attribute: undefined,
    minPrice: undefined,
    maxPrice: undefined,
    sortType: undefined,
  });
  const [products, setProducts] = useState<ProductSearchResult[]>([]);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(0);

  useEffect(() => {
    setSearchParams({
      keyword: keyword ? (keyword as string) : '',
      category: category ? (category as string) : undefined,
      attribute: attribute ? (attribute as string) : undefined,
      minPrice: minPrice && +minPrice > 0 ? +minPrice : undefined,
      maxPrice: maxPrice && +maxPrice > 0 ? +maxPrice : undefined,
      sortType: handleSortType(sortType),
    });
  }, [keyword, category, attribute, minPrice, maxPrice, sortType]);

  useEffect(() => {
    if (searchParams.keyword) {
      fetchSearchResult(searchParams);
    }
  }, [
    searchParams,
    searchParams.keyword,
    searchParams.category,
    searchParams.attribute,
    searchParams.minPrice,
    searchParams.maxPrice,
    searchParams.sortType,
  ]);

  const fetchSearchResult = (data: SearchParams) => {
    searchProducts(data)
      .then((res) => {
        setProducts(res.products);
        setPageNo(res.pageNo);
        setTotalPage(res.totalPages);
        setTotalElements(res.totalElements);
      })
      .catch((_error) => {
        toast.error('Something went wrong, please try again later');
      });
  };

  const crumb: BreadcrumbModel[] = [
    {
      pageName: 'Home',
      url: '/',
    },
    {
      pageName: `Search result for "${searchParams.keyword}"`,
      url: '#',
    },
  ];

  return (
    <div className={styles['search-page']}>
      <Container className={styles['search-container']}>
        <BreadcrumbComponent props={crumb} />

        <div className={styles['search-wrapper']}>
          {products.length > 0 && (
            <>
              <SearchFilter />

              <div className={styles['search-result']}>
                <SearchSort
                  totalElements={totalElements}
                  keyword={searchParams.keyword}
                  searchParams={searchParams}
                  setSearchParams={setSearchParams}
                />

                <Row xs={4} xl={5} className={styles['search-result__list']}>
                  {products.map((product) => (
                    <Col key={product.id}>
                      <ProductCard
                        className={['products-page']}
                        product={{
                          id: product.id,
                          name: product.name,
                          price: product.price,
                          thumbnailUrl: 'https://picsum.photos/200/300',
                          slug: product.slug,
                        }}
                        thumbnailId={product.thumbnailId}
                      />
                    </Col>
                  ))}
                </Row>

                {totalPage > 1 && (
                  <ReactPaginate
                    forcePage={pageNo}
                    previousLabel={'Previous'}
                    nextLabel={'Next'}
                    pageCount={totalPage}
                    onPageChange={({ selected }) => {
                      setPageNo(selected);
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
          )}

          {products.length === 0 && (
            <div className="text-center flex-grow-1 my-5">
              <Image
                src={noResultImg.src}
                alt="No result"
                style={{ width: '134px', height: '134ps' }}
              />
              <h5 className="text-black mb-2">No result is found</h5>
              <h5 className="mb-5">Try using more generic keywords</h5>
            </div>
          )}
        </div>
      </Container>
    </div>
  );
};

export default SearchPage;
