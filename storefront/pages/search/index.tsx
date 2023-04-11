import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import { toast } from 'react-toastify';

import BreadcrumbComponent from '@/common/components/BreadcrumbComponent';
import ProductCard from '@/common/components/ProductCard';
import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';
import SearchFilter from '@/modules/search/components/SearchFilter';
import SearchSort from '@/modules/search/components/SearchSort';
import { ProductSearchResult } from '@/modules/search/models/ProductSearchResult';
import { SearchParams } from '@/modules/search/models/SearchParams';
import { SortType } from '@/modules/search/models/SortType';
import { searchProducts } from '@/modules/search/services/SearchService';

import styles from '@/styles/modules/search/SearchPage.module.css';

const handleSortType = (sortType: string | string[] | undefined) => {
  if (sortType) {
    if (sortType === SortType.default) {
      return 'DEFAULT';
    }
    if (sortType === SortType.priceAsc) {
      return 'PRICE_ASC';
    }
    if (sortType === SortType.priceDesc) {
      return 'PRICE_DESC';
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
      })
      .catch((_err) => {
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
                <SearchSort />

                <Row xs={4} xl={5} className={styles['search-result__list']}>
                  <Col>
                    <ProductCard
                      className={['products-page']}
                      product={{
                        id: 1,
                        name: 'Product 1',
                        price: 100000,
                        thumbnailUrl: 'https://picsum.photos/200/300',
                        slug: 'product-1',
                      }}
                    />
                  </Col>
                  <Col>
                    <ProductCard
                      className={['products-page']}
                      product={{
                        id: 1,
                        name: 'Product 1',
                        price: 100000,
                        thumbnailUrl: 'https://picsum.photos/200/300',
                        slug: 'product-1',
                      }}
                    />
                  </Col>
                  <Col>
                    <ProductCard
                      className={['products-page']}
                      product={{
                        id: 1,
                        name: 'Product 1',
                        price: 100000,
                        thumbnailUrl: 'https://picsum.photos/200/300',
                        slug: 'product-1',
                      }}
                    />
                  </Col>
                  <Col>
                    <ProductCard
                      className={['products-page']}
                      product={{
                        id: 1,
                        name: 'Product 1',
                        price: 100000,
                        thumbnailUrl: 'https://picsum.photos/200/300',
                        slug: 'product-1',
                      }}
                    />
                  </Col>
                </Row>
              </div>
            </>
          )}

          {products.length === 0 && (
            <div className={styles['search-result__empty']}>
              <h3>There is no product match your search</h3>
            </div>
          )}
        </div>
      </Container>
    </div>
  );
};

export default SearchPage;
