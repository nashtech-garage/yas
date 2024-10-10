import { useEffect, useState } from 'react';
import { Col, Row } from 'react-bootstrap';

import SimilarProductCard from '@/common/components/SimilarProductCard';
import { ProductThumbnail } from '../models/ProductThumbnail';
import { getSimilarProductsByProductId } from '../services/ProductService';
import { SimilarProduct } from '../models/SimilarProduct';

type SimilarProductProps = {
  productId: number;
};

const SimilarProduct = ({ productId }: SimilarProductProps) => {
  const [products, setProducts] = useState<ProductThumbnail[]>([]);

  useEffect(() => {
    fetchSimilarProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
    // Convert API response to ProductThumbnail[
  const convertToProductThumbnail = (response: SimilarProduct[]): ProductThumbnail[] => {
    return response.map((product) => ({
    id: product.id,
    name: product.name,
    slug: product.slug,
    thumbnailUrl: product.thumbnail?.url || '',
    price: product.price
  }));
};
const fetchSimilarProducts = () => {
    getSimilarProductsByProductId(productId)
       .then((response) => {
         // Transform response to match ProductThumbnail[]
         const products: ProductThumbnail[] = convertToProductThumbnail(response);
         setProducts(products);
       })
       .catch((error) => console.log(error));
};

return (
    <div className="my-5">
      <h4 className="mb-2 text-black">Similar Products</h4>
           {products.length > 0 && (
            <Row md={5}>
              {products.map((product) => (
                <Col key={product.id}>
                  <SimilarProductCard
                    product={product}
                    thumbnailUrl={product.thumbnailUrl}
                  />
                </Col>
              ))}
            </Row>
          )}
      {products.length === 0 && <p className="mt-4">No product</p>}
    </div>
  );
};

export default SimilarProduct;
