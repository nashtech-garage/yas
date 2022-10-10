import type { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { ProductOption } from '../../../modules/catalog/models/ProductOption';
import {
  createProductOption,
  getProductOptions,
  updateProductOption,
} from '../../../modules/catalog/services/ProductOptionService';
const ProductOptionList: NextPage = () => {
  const [productOptions, setProductOptions] = useState<ProductOption[]>();
  const [isLoading, setLoading] = useState(false);
  const [showModalCreate, setShowModalCreate] = useState(false);
  const [showModalEdit, setShowModalEdit] = useState(false);
  const [productOptionId, setProductOptionId] = useState(0);
  const [productOptionName, setProductOptionName] = useState('');
  const getListProductOption = () => {
    getProductOptions().then((data) => {
      setProductOptions(data);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    getListProductOption();
  }, []);
  const handleCreate = async (event: any) => {
    event.preventDefault();
    let productOption: ProductOption = {
      id: 0,
      name: event.target.productOptionName.value,
    };
    productOption = await createProductOption(productOption);
    setShowModalCreate(false);
    getListProductOption();
  };
  const handleEdit = async (event: any) => {
    event.preventDefault();
    let productOption: ProductOption = {
      id: productOptionId,
      name: event.target.productOptionNameEdit.value,
    };
    updateProductOption(productOptionId, productOption).then((response) => {
      if (response.status === 204) {
        toast.success('Update successfully');
      } else if (response.title === 'Not found') {
        toast.error(response.detail);
      } else if (response.title === 'Bad request') {
        toast.error(response.detail);
      } else {
        toast.error('Update failed');
      }
      setShowModalEdit(false);
      getListProductOption();
    });
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
          <Button onClick={() => setShowModalCreate(true)}>Create Product Option</Button>
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
          {productOptions.map((obj) => (
            <tr key={obj.id}>
              <td>{obj.id}</td>
              <td>{obj.name}</td>
              <td>
                <button
                  className="btn btn-outline-primary btn-sm"
                  type="button"
                  onClick={() => {
                    setProductOptionId(obj.id);
                    setProductOptionName(obj.name);
                    setShowModalEdit(true);
                  }}
                >
                  Edit
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <Modal show={showModalCreate} onHide={() => setShowModalCreate(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Create Product Option</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleCreate}>
            <Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
              <Form.Label>Name</Form.Label>
              <Form.Control name="productOptionName" autoFocus required />
            </Form.Group>
            <Button variant="primary" type="submit">
              Save
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
      <Modal show={showModalEdit} onHide={() => setShowModalEdit(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Edit Product Option</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleEdit}>
            <Form.Group className="mb-3" controlId="exampleForm.ControlInput2">
              <Form.Label>Name</Form.Label>
              <Form.Control
                name="productOptionNameEdit"
                defaultValue={productOptionName}
                autoFocus
                required
              />
            </Form.Group>
            <Button variant="primary" type="submit">
              Save
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </>
  );
};
export default ProductOptionList;
