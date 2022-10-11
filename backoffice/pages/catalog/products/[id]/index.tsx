import type { NextPage } from 'next';
import React, { useEffect, useState } from 'react';
import { Tab, Tabs } from 'react-bootstrap';
import EditProduct from '../[id]/edit';
import ProductAttributes from '../[id]/productAttributes';
import { useRouter } from 'next/router';
import { Product } from '../../../../modules/catalog/models/Product';
import { getProduct } from '../../../../modules/catalog/services/ProductService';

const CategoryGetById: NextPage = () => {
  const [tabKey, setTabKey] = useState('one');
  const [product, setProduct] = useState<Product>();
  const [isLoading, setLoading] = useState(false);

  const router = useRouter();
  const { id } = router.query;

  useEffect(() => {
    setLoading(true);
    if (id) {
      getProduct(+id).then((data) => {
        setProduct(data);
        setLoading(false);
      });
    }
  }, [id]);
  if (isLoading) return <p>Loading...</p>;
  if (!product) return <p>No product</p>;

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Update product: {product.name}</h2>
          <div className="isolate-scope" style={{ marginTop: '50px' }}>
            <Tabs activeKey={tabKey} onSelect={(e: any) => setTabKey(e)}>
              <Tab eventKey="one" title="General Information">
                <EditProduct />
              </Tab>
              <Tab eventKey="two" title="Product Attributes">
                <ProductAttributes />
              </Tab>
            </Tabs>
          </div>
        </div>
      </div>
    </>
  );
};

export default CategoryGetById;
