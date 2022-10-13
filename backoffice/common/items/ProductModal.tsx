import React, { useEffect, useState } from 'react';
import ReactPaginate from 'react-paginate';
import { Product } from '../../modules/catalog/models/Product';
import { getProducts } from '../../modules/catalog/services/ProductService';
import { Table, Button, Modal } from 'react-bootstrap';

const ShowProductModel = (props: any) => {
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
            <tr>
              <td>Selected</td>
              <td>Product Name</td>
            </tr>
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

export default ShowProductModel;
