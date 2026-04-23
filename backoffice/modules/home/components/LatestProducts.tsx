import { Product } from '@catalogModels/Product';
import { getLatestProducts } from '@catalogServices/ProductService';
import moment from 'moment';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';

const LatestProducts = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const res = await getLatestProducts(5);
        setProducts(res);
      } catch (error) {
        console.log(error);
        setProducts([]);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  let tableContent;

  if (loading) {
    tableContent = (
      <tr>
        <td colSpan={5}>Loading...</td>
      </tr>
    );
  } else if (!products || products.length === 0) {
    tableContent = (
      <tr>
        <td colSpan={5}>No Products available</td>
      </tr>
    );
  } else {
    tableContent = products.map((product) => (
      <tr key={product.id}>
        <td className="id-column">{product.id}</td>
        <td className="identify-column">{product.name}</td>
        <td>{product.slug}</td>
        <td className="created-on-column">
          {product.createdOn && moment(product.createdOn).format('MMMM Do YYYY, h:mm:ss a')}
        </td>
        <td className="details-column">
          <Link href={`/catalog/products/${product.parentId ?? product.id}/edit`}>
            <button className="btn btn-outline-primary btn-sm" type="button">
              Details
            </button>
          </Link>
        </td>
      </tr>
    ));
  }

  return (
    <>
      <h2 className="text-danger font-weight-bold mb-3">List of the 5 latest Products</h2>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Slug</th>
            <th>Created On</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>{tableContent}</tbody>
      </Table>
    </>
  );
};

export default LatestProducts;
