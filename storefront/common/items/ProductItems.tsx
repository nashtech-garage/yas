import ProductCard from 'common/components/ProductCard';
import { ProductThumbnail } from 'modules/catalog/models/ProductThumbnail';

type Props = {
  products: ProductThumbnail[];
};

const ProductItems = ({ products }: Props) => {
  return (
    <>
      {products.length > 0 &&
        products.map((product) => <ProductCard product={product} key={product.id} />)}
    </>
  );
};

export default ProductItems;
