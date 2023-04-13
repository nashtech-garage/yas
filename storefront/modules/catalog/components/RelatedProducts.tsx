import { useEffect, useState } from 'react';

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
  return <div></div>;
};

export default RelatedProduct;
