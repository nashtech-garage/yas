import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Container, Row } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';

import ImageWithFallBack from 'common/components/ImageWithFallback';
import { ProductThumbnail } from 'modules/catalog/models/ProductThumbnail';
import { getFeaturedProducts } from 'modules/catalog/services/ProductService';
import { formatPrice } from 'utils/formatPrice';

const FeaturedProduct = () => {
  const [products, setProduct] = useState<ProductThumbnail[]>([]);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(0);

  useEffect(() => {
    getFeaturedProducts(pageNo).then((res) => {
      setProduct([...res.productList, ...res.productList, ...res.productList, ...res.productList]);
      setTotalPage(res.totalPage);
    });
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

  return (
    <Container className="featured-product-container">
      <div className="title">Featured products</div>
      <Row xs={6}>
        {products.length > 0 &&
          products.map((product) => (
            <Link className="product" href={`/products/${product.slug}`} key={product.id}>
              <div className="product-card">
                <div className="image-wrapper">
                  <ImageWithFallBack src={product.thumbnailUrl} alt={product.name} />
                </div>
                <div className="info-wrapper">
                  <h3 className="prod-name">
                    {product.name} Lorem ipsum dolor sit amet consectetur, adipisicing elit.
                    Dignissimos libero reiciendis nulla repellendus. Tenetur quas sequi fugiat sed
                    cupiditate, dignissimos architecto, est esse delectus facere, aut reprehenderit
                    aliquam voluptates ipsum.
                  </h3>
                  <div className="rating-sold">
                    <div className="star">
                      0 <i className="bi bi-star-fill"></i>
                    </div>{' '}
                    |{' '}
                    <div className="sold">
                      0 <span>sold</span>
                    </div>
                  </div>

                  <div className="price">{product?.price && formatPrice(product?.price)}</div>

                  <hr />

                  <div className="delivery">Fast delivery 2 hours</div>
                </div>
              </div>
            </Link>
          ))}
      </Row>
      <ReactPaginate
        forcePage={pageNo}
        previousLabel={'Previous'}
        nextLabel={'Next'}
        pageCount={totalPage}
        onPageChange={changePage}
        containerClassName={'paginationBtns'}
        previousClassName={'previousBtn'}
        nextClassName={'nextBtn'}
        disabledClassName={'paginationDisabled'}
        activeClassName={'paginationActive'}
      />
    </Container>
  );
};

export default FeaturedProduct;
