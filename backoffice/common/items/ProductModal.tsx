import React, { useEffect, useState } from 'react';
import { Button, Modal, Table } from 'react-bootstrap';
import { useRouter } from 'next/router';

import { Product } from '@catalogModels/Product';
import { getProducts } from '@catalogServices/ProductService';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PRODUCT_PAGE_SIZE } from 'constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonServices/PaginationService';

type Props = {
  show: boolean;
  onHide: () => void;
  label: string;
  onSelected: (product: Product) => void;
  selectedProduct: Product[];
};

const ShowProductModel = (props: Props) => {
  const router = useRouter();
  const { id } = router.query;

  const [selectedProduct, setSelectedProduct] = useState<Product[]>([]);
  const [products, setProducts] = useState<Product[]>([]);

  const { pageNo, totalPage, setTotalPage, itemsPerPage, changePage } = usePagination({
    initialPageNo: DEFAULT_PAGE_NUMBER,
    initialItemsPerPage: DEFAULT_PRODUCT_PAGE_SIZE,
  });

  useEffect(() => {
    getProducts(pageNo, itemsPerPage, '', '').then((data) => {
      if (id) {
        let filterProduct = data.productContent.filter((product) => product.id !== +id);
        setProducts(filterProduct);
      } else {
        setProducts(data.productContent);
      }
      setTotalPage(data.totalPages);
    });
  }, [pageNo, id]);

  useEffect(() => {
    setSelectedProduct(props.selectedProduct);
  }, [props.selectedProduct]);

  return (
    <Modal show={props.show} size="lg" aria-labelledby="contained-modal-title-vcenter" centered>
      <Modal.Header>
        <Modal.Title id="contained-modal-title-vcenter">{props.label}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Table striped bordered hover>
          <thead>
            <tr>
              <th style={{ width: '100px' }}>Select</th>
              <th>Product Name</th>
            </tr>
          </thead>
          <tbody>
            {(products || []).map((product) => (
              <tr key={product.id}>
                <td>
                  <input
                    type="checkbox"
                    id={product.slug}
                    className="form-check-input"
                    style={{ cursor: 'pointer' }}
                    onClick={() => props.onSelected(product)}
                    checked={selectedProduct.some((_product) => _product.id === product.id)}
                  />
                </td>
                <td>
                  <label
                    className="form-check-label"
                    htmlFor={product.slug}
                    style={{ cursor: 'pointer' }}
                  >
                    {product.name}
                  </label>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
        {totalPage > 1 && (
          <Pagination
            pageNo={pageNo}
            totalPage={totalPage}
            itemsPerPage={itemsPerPage}
            onPageChange={changePage}
            showHelpers={false}
          />
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button onClick={props.onHide}>Close</Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ShowProductModel;
