import clsx from 'clsx';
import Link from 'next/link';

import { ProductThumbnail } from 'modules/catalog/models/ProductThumbnail';
import { formatPrice } from 'utils/formatPrice';
import ImageWithFallBack from './ImageWithFallback';

import styles from 'styles/ProductCard.module.css';

export interface Props {
  product: ProductThumbnail;
  className?: string[];
}

export default function ProductCard({ product, className }: Props) {
  return (
    <Link
      className={clsx(
        styles['product'],
        className?.map((item) => styles[item])
      )}
      href={`/products/${product.slug}`}
    >
      <div className={styles['product-card']}>
        <div className={styles['image-wrapper']}>
          <ImageWithFallBack src={product.thumbnailUrl} alt={product.name} />
        </div>
        <div className={styles['info-wrapper']}>
          <h3 className={styles['prod-name']}>{product.name}</h3>
          <div className={styles['rating-sold']}>
            <div className={styles['star']}>
              0 <i className="bi bi-star-fill"></i>
            </div>{' '}
            |{' '}
            <div className={styles['sold']}>
              0 <span>sold</span>
            </div>
          </div>

          <div className={styles['price']}>{product?.price && formatPrice(product?.price)}</div>

          <hr />

          <div className={styles['delivery']}>Fast delivery 2 hours</div>
        </div>
      </div>
    </Link>
  );
}
