import type { NextPage } from 'next';
import Link from 'next/link';
import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { ProductAttributeGroup } from '../../../modules/catalog/models/ProductAttributeGroup';
import { getProductAttributeGroups } from '../../../modules/catalog/services/ProductAttributeGroupService';
const ProductAttrbuteGroupList: NextPage = () => {
  const [productAttributeGroups, setProductAttributeGroups] = useState<ProductAttributeGroup[]>();
  const [isLoading, setLoading] = useState(false);
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
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </>
  );
};
export default ProductAttrbuteGroupList;
