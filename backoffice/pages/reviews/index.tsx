import type { NextPage } from 'next';
import Link from 'next/link';
import { useState, useEffect } from 'react';
import { Button, Stack, Table, Form } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import { getProducts } from '../../modules/catalog/services/ProductService';
import { getRatingsByProductId } from '../../modules/catalog/services/RatingService';
import moment from 'moment';
import { toast } from 'react-toastify';
import type { Product } from '../../modules/catalog/models/Product';
import type { Rating } from '../../modules/catalog/models/Rating';

const Reviews: NextPage = () => {
  const [productList, setProductList] = useState<Product[]>([]);
  const [ratingList, setRatingList] = useState<Rating[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [pageNoProduct, setPageNoProduct] = useState<number>(0);
  const [totalPageProduct, setTotalPageProduct] = useState<number>(1);

  const [pageNoRating, setPageNoRating] = useState<number>(0);
  const [totalPageRating, setTotalPageRating] = useState<number>(1);
  useEffect(() => {
    setLoading(true);

    getProducts(pageNo, '', '').then((data) => {
      setTotalPageProduct(data.totalPages);
      setProductList(data.productContent);
      setLoading(false);
    });
  }, []);

  const handleOnClickProduct = (productId: number) => {
    getRatingsByProductId(productId, pageNo, pageSize).then((res) => {
      setRatingList(res.ratingList);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    });
  };

  if (isLoading) return <p>Loading...</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Product Reviews</h2>
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
                  onClick={}
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
                            {rating.lastName} + {rating.firstName}
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
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
      {/* <ReactPaginate
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
      /> */}
    </>
  );
};

export default Reviews;
