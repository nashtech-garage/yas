import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, InputGroup, Stack, Table } from 'react-bootstrap';
import Form from 'react-bootstrap/Form';
import { FaSearch } from 'react-icons/fa';
import ReactPaginate from 'react-paginate';
import type { Brand } from '../../../modules/catalog/models/Brand';
import type { Product } from '../../../modules/catalog/models/Product';
import { getBrands } from '../../../modules/catalog/services/BrandService';
import { getProducts } from '../../../modules/catalog/services/ProductService';
import styles from '../../../styles/Filter.module.css'

const ProductList: NextPage = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [brands, setBrands] = useState<Brand[]>([]);
  const [brandName, setBrandName] = useState<string>('');
  const [productName, setProductName] = useState<string>('');

  useEffect(() => {
    setLoading(true);

    getProducts(pageNo, productName, brandName).then((data) => {
      setTotalPage(data.totalPages);
      setProducts(data.productContent);
      setLoading(false);
    });
  }, [pageNo, brandName, productName]);

  useEffect(() => {
    setLoading(true);
    getBrands().then((data) => {
      setBrands(data);
      setLoading(false);
    });
  }, []);

  //searching handler
  const searchingHandler = () => {
    let inputValue = (document.getElementById('product-name') as HTMLInputElement).value;
    setProductName(inputValue);
    setPageNo(0);
  };

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

  if (isLoading) return <p>Loading...</p>;
  if (!products) return <p>No product</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Products</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href="/catalog/products/create">
            <Button>Create Product</Button>
          </Link>
        </div>
        <br />

        {/* Filter */}

        <div className="row mb-3">
          <div className="col ">
            {/* <Form.Label htmlFor="brand-filter">Brand: </Form.Label> */}
            <Form.Select
              id="brand-filter"
              onChange={(e) => {
                setPageNo(0);
                setBrandName(e.target.value);
              }}
              className={styles.filterButton}
            >
              <option value={''} selected={brandName == ''}>
                All
              </option>
              {brands.map((brand) => (
                <option key={brand.id} value={brand.name} selected={brandName === brand.name}>
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
                  onChange={(e) => {
                    if (e.target.value.replaceAll(' ', '') == '') setProductName('');
                  }}
                />
                <Button
                  id="seach-category"
                  variant="danger"
                  onClick={searchingHandler}
                >
                  <FaSearch />
                </Button>
              </InputGroup>
            </Form>

            {/* <Form.Label htmlFor="brand-filter">Search: </Form.Label>
            <div className="row height d-flex justify-content-center align-items-center">
              <div className="col" style={{ padding: '0' }}>
                <div className="search">
                  <i className="fa fa-search"></i>
                  <Form.Control
                    className="form-control"
                    id="product-name"
                    defaultValue={productName}
                    autoFocus
                    onChange={(e) => {
                      if (e.target.value.replaceAll(' ', '') == '') setProductName('');
                    }}
                  ></Form.Control>
                  <button className="btn btn-primary" onClick={searchingHandler}>
                    Search
                  </button>
                </div>
              </div>
            </div> */}
          </div>
        </div>
      </div>

      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              <td>{product.id}</td>
              <td>{product.name}</td>
              <td>
                <Stack direction="horizontal" gap={3}>
                  <Link href={`/catalog/products/${product.id}/edit`}>
                    <button className="btn btn-outline-primary btn-sm" type="button">
                      Edit
                    </button>
                  </Link>
                </Stack>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

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
    </>
  );
};

export default ProductList;
