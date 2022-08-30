import type { NextPage } from 'next'
import Link from 'next/link'
import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import type { Product } from '../../../modules/catalog/models/Product'
import { getProducts } from '../../../modules/catalog/services/ProductService'

const ProductList: NextPage = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setLoading] = useState(false);
  useEffect(() => {
    setLoading(true);
    getProducts()
      .then((data) => {
        setProducts(data);
        setLoading(false);
      });
  }, []);

  if (isLoading) return <p>Loading...</p>;
  if (!products) return <p>No product</p>;
    return (
      <>
      <div className='row mt-5'>
        <div className='col-md-8'>
          <h2>Products</h2>
        </div> 
        <div className='col-md-4 text-right'>
          <Link href="/catalog/products/create">
            <Button>Create Product</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
        {products.map((product) => (
          <tr key={product.id}>
            <td>{product.id}</td>
            <td>{product.name}</td>
            <td><Link href={`/catalog/products/${product.id}/edit`}><a>Edit</a></Link></td>
          </tr>
        ))}
        </tbody>
      </Table>
      </>
    )
  }
  
  export default ProductList