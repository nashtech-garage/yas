import type { NextPage } from 'next';
import type { Brand } from '../../modules/catalog/models/Brand';
import { useState, useEffect } from 'react';
import { Button, Stack, Table, Form, InputGroup } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import { getRatings, deleteRatingById } from '../../modules/rating/services/RatingService';
import moment from 'moment';
import styles from '../../styles/Filter.module.css';
import { toast } from 'react-toastify';
import type { Rating } from '../../modules/rating/models/Rating';
import { getBrands } from '../../modules/catalog/services/BrandService';
import { FaSearch } from 'react-icons/fa';

const Reviews: NextPage = () => {
  let typingTimeOutRef: null | ReturnType<typeof setTimeout> = null;
  const [isLoading, setLoading] = useState(false);

  const [totalPageProduct, setTotalPageProduct] = useState<number>(1);
  const [brandName, setBrandName] = useState<string>('');
  const [productName, setProductName] = useState<string>('');
  const [brandList, setBrandList] = useState<Brand[]>([]);

  const [ratingList, setRatingList] = useState<Rating[]>([]);
  const [customerName, setCustomerName] = useState<string>('');
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [isDelete, setDelete] = useState<boolean>(false);
  const ratingPageSize = 10;

  useEffect(() => {
    setLoading(true);
    getBrands().then((data) => {
      setBrandList(data);
      setLoading(false);
    });
  }, []);

  useEffect(() => {
    getRatings('', customerName, pageNo, ratingPageSize).then((res) => {
      setRatingList(res.ratingList);
      setTotalPage(res.totalPages);
      setPageNo(0);
      setCustomerName('');
    });
  }, [isDelete, pageNo, customerName]);

  const handlePageChange = ({ selected }: any) => {
    setPageNo(selected);
  };

  //searching handler
  const searchingProductHandler = (e: any) => {
    if (typingTimeOutRef) {
      clearTimeout(typingTimeOutRef);
    }
    typingTimeOutRef = setTimeout(() => {
      setProductName(e.target.value);
    }, 500);
  };

  const searchingCustomerHandler = (e: any) => {
    if (typingTimeOutRef) {
      clearTimeout(typingTimeOutRef);
    }
    typingTimeOutRef = setTimeout(() => {
      setCustomerName(e.target.value);
      setPageNo(0);
    }, 500);
  };

  const handleDeleteRating = (ratingId: number) => {
    deleteRatingById(ratingId).then(() => {
      toast.success('Delete rating successfully');
      setDelete(!isDelete);
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
      {/* Filter */}
      <div className="d-flex flex-row mb-1 gap-3">
        <div className="col-6">
          <div className="d-flex flex-row mb-4">
            <Form.Label htmlFor="Created-From" className="label">
              Created From:{' '}
            </Form.Label>
            <input
              id="startDate"
              className="form-control w-50"
              type="date"
              defaultValue={'1970-01-01'}
            />
          </div>
          <div className="d-flex flex-row mb-4">
            <Form.Label htmlFor="createdTo" className="label">
              Created To:{' '}
            </Form.Label>
            <input
              id="startDate"
              className="form-control  w-50"
              type="date"
              defaultValue={new Date().toISOString().substr(0, 10)}
            />
          </div>
          <div className="d-flex flex-row mb-4">
            <Form.Label htmlFor="brand-filter" className="label">
              Brand:{' '}
            </Form.Label>
            <Form.Select
              id="brand-filter"
              onChange={(e) => {
                setPageNo(0);
                setBrandName(e.target.value);
              }}
              className="w-75"
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
        </div>
        <div className="col-6 ">
          <div className="mb-4">
            <Form className="d-flex flex-row ">
              <Form.Label htmlFor="product" className="mx-2 pt-2">
                Product:{' '}
              </Form.Label>
              <InputGroup>
                <Form.Control
                  id="product-name"
                  placeholder="Search product name ..."
                  defaultValue={productName}
                  onChange={(e) => searchingProductHandler(e)}
                />
                <Button
                  id="seach-product"
                  variant="danger"
                  onClick={(e) => searchingProductHandler(e)}
                >
                  <FaSearch />
                </Button>
              </InputGroup>
            </Form>
          </div>
          <div className="mb-4">
            <Form className="d-flex flex-row ">
              <Form.Label htmlFor="cusomter" className="mx-2 pt-2">
                Customer:{' '}
              </Form.Label>
              <InputGroup>
                <Form.Control
                  id="product-name"
                  placeholder="Search customer name ..."
                  defaultValue={productName}
                  onChange={(e) => searchingProductHandler(e)}
                />
                <Button
                  id="seach-customer"
                  variant="danger"
                  onClick={(e) => searchingProductHandler(e)}
                >
                  <FaSearch />
                </Button>
              </InputGroup>
            </Form>
          </div>
          <div className="mb-4">
            <Form className="d-flex flex-row ">
              <Form.Label htmlFor="createdTo" className="mx-2 pt-2">
                Message:{' '}
              </Form.Label>
              <InputGroup>
                <Form.Control
                  id="product-name"
                  placeholder="Search message ..."
                  defaultValue={productName}
                  onChange={(e) => searchingProductHandler(e)}
                />
                <Button
                  id="seach-customer"
                  variant="danger"
                  onClick={(e) => searchingProductHandler(e)}
                >
                  <FaSearch />
                </Button>
              </InputGroup>
            </Form>
          </div>
        </div>
      </div>
      <div className="row mt-5 my-1">
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
            {Array.isArray(ratingList) &&
              ratingList.map((rating) => (
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
                      <button
                        className="btn btn-outline-danger btn-sm"
                        type="button"
                        onClick={() => handleDeleteRating(rating.id)}
                      >
                        Delete
                      </button>
                    </Stack>
                  </td>
                </tr>
              ))}
          </tbody>
        </Table>
      </div>
      <ReactPaginate
        forcePage={pageNo}
        previousLabel={'Previous'}
        nextLabel={'Next'}
        pageCount={totalPageProduct}
        onPageChange={handlePageChange}
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
