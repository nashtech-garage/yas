import Head from 'next/head';
import { useRouter } from 'next/router';
import queryString from 'query-string';
import { ChangeEvent, useEffect, useRef, useState } from 'react';
import { Container } from 'react-bootstrap';
import Row from 'react-bootstrap/Row';
import ReactPaginate from 'react-paginate';

import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import ProductItems from '../../common/items/ProductItems';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';
import { Category } from '../../modules/catalog/models/Category';
import { ProductThumbnail } from '../../modules/catalog/models/ProductThumbnail';
import { getCategories } from '../../modules/catalog/services/CategoryService';
import { getProductByMultiParams } from '../../modules/catalog/services/ProductService';

import styles from '../../styles/productList.module.css';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'Prouct List',
    url: '#',
  },
];

const CATEGORY_SLUG = 'categorySlug';

const ProductList = () => {
  const router = useRouter();
  const [products, setProduct] = useState<ProductThumbnail[]>([]);
  const [cates, setCates] = useState<Category[]>([]);
  const [totalPage, setTotalPage] = useState<number>(0);
  const [pageNo, setPageNo] = useState<number>(0);

  const [filters, setFilters] = useState<any>(null);
  const inputSearchRef = useRef<HTMLInputElement>(null);
  const inputStartPriceRef = useRef<HTMLInputElement>(null);
  const inputEndPriceRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (Object.keys(router.query).length && router.query.categorySlug) {
      const categorySlugValue = router.query.categorySlug as string;
      handleFilter(CATEGORY_SLUG, categorySlugValue);
    }
    getCategories().then((res) => {
      setCates(res);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!Object.keys(router.query).length) {
      setPageNo(0);
    }
    setFilters(router.query);
  }, [router.query]);

  useEffect(() => {
    if (Object.keys(router.query).length && router.query.categorySlug && filters == null) {
      return;
    }
    let predicates = queryString.stringify({ ...filters, pageNo: pageNo });
    getProductByMultiParams(predicates).then((res) => {
      setProduct(res.productContent);
      setTotalPage(res.totalPages);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
    pushParamsToRouter('pageNo', selected);
  };

  const handleClearFilter = () => {
    setPageNo(0);
    router.push({
      pathname: '/products',
    });
    if (inputSearchRef.current) {
      inputSearchRef.current.value = '';
    }
    if (inputStartPriceRef.current) {
      inputStartPriceRef.current.value = '';
    }
    if (inputEndPriceRef.current) {
      inputEndPriceRef.current.value = '';
    }
  };

  const handleFilter = (key: string, value: string | number | undefined) => {
    setPageNo(0);
    pushParamsToRouter(key, value);
  };

  const pushParamsToRouter = (key: string, value: string | number | undefined) => {
    router.push({
      pathname: '/products',
      query: {
        ...filters,
        [key]: value,
      },
    });
  };

  return (
    <Container>
      <Head>
        <title>Product List</title>
      </Head>
      <BreadcrumbComponent props={crumb} />

      <div className="wrapper py-5">
        <div className="container-xxl">
          <div className="row">
            <div className="d-none d-lg-block col-lg-3 ">
              <div className={`${styles['filter-card']} mb-3`}>
                <div className="d-flex justify-content-between">
                  <h3 className={`${styles['filter-title']}`}>Filter By</h3>
                  <button type="button" className="btn btn-secondary" onClick={handleClearFilter}>
                    Clear fillter
                  </button>
                </div>

                <div>
                  <h3 className={`${styles['sub-title']}`}>Categories</h3>
                  <div>
                    <ul className="ps-3">
                      {cates.map((cate) => (
                        <li
                          key={cate.id}
                          style={{ cursor: 'pointer' }}
                          className={`d-inline-block my-2 me-2 px-3 border border-secondary rounded-pill ${styles['hover-category']}`}
                          onClick={() => {
                            handleFilter(CATEGORY_SLUG, cate.slug);
                          }}
                        >
                          {cate.name}
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>
                <div>
                  <h5 className={`${styles['sub-title']}`}>Price</h5>
                  <div className="d-flex align-items-center">
                    <div className="form-floating">
                      <input
                        type="number"
                        className="form-control"
                        id="priceFrom"
                        placeholder="From"
                        min={0}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => {
                          handleFilter('startPrice', Number(e.target.value));
                        }}
                        ref={inputStartPriceRef}
                      />
                      <label htmlFor="priceFrom">From</label>
                    </div>
                    <div className="form-floating">
                      <input
                        type="number"
                        className="form-control "
                        id="priceTo"
                        placeholder="To"
                        min={0}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => {
                          handleFilter('endPrice', Number(e.target.value));
                        }}
                        ref={inputEndPriceRef}
                      />
                      <label htmlFor="priceTo">To</label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div className="col-lg-9">
              <div className={styles['filter-sort-grid']}>
                <div className="d-flex justify-content-between align-items-center">
                  <div className="d-flex align-items-center gap-10">
                    <p className="mb-0 d-block" style={{ width: 100 }}>
                      Sort By:
                    </p>
                    <select name="" className="form-control form-select">
                      <option value="">Featured</option>
                      <option value="">Best Selling</option>
                    </select>
                  </div>
                  <div className="input-group mb-3" style={{ width: '30rem' }}>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Search..."
                      aria-label="Search..."
                      aria-describedby="button-search"
                      ref={inputSearchRef}
                      onChange={(e: ChangeEvent<HTMLInputElement>) => {
                        handleFilter('productName', e.target.value);
                      }}
                    />
                  </div>
                </div>
              </div>
              <div className="products-list" style={{ marginTop: 10 }}>
                {/* PRODUCT VIEW */}
                <Row xs={5}>
                  <ProductItems products={products} />
                </Row>
                {/* PAGINATION */}
                <ReactPaginate
                  forcePage={pageNo}
                  previousLabel={'Previous'}
                  nextLabel={'Next'}
                  pageCount={totalPage}
                  onPageChange={changePage}
                  containerClassName={'pagination-container'}
                  previousClassName={'previous-btn'}
                  nextClassName={'next-btn'}
                  disabledClassName={'pagination-disabled'}
                  activeClassName={'pagination-active'}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </Container>
  );
};

export default ProductList;
