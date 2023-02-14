import Link from 'next/link';
import 'react-toastify/dist/ReactToastify.css';
import { ProductThumbnail } from '../../modules/catalog/models/ProductThumbnail';
import { formatPrice, getFeaturedProducts } from '../../modules/catalog/services/ProductService';
import styles from '../../styles/productList.module.css';
import Card from 'react-bootstrap/Card';
import Col from 'react-bootstrap/Col';

type Props = {
  products: ProductThumbnail[];
};

const ProductItems = ({ products }: Props) => {
  return (
    <>
      {products.map((product) => (
        <Col key={product.id}>
          <Card
            className={styles['item-product']}
            style={{ padding: '0', borderRadius: 0, margin: 0 }}
          >
            <Link href={`/products/${product.slug}`}>
              <Card.Img
                variant="top"
                src={product.thumbnailUrl}
                style={{ width: '100%', height: '14rem', cursor: 'pointer' }}
              />
            </Link>
            <Card.Body>
              <Link href={`/products/${product.slug}`}>
                <div style={{ height: '45px', cursor: 'pointer' }}>
                  <a style={{ fontWeight: 'bolder' }}>{product.name}</a>
                </div>
              </Link>
              <div
                className={`container ${styles['btn-card-container']}`}
                style={{ zIndex: 2, alignSelf: 'center' }}
              >
                <div className="row">
                  <Link
                    href={`/products/${product.slug}`}
                    className={`col ${styles['btn-in-card']}`}
                  >
                    Quick View
                  </Link>
                  <Link
                    href={`/products/${product.slug}`}
                    className={`col ${styles['btn-in-card']}`}
                  >
                    <i className="yith-wcwl-icon fa fa-heart-o"></i> <span>Save</span>
                  </Link>
                </div>
              </div>

              <div className={styles['price-card-container']} style={{ zIndex: 1 }}>
                <span style={{ color: 'red', fontWeight: 'bolder' }}>
                  {formatPrice(product.price)}
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
