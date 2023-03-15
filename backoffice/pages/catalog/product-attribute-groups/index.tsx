import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';
import { ProductAttributeGroup } from '../../../modules/catalog/models/ProductAttributeGroup';
import {
  deleteProductAttributeGroup,
  getProductAttributeGroups,
} from '../../../modules/catalog/services/ProductAttributeGroupService';
import CustomToast from '../../../common/items/CustomToast';
import { useDeletingContext } from '../../../common/hooks/UseToastContext';

const ProductAttrbuteGroupList: NextPage = () => {
  const { toastVariant, toastHeader, showToast, setShowToast, handleDeletingResponse } =
    useDeletingContext();
  const [productAttributeGroups, setProductAttributeGroups] = useState<ProductAttributeGroup[]>();
  const [isLoading, setLoading] = useState(false);
  const [isShowModalDelete, setIsShowModalDelete] = useState<boolean>(false);
  const [productAttributeGroupNameWantToDelete, setProductAttributeGroupNameWantToDelete] =
    useState<string>('');
  const [productAttributeGroupIdWantToDelete, setProductAttributeGroupIdWantToDelete] =
    useState<number>(-1);

  const handleClose: any = () => setIsShowModalDelete(false);
  const handleDelete: any = () => {
    if (productAttributeGroupIdWantToDelete == -1) return;
    deleteProductAttributeGroup(productAttributeGroupIdWantToDelete)
      .then((response) => {
        setIsShowModalDelete(false);
        handleDeletingResponse(response, productAttributeGroupIdWantToDelete);
        getListProductAttributeGroup();
      })
  };

  const getListProductAttributeGroup = () => {
    getProductAttributeGroups().then((data) => {
      setProductAttributeGroups(data);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    getListProductAttributeGroup();
  }, []);
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
      {showToast && (
        <CustomToast
          variant={toastVariant}
          header={toastHeader}
          show={showToast}
          setShow={setShowToast}
        ></CustomToast>
      )}
    </>
  );
};
export default ProductAttrbuteGroupList;
