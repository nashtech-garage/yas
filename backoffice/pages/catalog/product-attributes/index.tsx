import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';

import { ProductAttribute } from '@catalogModels/ProductAttribute';
import {
  deleteProductAttribute,
  getPageableProductAttributes,
} from '@catalogServices/ProductAttributeService';
import ModalDeleteCustom from '@commonItems/ModalDeleteCustom';
import { handleDeletingResponse } from '@commonServices/ResponseStatusHandlingService';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from '@constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonHooks/usePagination';

const ProductAttributeList: NextPage = () => {
  const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>();
  const [isLoading, setLoading] = useState(false);
  const [isShowModalDelete, setIsShowModalDelete] = useState<boolean>(false);
  const [productAttributeNameWantToDelete, setProductAttributeNameWantToDelete] =
    useState<string>('');
  const [productAttributeIdWantToDelete, setProductAttributeIdWantToDelete] = useState<number>(-1);

  const { pageNo, totalPage, setTotalPage, paginationControls, changePage } = usePagination();

  const itemsPerPage = paginationControls?.itemsPerPage?.value ?? DEFAULT_PAGE_SIZE;

  useEffect(() => {
    setLoading(true);
    getListProductAttributes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageNo, itemsPerPage]);

  const handleClose: any = () => setIsShowModalDelete(false);
  const handleDelete: any = () => {
    if (productAttributeIdWantToDelete == -1) return;

    deleteProductAttribute(productAttributeIdWantToDelete)
      .then((response) => {
        setIsShowModalDelete(false);
        handleDeletingResponse(response, productAttributeIdWantToDelete);
        changePage({ selected: DEFAULT_PAGE_NUMBER });
        getListProductAttributes();
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const getListProductAttributes = () => {
    getPageableProductAttributes(pageNo, itemsPerPage)
      .then((data) => {
        setTotalPage(data.totalPages);
        setProductAttributes(data.productAttributeContent);
        setLoading(false);
      })
      .catch((err) => console.log(err));
  };

  if (isLoading) return <p>Loading...</p>;
  if (!productAttributes) return <p>No Product Attributes</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Product Attributes </h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href="/catalog/product-attributes/create">
            <Button>Create Product Attribute</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>Group</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {productAttributes.map((obj) => (
            <tr key={obj.id}>
              <td>{obj.id}</td>
              <td>{obj.name}</td>
              <td>{obj.productAttributeGroup}</td>
              <td>
                <Link href={`/catalog/product-attributes/${obj.id}/edit`}>
                  <button className="btn btn-outline-primary btn-sm" type="button">
                    Edit
                  </button>
                </Link>
                &nbsp;
                <button
                  className="btn btn-outline-danger btn-sm"
                  type="button"
                  onClick={() => {
                    setIsShowModalDelete(true);
                    setProductAttributeIdWantToDelete(obj.id);
                    setProductAttributeNameWantToDelete(obj.name);
                  }}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <ModalDeleteCustom
        showModalDelete={isShowModalDelete}
        handleClose={handleClose}
        nameWantToDelete={productAttributeNameWantToDelete}
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

export default ProductAttributeList;
