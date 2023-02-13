import type { NextPage } from 'next';
import Link from 'next/link';
import React, { useEffect, useState } from 'react';
import { Button, Modal, Table } from 'react-bootstrap';
import {
  deleteProductAttribute,
  getProductAttributes,
} from '../../../modules/catalog/services/ProductAttributeService';
import { ProductAttribute } from '../../../modules/catalog/models/ProductAttribute';
import { toast } from 'react-toastify';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';

const ProductAttributeList: NextPage = () => {
  const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>();
  const [isLoading, setLoading] = useState(false);
  const [isShowModalDelete, setIsShowModalDelete] = useState<boolean>(false);
  const [productAttributeNameWantToDelete, setProductAttributeNameWantToDelete] =
    useState<string>('');
  const [productAttributeIdWantToDelete, setProductAttributeIdWantToDelete] = useState<number>(-1);

  const handleClose: any = () => setIsShowModalDelete(false);
  const handleDelete: any = () => {
    if (productAttributeIdWantToDelete == -1) return;
    deleteProductAttribute(productAttributeIdWantToDelete)
      .then((response) => {
        if (response.status === 204) {
          toast.success(productAttributeIdWantToDelete + ' have been deleted');
        } else if (response.title === 'Not found') {
          toast.error(response.detail);
        } else if (response.title === 'Bad request') {
          toast.error(response.detail);
        } else {
          toast.error('Delete failed');
        }
        getListProductAttributes();
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const getListProductAttributes = () => {
    getProductAttributes().then((data) => {
      setProductAttributes(data);
      setLoading(false);
    });
  };

  useEffect(() => {
    setLoading(true);
    getListProductAttributes();
  }, []);
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
    </>
  );
};
export default ProductAttributeList;
