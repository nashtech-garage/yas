import { NextPage } from 'next';
import { useEffect, useState } from 'react';
import ReactPaginate from 'react-paginate';

import ProductItems from 'common/items/ProductItems';
import type { ProductThumbnail } from 'modules/catalog/models/ProductThumbnail';
import { getFeaturedProducts } from 'modules/catalog/services/ProductService';
import Category from 'modules/home/components/Category';

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
    <div className="hompage-container">
      <Category />
      <h2>Featured products</h2>
      <ProductItems products={products} />
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
    </div>
  );
};
export default Home;
