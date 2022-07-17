import { GetStaticProps } from 'next'
import Card from 'react-bootstrap/Card';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import { getFeaturedProducts } from '../modules/catalog/services/ProductService'
import type { ProductThumbnail } from '../modules/catalog/models/ProductThumbnail'
import styles from '../styles/Home.module.css'

type Props = {
  products: ProductThumbnail[]
}

const Home = ({products}: Props) => {
  return (
    <>
    <h2>Featured products</h2>
    <Row xs={2} md={4} className="g-4">
      {products.map((product) => (
        <Col>
          <Card>
            <Card.Img variant="top" src={product.thumbnailUrl} />
            <Card.Body>
              <Card.Title>{product.name}</Card.Title>
              <Card.Text>
                Price: $0.0
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
      ))}
    </Row>
    </>
  )
}

export const getStaticProps: GetStaticProps = async () => {
  let products = await getFeaturedProducts()

  return { props: { products } }
}

export default Home
