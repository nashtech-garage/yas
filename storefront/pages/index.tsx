import { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import Card from 'react-bootstrap/Card';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import ReactPaginate from 'react-paginate';
import type { ProductThumbnail } from '../modules/catalog/models/ProductThumbnail';
import { formatPrice, getFeaturedProducts } from '../modules/catalog/services/ProductService';

const Home: NextPage = () => {
  const [products, setProduct] = useState<ProductThumbnail[]>([]);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(0);

  useEffect(() => {
    getFeaturedProducts(pageNo).then((res) => {
      setProduct(res.productList);
      setTotalPage(res.totalPage);
    });
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

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
      <ReactPaginate
        forcePage={pageNo}
        previousLabel={'Previous'}
        nextLabel={'Next'}
        pageCount={totalPage}
        onPageChange={changePage}
        containerClassName={'paginationBtns'}
        previousLinkClassName={'previousBtn'}
        nextClassName={'nextBtn'}
        disabledClassName={'paginationDisabled'}
        activeClassName={'paginationActive'}
      />
    </>
  );
};
export default Home;
