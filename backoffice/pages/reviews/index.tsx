import type { NextPage } from 'next';
import type { Brand } from '../../modules/catalog/models/Brand';
import { useState, useEffect } from 'react';
import { Button, Stack, Table, Form, InputGroup } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import { getProducts } from '../../modules/catalog/services/ProductService';
import { getRatingsByProductId } from '../../modules/catalog/services/RatingService';
import moment from 'moment';
import styles from '../../styles/Filter.module.css';
import { toast } from 'react-toastify';
import type { Product } from '../../modules/catalog/models/Product';
import type { Rating } from '../../modules/catalog/models/Rating';
import { getBrands } from '../../modules/catalog/services/BrandService';
import { FaSearch } from 'react-icons/fa';

const Reviews: NextPage = () => {
  const [isLoading, setLoading] = useState(false);

  const [productList, setProductList] = useState<Product[]>([]);
  const [pageNoProduct, setPageNoProduct] = useState<number>(0);
  const [totalPageProduct, setTotalPageProduct] = useState<number>(1);
  const [brandName, setBrandName] = useState<string>('');
  const [productName, setProductName] = useState<string>('');
  const [productId, setProductId] = useState<number>();
  const [brandList, setBrandList] = useState<Brand[]>([]);

  const [ratingList, setRatingList] = useState<Rating[]>([]);
  const [pageNoRating, setPageNoRating] = useState<number>(0);
  const [totalPageRating, setTotalPageRating] = useState<number>(1);
  const ratingPageSize = 5;

  useEffect(() => {
    setLoading(true);
    getBrands().then((data) => {
      setBrandList(data);
      setLoading(false);
    });
  }, []);
  useEffect(() => {
    setLoading(true);

    getProducts(pageNoProduct, '', brandName).then((data) => {
      setTotalPageProduct(data.totalPages);
      setProductList(data.productContent);
      setLoading(false);
    });
  }, [pageNoProduct, brandName, productName]);
  useEffect(() => {
    if (productId) {
      getRatingsByProductId(productId, pageNoRating, ratingPageSize).then((res) => {
        setRatingList(res.ratingList);
        setTotalPageRating(res.totalPages);
      });
    }
  }, [pageNoRating]);
  useEffect(() => {
    if (productId) {
      getRatingsByProductId(productId, pageNoRating, ratingPageSize).then((res) => {
        setRatingList(res.ratingList);
        setTotalPageRating(res.totalPages);
        setPageNoRating(0);
      });
    }
  }, [productId]);

  const handleOnClickProduct = (productId: number) => {
    setProductId(productId);
  };

  const changePageProduct = ({ selected }: any) => {
    setPageNoProduct(selected);
  };
  const changePageRating = ({ selected }: any) => {
    setPageNoRating(selected);
  };

  if (isLoading) return <p>Loading...</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Product Reviews</h2>
        </div>
      </div>
      {/* Filter */}
      <div className="row mb-1">
        <div className="col ">
          {/* <Form.Label htmlFor="brand-filter">Brand: </Form.Label> */}
          <Form.Select
            id="brand-filter"
            onChange={(e) => {
              setPageNoProduct(0);
              setBrandName(e.target.value);
            }}
            className={styles.filterButton}
            defaultValue={brandName || ''}
          >
            <option value={''}>All</option>
            {brandList.map((brand) => (
              <option key={brand.id} value={brand.name}>
                {brand.name}
              </option>
            ))}
          </Form.Select>
        </div>
        <div className="search-container">
          <Form>
            <InputGroup>
              <Form.Control
                id="product-name"
                placeholder="Search name ..."
                defaultValue={productName}
                onChange={() => {}}
              />
              <Button id="seach-category" variant="danger" onClick={() => {}}>
                <FaSearch />
              </Button>
            </InputGroup>
          </Form>
        </div>
      </div>
      <div className="row mt-5 my-1">
        <div className="accordion" id="accordionExample">
          {productList.map((product, index) => (
            <div className="accordion-item" key={product.id}>
              <h2 className="accordion-header" id="headingOne">
                <button
                  className="accordion-button"
                  type="button"
                  data-bs-toggle="collapse"
                  data-bs-target={`#collapse${index}`}
                  aria-expanded="true"
                  aria-controls={`collapse${index}`}
                  onClick={() => handleOnClickProduct(product.id)}
                >
                  {product.name}
                </button>
              </h2>
              <div
                id={`collapse${index}`}
                className="accordion-collapse collapse"
                aria-labelledby="headingOne"
                data-bs-parent="#accordionExample"
              >
                <div className="accordion-body">
                  <div className="row mb-1">
                    <div className="col d-flex justify-content-end mb-2 search-container">
                      <Form>
                        <InputGroup>
                          <Form.Control
                            id="product-name"
                            placeholder="Search customer name ..."
                            defaultValue={productName}
                            onChange={() => {}}
                          />
                          <Button id="seach-category" variant="danger" onClick={() => {}}>
                            <FaSearch />
                          </Button>
                        </InputGroup>
                      </Form>
                    </div>
                  </div>
                  <Table striped bordered hover>
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th style={{ width: '45%' }}>Content</th>
                        <th>Customer Id</th>
                        <th>Customer Name</th>
                        <th>Date Post</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {ratingList.map((rating) => (
                        <tr key={rating.id}>
                          <td>{rating.id}</td>
                          <td>{rating.content}</td>
                          <td>{rating.createdBy}</td>
                          <td>
                            {rating.lastName} {rating.firstName}
                          </td>
                          <td>{moment(rating.createdOn).format('MMMM Do YYYY, h:mm:ss a')}</td>

                          <td>
                            <Stack direction="horizontal" gap={3}>
                              <button className="btn btn-outline-danger btn-sm" type="button">
                                Delete
                              </button>
                            </Stack>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                  <ReactPaginate
                    forcePage={pageNoRating}
                    previousLabel={'Previous'}
                    nextLabel={'Next'}
                    pageCount={totalPageRating}
                    onPageChange={changePageRating}
                    containerClassName={'paginationBtns'}
                    previousLinkClassName={'previousBtn'}
                    nextClassName={'nextBtn'}
                    disabledClassName={'paginationDisabled'}
                    activeClassName={'paginationActive'}
                  />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
      <ReactPaginate
        forcePage={pageNoProduct}
        previousLabel={'Previous'}
        nextLabel={'Next'}
        pageCount={totalPageProduct}
        onPageChange={changePageProduct}
        containerClassName={'paginationBtns'}
        previousLinkClassName={'previousBtn'}
        nextClassName={'nextBtn'}
        disabledClassName={'paginationDisabled'}
        activeClassName={'paginationActive'}
      />
    </>
  );
};

export default Reviews;
