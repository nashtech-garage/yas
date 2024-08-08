import { useEffect, useState } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';

import ProductCard from '@/common/components/ProductCard';
import { ProductThumbnail } from 'modules/catalog/models/ProductThumbnail';
import { getFeaturedProducts } from 'modules/catalog/services/ProductService';

const FeaturedProduct = () => {
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
    <Container className="featured-product-container">
      <div className="title">Featured products</div>
      <Row xs={5} xxl={6}>
        {products.length > 0 &&
          products.map((product) => (
            <Col key={product.id}>
              <ProductCard product={product} />
            </Col>
          ))}
      </Row>
      {totalPage > 1 && (
        <ReactPaginate
          forcePage={pageNo}
          previousLabel={'Previous'}
          nextLabel={'Next'}
          pageCount={totalPage}
          onPageChange={changePage}
          containerClassName={'pagination-container'}
          previousClassName={'previous-btn'}
          nextClassName={'next-btn'}
          disabledClassName={'pagination-disabled'}
          activeClassName={'pagination-active'}
        />
      )}
    </Container>
  );
};

export default FeaturedProduct;
