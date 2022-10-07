import type {NextPage} from 'next'
import Link from'next/link'
import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { getProductAttributes } from '../../../modules/catalog/services/ProductAttributeService'
import {ProductAttribute} from "../../../modules/catalog/models/ProductAttribute";


const ProductAttributeList: NextPage = () =>{
    const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>();

    const [isLoading, setLoading] = useState(false);

    useEffect(() =>{
        setLoading(true)
        getProductAttributes()
            .then((data) => {
                console.log(data)
                setProductAttributes(data);
                setLoading(false);
            });
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
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </>
    );
}
export  default  ProductAttributeList
