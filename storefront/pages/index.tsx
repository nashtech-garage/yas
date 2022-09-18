import { GetServerSideProps } from "next";
import Card from "react-bootstrap/Card";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import { getFeaturedProducts } from "../modules/catalog/services/ProductService";
import type { ProductThumbnail } from "../modules/catalog/models/ProductThumbnail";
import styles from "../styles/Home.module.css";
import { Button } from "react-bootstrap";
import Link from "next/link";

type Props = {
  products: ProductThumbnail[];
};

const Home = ({ products }: Props) => {
  return (
    <>
      <h2>Featured products</h2>
      <Row xs={2} md={4} className="g-4">
        {products.map((product) => (
          <Col key={product.id}>
            <Card>
              <Card.Img
                variant="top"
                src={product.thumbnailUrl}
                style={{ width: "100%", height: "18rem" }}
              />
              <Card.Body>
                <Card.Title>{product.name}</Card.Title>
                <Card.Text>Price: $0.0</Card.Text>
                <Button variant="primary" className="">
                  <Link href={`/catalog/products/${product.id}/details`}>
                    More detail
                  </Link>
                </Button>
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
