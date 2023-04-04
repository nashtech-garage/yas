import { useEffect, useState } from 'react';
import { Container, Row } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';

import ProductItems from 'common/items/ProductItems';
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
      <Row xs={6} style={{ gap: '16px' }}>
        <ProductItems products={products} />
      </Row>
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
    </Container>
  );
};

export default FeaturedProduct;
