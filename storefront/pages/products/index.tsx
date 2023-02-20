import Head from 'next/head';
import { useState, useEffect } from 'react';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';
import { ProductThumbnail } from '../../modules/catalog/models/ProductThumbnail';
import { Category } from '../../modules/catalog/models/Category';
import { getFeaturedProducts } from '../../modules/catalog/services/ProductService';
import { getCategories } from '../../modules/catalog/services/CategoryService';
import styles from '../../styles/productList.module.css';
import Row from 'react-bootstrap/Row';
import ProductItems from '../../common/items/ProductItems';
import ReactPaginate from 'react-paginate';

const ProductList = () => {
  const [products, setProduct] = useState<ProductThumbnail[]>([]);
  const [cates, setCates] = useState<Category[]>([]);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(0);

  useEffect(() => {
    getFeaturedProducts(pageNo).then((res) => {
      setProduct(res.productList);
      setTotalPage(res.totalPage);
    });

    getCategories().then((res) => {
      setCates(res);
    });
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };
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

  return (
    <>
      <Head>
        <title>Product List</title>
      </Head>
      <ToastContainer style={{ marginTop: '70px' }} />
      <BreadcrumbComponent props={crumb} />
      <div className="wrapper py-5">
        <div className="container-xxl">
          <div className="row">
            <div className="d-none d-lg-block col-lg-3 ">
              <div className={`${styles['filter-card']} mb-3`}>
                <h3 className={`${styles['filter-title']}`}>Filter By</h3>
                <div>
                  <h3 className={`${styles['sub-title']}`}>Categories</h3>
                  <div>
                    <ul className="ps-3">
                      {cates.map((cate) => (
                        <>
                          <li>{cate.name}</li>
                        </>
                      ))}
                    </ul>
                  </div>
                </div>
                <div>
                  <h5 className={`${styles['sub-title']}`}>Price</h5>
                  <div className="d-flex align-items-center">
                    <div className="form-floating">
                      <input
                        type="email"
                        className="form-control"
                        id="floatingInput"
                        placeholder="From"
                      />
                      <label htmlFor="priceFrom">From</label>
                    </div>
                    <div className="form-floating">
                      <input
                        type="email"
                        className="form-control "
                        id="floatingInput2"
                        placeholder="To"
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
                  <div className="d-flex align-items-center gap-10">
                    <p className="totalproducts" style={{ marginTop: 10, paddingRight: '20px' }}>
                      1233 Products
                    </p>
                  </div>
                </div>
              </div>
              <div className="products-list" style={{ marginTop: 10 }}>
                <Row xs={1} sm={1} md={2} lg={3} className="g-2">
                  <ProductItems products={products} />
                </Row>
                <ReactPaginate
                  forcePage={pageNo}
                  previousLabel={'Previous'}
                  nextLabel={'Next'}
                  pageCount={totalPage}
                  onPageChange={changePage}
                  containerClassName={'paginationBtns'}
                  previousLinkClassName={'previousBtn'}
                  nextClassName={'nextBtn'}
                  disabledClassName={'paginationDisabled'}
                  activeClassName={'paginationActive'}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default ProductList;
