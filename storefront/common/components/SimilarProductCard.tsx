import { SimilarProduct } from 'modules/catalog/models/SimilarProduct';
import ProductCardBase from './ProductCardBase';

export interface SimilarProductCardProps {
  product: SimilarProduct;
  thumbnailUrl?: string;
  className?: string[];
}

export default function SimilarProductCard({
  product,
  className,
  thumbnailUrl,
}: Readonly<SimilarProductCardProps>) {
  return (
    <ProductCardBase product={product} thumbnailUrl={thumbnailUrl ?? ''} className={className} />
  );
}
