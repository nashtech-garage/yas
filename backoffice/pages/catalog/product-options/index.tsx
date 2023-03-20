import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';
import { ProductOption } from '../../../modules/catalog/models/ProductOption';
import {
  deleteProductOption,
  getPageableProductOptions,
} from '../../../modules/catalog/services/ProductOptionService';
import { handleDeletingResponse } from '../../../modules/catalog/services/ResponseStatusHandlingService';
import ReactPaginate from 'react-paginate';
import { DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER } from '../../../constants/Common';

const ProductOptionList: NextPage = () => {
  const [productOptions, setProductOptions] = useState<ProductOption[]>();
  const [isLoading, setLoading] = useState(false);
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [productOptionNameWantToDelete, setProductOptionNameWantToDelete] = useState<string>('');
  const [productOptionIdWantToDelete, setProductOptionIdWantToDelete] = useState<number>(-1);
  const [pageNo, setPageNo] = useState<number>(DEFAULT_PAGE_NUMBER);
  const [totalPage, setTotalPage] = useState<number>(1);

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (productOptionIdWantToDelete == -1) {
      return;
    }
    deleteProductOption(productOptionIdWantToDelete)
      .then((response) => {
        setShowModalDelete(false);
        handleDeletingResponse(response, productOptionNameWantToDelete);
        setPageNo(DEFAULT_PAGE_NUMBER);
        getListProductOption();
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const getListProductOption = () => {
    getPageableProductOptions(pageNo, DEFAULT_PAGE_SIZE).then((data) => {
      setTotalPage(data.totalPages);
      setProductOptions(data.productOptionContent);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    getListProductOption();
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

  if (isLoading) return <p>Loading...</p>;
  if (!productOptions) return <p>No Product Options</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Product Options </h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href={'/catalog/product-options/create'}>
            <Button>Create Product Option</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {productOptions.map((productOpt) => (
            <tr key={productOpt.id}>
              <td>{productOpt.id}</td>
              <td>{productOpt.name}</td>
              <td>
                <Link href={`/catalog/product-options/${productOpt.id}/edit`}>
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
                    setProductOptionIdWantToDelete(productOpt.id);
                    setProductOptionNameWantToDelete(productOpt.name);
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
        showModalDelete={showModalDelete}
        handleClose={handleClose}
        nameWantToDelete={productOptionNameWantToDelete}
        handleDelete={handleDelete}
        action="delete"
      />
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
export default ProductOptionList;
