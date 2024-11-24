import type { NextPage } from 'next';
import { useRouter } from 'next/router';
import { Table } from 'react-bootstrap';
import React, { useEffect, useState } from 'react';
import { ProductThumbnail } from '../../../../modules/catalog/models/ProductThumbnail';
import {
  getProductsByCategory,
  getCategory,
} from '../../../../modules/catalog/services/CategoryService';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonServices/PaginationService';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from '@constants/Common';

const GetProductsByCategory: NextPage = () => {
  const [products, setProducts] = useState<ProductThumbnail[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [categorySlug, setCategorySlug] = useState<string>('');
  const router = useRouter();
  const { id } = router.query;

  const { pageNo, totalPage, setTotalPage, changePage } = usePagination({
    initialPageNo: DEFAULT_PAGE_NUMBER,
    initialItemsPerPage: DEFAULT_PAGE_SIZE,
  });

  useEffect(() => {
    if (id)
      getCategory(+id).then((data) => {
        setCategorySlug(data.slug);
      });
  }, [id]);
  useEffect(() => {
    setLoading(true);
    getProductsByCategory(pageNo, DEFAULT_PAGE_SIZE, categorySlug).then((data) => {
      setTotalPage(data.totalPages);
      setProducts(data.productContent);
      setLoading(false);
    });
  }, [pageNo, categorySlug]);

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
      {totalPage > 1 && (
        <Pagination pageNo={pageNo} totalPage={totalPage} onPageChange={changePage} />
      )}
    </>
  );
};

export default GetProductsByCategory;
