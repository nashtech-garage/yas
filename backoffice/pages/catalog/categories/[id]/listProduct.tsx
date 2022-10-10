import type { NextPage } from 'next';
import { useRouter } from 'next/router';
import { Table } from 'react-bootstrap';
import React, { useEffect, useState } from 'react';
import { ProductThumbnail } from '../../../../modules/catalog/models/ProductThumbnail';
import { getProductsByCategory } from '../../../../modules/catalog/services/CategoryService';
import { getCategory } from '../../../../modules/catalog/services/CategoryService';
import ReactPaginate from 'react-paginate';

const GetProductsByCategory: NextPage = () => {
  const [products, setProducts] = useState<ProductThumbnail[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [categorySlug, setCategorySlug] = useState<string>('');
  const router = useRouter();
  const { id } = router.query;
  useEffect(() => {
    if (id)
      getCategory(+id).then((data) => {
        setCategorySlug(data.slug);
      });
  }, [id]);
  useEffect(() => {
    setLoading(true);
    getProductsByCategory(pageNo, categorySlug).then((data) => {
      setTotalPage(data.totalPages);
      setProducts(data.productContent);
      setLoading(false);
    });
  }, [pageNo, categorySlug]);
  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };
  if (isLoading) return <p>Loading...</p>;
  if (!products) return <p>No product</p>;
  return (
    <>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Slug</th>
          </tr>
        </thead>
        <tbody>
          {products &&
            products.map((product) => (
              <tr key={product.id}>
                <td>{product.id}</td>
                <td>{product.name}</td>
                <td>{product.slug}</td>
              </tr>
            ))}
        </tbody>
      </Table>

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

export default GetProductsByCategory;
