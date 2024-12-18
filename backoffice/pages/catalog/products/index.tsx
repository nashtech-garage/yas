import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, InputGroup, Stack, Table } from 'react-bootstrap';
import Form from 'react-bootstrap/Form';
import { FaSearch } from 'react-icons/fa';

import type { Brand } from '../../../modules/catalog/models/Brand';
import type { Product } from '../../../modules/catalog/models/Product';
import { getBrands } from '../../../modules/catalog/services/BrandService';
import { deleteProduct, getProducts } from '../../../modules/catalog/services/ProductService';
import styles from '../../../styles/Filter.module.css';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';
import { handleDeletingResponse } from '../../../common/services/ResponseStatusHandlingService';
import moment from 'moment';
import { ExportProduct } from '../../../modules/catalog/components';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PRODUCT_PAGE_SIZE } from 'constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonHooks/usePagination';

const ProductList: NextPage = () => {
  let typingTimeOutRef: null | ReturnType<typeof setTimeout> = null;
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [brands, setBrands] = useState<Brand[]>([]);
  const [brandName, setBrandName] = useState<string>('');
  const [productName, setProductName] = useState<string>('');

  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [productNameWantToDelete, setProductNameWantToDelete] = useState<string>('');
  const [productIdWantToDelete, setProductIdWantToDelete] = useState<number>(-1);

  const { pageNo, totalPage, setTotalPage, paginationControls, changePage } = usePagination({
    initialPageNo: DEFAULT_PAGE_NUMBER,
    initialItemsPerPage: DEFAULT_PRODUCT_PAGE_SIZE,
  });

  const itemsPerPage = paginationControls?.itemsPerPage?.value ?? DEFAULT_PRODUCT_PAGE_SIZE;
  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (productIdWantToDelete == -1) {
      return;
    }
    deleteProduct(productIdWantToDelete)
      .then((response) => {
        setShowModalDelete(false);
        handleDeletingResponse(response, productNameWantToDelete);
        getProducts(pageNo, itemsPerPage, productName, brandName).then((data) => {
          setTotalPage(data.totalPages);
          setProducts(data.productContent);
          setLoading(false);
        });
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    setLoading(true);

    getProducts(pageNo, itemsPerPage, productName, brandName).then((data) => {
      setTotalPage(data.totalPages);
      setProducts(data.productContent);
      setLoading(false);
    });
  }, [pageNo, itemsPerPage, brandName, productName]);

  useEffect(() => {
    setLoading(true);
    getBrands().then((data) => {
      setBrands(data);
      setLoading(false);
    });
  }, []);

  //searching handler
  const searchingHandler = () => {
    if (typingTimeOutRef) {
      clearTimeout(typingTimeOutRef);
    }
    typingTimeOutRef = setTimeout(() => {
      let inputValue = (document.getElementById('product-name') as HTMLInputElement).value;
      setProductName(inputValue);
      changePage({ selected: DEFAULT_PAGE_NUMBER });
    }, 500);
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
      </div>

      {/* Filter */}
      <div className="row mb-5">
        <div className="col-md-6">
          {/* <Form.Label htmlFor="brand-filter">Brand: </Form.Label> */}
          <Form.Select
            id="brand-filter"
            onChange={(e) => {
              changePage({ selected: DEFAULT_PAGE_NUMBER });
              setBrandName(e.target.value);
            }}
            className={styles.filterButton}
            defaultValue={brandName || ''}
          >
            <option value={''}>All</option>
            {brands.map((brand) => (
              <option key={brand.id} value={brand.name}>
                {brand.name}
              </option>
            ))}
          </Form.Select>
        </div>

        <div className="col-md-4">
          <Form>
            <InputGroup>
              <Form.Control
                id="product-name"
                placeholder="Search name ..."
                defaultValue={productName}
                onChange={searchingHandler}
              />
              <Button id="seach-category" variant="danger" onClick={searchingHandler}>
                <FaSearch />
              </Button>
            </InputGroup>
          </Form>
        </div>
        <div className="col-md-2 d-flex justify-content-end">
          <ExportProduct productName={productName} brandName={brandName} />
        </div>
      </div>

      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Is Featured</th>
            <th>Is Allow To Order</th>
            <th>Is Published</th>
            <th>Created Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              <td>{product.id}</td>
              <td>{product.name}</td>
              <td>{product.isFeatured ? 'Yes' : 'No'}</td>
              <td>{product.isAllowedToOrder ? 'Yes' : 'No'}</td>
              <td>{product.isPublished ? 'Yes' : 'No'}</td>
              <td>
                {product.createdOn != null ? moment(product.createdOn).format('DD/MM/YYYY') : ''}
              </td>
              <td>
                <Stack direction="horizontal" gap={3}>
                  <Link href={`/catalog/products/${product.id}/edit`}>
                    <button className="btn btn-outline-primary btn-sm" type="button">
                      Edit
                    </button>
                  </Link>
                  &nbsp;
                  <button
                    className="btn btn-outline-danger btn-sm"
                    type="button"
                    onClick={() => {
                      setShowModalDelete(true);
                      setProductIdWantToDelete(product.id);
                      setProductNameWantToDelete(product.name);
                    }}
                  >
                    Delete
                  </button>
                </Stack>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <ModalDeleteCustom
        showModalDelete={showModalDelete}
        handleClose={handleClose}
        nameWantToDelete={productNameWantToDelete}
        handleDelete={handleDelete}
        action="delete"
      />
      {totalPage > 0 && (
        <Pagination
          pageNo={pageNo}
          totalPage={totalPage}
          paginationControls={paginationControls}
          onPageChange={changePage}
        />
      )}
    </>
  );
};

export default ProductList;
