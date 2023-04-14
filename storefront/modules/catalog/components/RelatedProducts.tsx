import { useEffect, useState } from 'react';
import { Col, Row } from 'react-bootstrap';

import ProductCard from '@/common/components/ProductCard';
import { ProductThumbnail } from '../models/ProductThumbnail';
import { getRelatedProductsByProductId } from '../services/ProductService';

type RelatedProductProps = {
  productId: number;
};

const RelatedProduct = ({ productId }: RelatedProductProps) => {
  const [products, setProducts] = useState<ProductThumbnail[]>([]);

  useEffect(() => {
    fetchRelatedProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchRelatedProducts = () => {
    getRelatedProductsByProductId(productId)
      .then((response) => setProducts(response))
      .catch((error) => console.log(error));
  };

  console.log('>>> Related products:', products);
  return (
    <div className="my-5">
      <h4 className="mb-2 text-black">Related products</h4>
      {products.length > 0 && (
        <Row md={5}>
          {products.map((product) => (
            <Col key={product.id}>
              <ProductCard product={product} />
            </Col>
          ))}
        </Row>
      )}

      {products.length === 0 && <p className="mt-4">No product</p>}
    </div>
  );
};

export default RelatedProduct;
