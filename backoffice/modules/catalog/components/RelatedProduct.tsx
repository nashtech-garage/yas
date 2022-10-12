import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import React, { useEffect, useState } from 'react';
import { getProducts } from '../services/ProductService';
import { Product } from '../models/Product';
import { Table } from 'react-bootstrap';
import { UseFormSetValue, UseFormGetValues } from 'react-hook-form';
import { ProductPost } from '../models/ProductPost';
import ReactPaginate from 'react-paginate';

const ShowRelatedProductModel = (props: any) => {
  const [products, setProducts] = useState<Product[]>([]);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(0);
  useEffect(() => {
    getProducts(pageNo, '', '').then((data) => {
      setProducts(data.productContent);
      setTotalPage(data.totalPages);
    });
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };
  return (
    <Modal {...props} size="lg" aria-labelledby="contained-modal-title-vcenter" centered>
      <Modal.Header closeButton>
        <Modal.Title id="contained-modal-title-vcenter">{props.label}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Table>
          <thead>
            <th>Selected</th>
            <th>Product Name</th>
          </thead>
          <tbody>
            {(products || []).map((product) => (
              <tr className="mb-3" key={product.id}>
                <th>
                  <input
                    type="checkbox"
                    id={product.slug}
                    className={`form-check-input`}
                    onClick={(event) => props.onSelected(event, product)}
                  />
                </th>
                <th>
                  <label className="form-check-label ps-3" htmlFor={product.slug}>
                    {product.name}
                  </label>
                </th>
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
      </Modal.Body>
      <Modal.Footer>
        <Button onClick={props.onHide}>Close</Button>
      </Modal.Footer>
    </Modal>
  );
};

type Props = {
  setValue: UseFormSetValue<ProductPost>;
  getValue: UseFormGetValues<ProductPost>;
};

export const RelatedProduct = ({ setValue, getValue }: Props) => {
  const [modalShow, setModalShow] = useState<boolean>(false);
  const [selectedProduct, setSelectedProduct] = useState<Product[]>([]);

  const onProductSelected = (event: React.MouseEvent<HTMLElement>, product: Product) => {
    let temp = getValue('relateProduct') || [];
    let index = temp.indexOf(product.id);
    if (index === -1) {
      temp.push(product.id);
      setSelectedProduct([...selectedProduct, product]);
    } else {
      temp = temp.filter((item) => item !== product.id);
      let filterProduct = selectedProduct.filter((_product) => _product.id !== product.id);
      setSelectedProduct([...filterProduct]);
    }
    setValue('relateProduct', temp);
  };
  return (
    <>
      <Button variant="primary" onClick={() => setModalShow(true)}>
        Manage Related Product
      </Button>

      <ShowRelatedProductModel
        show={modalShow}
        onHide={() => setModalShow(false)}
        label="Add Related Product"
        onSelected={onProductSelected}
      />
      {selectedProduct.length > 0 && (
        <Table>
          <thead>
            <th>Selected</th>
            <th>Product Name</th>
          </thead>
          <tbody>
            {(selectedProduct || []).map((product) => (
              <tr className="mb-3" key={product.id}>
                <th>{product.id}</th>
                <th>{product.name}</th>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </>
  );
};

export const CrossSellProduct = ({ setValue, getValue }: Props) => {
  const [modalShow, setModalShow] = useState<boolean>(false);
  const [selectedProduct, setSelectedProduct] = useState<Product[]>([]);

  const onProductSelected = (event: React.MouseEvent<HTMLElement>, product: Product) => {
    let temp = getValue('crossSell') || [];
    let index = temp.indexOf(product.id);
    if (index === -1) {
      temp.push(product.id);
      setSelectedProduct([...selectedProduct, product]);
    } else {
      temp = temp.filter((item) => item !== product.id);
      let filterProduct = selectedProduct.filter((_product) => _product.id !== product.id);
      setSelectedProduct([...filterProduct]);
    }
    setValue('crossSell', temp);
  };
  return (
    <>
      <Button variant="primary" onClick={() => setModalShow(true)}>
        Manage Cross-Sell Product
      </Button>

      <ShowRelatedProductModel
        show={modalShow}
        onHide={() => setModalShow(false)}
        onSelected={onProductSelected}
        label="Add Cross - Sell Product"
      />
      {selectedProduct.length > 0 && (
        <Table>
          <thead>
            <th>Selected</th>
            <th>Product Name</th>
          </thead>
          <tbody>
            {(selectedProduct || []).map((product) => (
              <tr className="mb-3" key={product.id}>
                <th>{product.id}</th>
                <th>{product.name}</th>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </>
  );
};
