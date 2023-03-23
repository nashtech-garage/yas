import Link from 'next/link';
import Card from 'react-bootstrap/Card';
import Col from 'react-bootstrap/Col';

import { ProductThumbnail } from '../../modules/catalog/models/ProductThumbnail';
import { formatPrice } from '../../utils/formatPrice';
import ImageWithFallBack from '../components/ImageWithFallback';

import styles from '../../styles/productList.module.css';

type Props = {
  products: ProductThumbnail[];
};

const ProductItems = ({ products }: Props) => {
  return (
    <>
      {products &&
        products.map((product) => (
          <Col key={product.id}>
            <Card
              className={styles['item-product']}
              style={{ padding: '0', borderRadius: 0, margin: 0 }}
            >
              <Link href={`/products/${product.slug}`}>
                <ImageWithFallBack
                  src={product.thumbnailUrl}
                  alt={product.name}
                  style={{ width: '100%', height: '14rem', cursor: 'pointer' }}
                />
              </Link>
              <Card.Body>
                <Link href={`/products/${product.slug}`}>
                  <div style={{ height: '45px', cursor: 'pointer' }}>
                    <Link href="" style={{ fontWeight: 'bolder' }}>
                      {product.name}
                    </Link>
                  </div>
                </Link>
                <div className={` ${styles['btn-card-container']}`} style={{ zIndex: 2 }}>
                  <Link href={`/products/${product.slug}`} className={` ${styles['btn-in-card']}`}>
                    Quick View
                  </Link>
                  <Link href={`/products/${product.slug}`} className={` ${styles['btn-in-card']}`}>
                    <i className="yith-wcwl-icon fa fa-heart-o"></i> <span>Save</span>
                  </Link>
                </div>

                <div className={styles['price-card-container']} style={{ zIndex: 1 }}>
                  <span style={{ color: 'red', fontWeight: 'bolder' }}>
                    {product?.price && formatPrice(product?.price)}
                  </span>
                  <br />
                  <span className="fa fa-star checked"></span>
                  <span className="fa fa-star checked"></span>
                  <span className="fa fa-star checked"></span>
                  <span className="fa fa-star checked"></span>
                  <span className="fa fa-star checked"></span>
                </div>
              </Card.Body>
            </Card>
          </Col>
        ))}
    </>
  );
};

export default ProductItems;
