import { useEffect, useState } from 'react';
import { Col, Row } from 'react-bootstrap';

import SimilarProductCard from '@/common/components/SimilarProductCard';
import { getSimilarProductsByProductId } from '../services/ProductService';
import { SimilarProduct } from '../models/SimilarProduct';

type SimilarProductProps = {
  productId: number;
};

const SimilarProducts = ({ productId }: SimilarProductProps) => {
  const [products, setProducts] = useState<SimilarProduct[]>([]);

  useEffect(() => {
    fetchSimilarProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  const fetchSimilarProducts = () => {
    getSimilarProductsByProductId(productId)
      .then((response) => setProducts(response))
      .catch((error) => console.log(error));
  };

  return (
    <div className="my-5">
      {products.length > 0 && (
        <>
          <h4 className="mb-2 text-black">Similar Products</h4>
          <Row md={5}>
            {products.map((product) => (
              <Col key={product.id}>
                <SimilarProductCard product={product} thumbnailUrl={product.thumbnail.url} />
              </Col>
            ))}
          </Row>
        </>
      )}
    </div>
  );
};

export default SimilarProducts;
