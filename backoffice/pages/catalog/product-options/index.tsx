import type { NextPage } from 'next';
import Link from 'next/link';
import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { ProductOption } from '../../../modules/catalog/models/ProductOption';
import { getProductOptions } from '../../../modules/catalog/services/ProductOptionService';
const ProductOptionList: NextPage = () => {
  const [productOptions, setProductOptions] = useState<ProductOption[]>();
  const [isLoading, setLoading] = useState(false);
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
          {productOptions.map((obj) => (
            <tr key={obj.id}>
              <td>{obj.id}</td>
              <td>{obj.name}</td>
              <td>
                <Link href={`/catalog/product-options/${obj.id}/edit`}>
                  <button className="btn btn-outline-primary btn-sm" type="button">
                    Edit
                  </button>
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </>
  );
};
export default ProductOptionList;
