import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';
import { ProductAttributeGroup } from '../../../modules/catalog/models/ProductAttributeGroup';
import {
  deleteProductAttributeGroup,
  getPageableProductAttributeGroups,
} from '../../../modules/catalog/services/ProductAttributeGroupService';
import { handleDeletingResponse } from '../../../common/services/ResponseStatusHandlingService';
import { DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER } from '../../../constants/Common';
import ReactPaginate from 'react-paginate';

const ProductAttrbuteGroupList: NextPage = () => {
  const [productAttributeGroups, setProductAttributeGroups] = useState<ProductAttributeGroup[]>();
  const [isLoading, setLoading] = useState(false);
  const [isShowModalDelete, setIsShowModalDelete] = useState<boolean>(false);
  const [productAttributeGroupNameWantToDelete, setProductAttributeGroupNameWantToDelete] =
    useState<string>('');
  const [productAttributeGroupIdWantToDelete, setProductAttributeGroupIdWantToDelete] =
    useState<number>(-1);
  const [pageNo, setPageNo] = useState<number>(DEFAULT_PAGE_NUMBER);
  const [totalPage, setTotalPage] = useState<number>(1);

  const handleClose: any = () => setIsShowModalDelete(false);
  const handleDelete: any = () => {
    if (productAttributeGroupIdWantToDelete == -1) return;
    deleteProductAttributeGroup(productAttributeGroupIdWantToDelete).then((response) => {
      setIsShowModalDelete(false);
      handleDeletingResponse(response, productAttributeGroupIdWantToDelete);
      setPageNo(DEFAULT_PAGE_NUMBER);
      getListProductAttributeGroup();
    });
  };

  const getListProductAttributeGroup = () => {
    getPageableProductAttributeGroups(pageNo, DEFAULT_PAGE_SIZE).then((data) => {
      setTotalPage(data.totalPages);
      setProductAttributeGroups(data.productAttributeGroupContent);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    getListProductAttributeGroup();
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

  if (isLoading) return <p>Loading...</p>;
  if (!productAttributeGroups) return <p>No Product Attribute Group</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Product Attribute Groups </h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href={'/catalog/product-attribute-groups/create'}>
            <Button>Create Product Attribute Group</Button>
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
          {productAttributeGroups.map((obj) => (
            <tr key={obj.id}>
              <td>{obj.id}</td>
              <td>{obj.name}</td>
              <td>
                <Link href={`/catalog/product-attribute-groups/${obj.id}/edit`}>
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
                    setProductAttributeGroupIdWantToDelete(obj.id);
                    setProductAttributeGroupNameWantToDelete(obj.name);
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
        nameWantToDelete={productAttributeGroupNameWantToDelete}
        handleDelete={handleDelete}
        action="delete"
      />
      <ReactPaginate
        forcePage={pageNo}
        previousLabel={'Previous'}
        nextLabel={'Next'}
        pageCount={totalPage}
        onPageChange={changePage}
        containerClassName={'pagination-container'}
        previousClassName={'previous-btn'}
        nextClassName={'next-btn'}
        disabledClassName={'pagination-disabled'}
        activeClassName={'pagination-active'}
      />
    </>
  );
};
export default ProductAttrbuteGroupList;
