import { GetServerSideProps } from 'next';
import Card from 'react-bootstrap/Card';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import { getFeaturedProducts, formatPrice } from '../modules/catalog/services/ProductService';
import type { ProductThumbnail } from '../modules/catalog/models/ProductThumbnail';
import styles from '../styles/Home.module.css';
import { Button } from 'react-bootstrap';
import Link from 'next/link';

type Props = {
  products: ProductThumbnail[];
};

const Home = ({ products }: Props) => {
  return (
    <>
      <h2>Featured products</h2>
      <Row xs={2} md={5} className="g-4">
        {products.map((product) => (
          <Col key={product.id}>
            <Card className="item-product" style={{ padding: '0', borderRadius: 0, margin: 0 }}>
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

                <span style={{ color: 'red', fontWeight: 'bolder' }}>
                  {formatPrice(product.price)}
                </span>
                <br />
                <span className="fa fa-star checked"></span>
                <span className="fa fa-star checked"></span>
                <span className="fa fa-star checked"></span>
                <span className="fa fa-star checked"></span>
                <span className="fa fa-star checked"></span>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
    </>
  );
};

export const getServerSideProps: GetServerSideProps = async () => {
  let products = await getFeaturedProducts();

  return { props: { products } };
};

export default Home;
