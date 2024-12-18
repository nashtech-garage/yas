import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import type { ProductTemplate } from '@catalogModels/ProductTemplate';
import { getPageableProductTemplates } from '@catalogServices/ProductTemplateService';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from '@constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonHooks/usePagination';

const ProductTemplate: NextPage = () => {
  const [productTemplates, setProductTemplates] = useState<ProductTemplate[]>();
  const [isLoading, setLoading] = useState<boolean>(false);

  const { pageNo, totalPage, setTotalPage, changePage } = usePagination({
    initialPageNo: DEFAULT_PAGE_NUMBER,
    initialItemsPerPage: DEFAULT_PAGE_SIZE,
  });

  useEffect(() => {
    setLoading(true);
    getListProductTemplate();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageNo]);

  const getListProductTemplate = () => {
    getPageableProductTemplates(pageNo, DEFAULT_PAGE_SIZE)
      .then((data) => {
        setTotalPage(data.totalPages);
        setProductTemplates(data.productTemplateVms);
        setLoading(false);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  if (isLoading) return <p>Loading...</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Product Template</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href={'/catalog/product-templates/create'}>
            <Button>Create Product Templates</Button>
          </Link>
        </div>
      </div>
      {!productTemplates ? (
        <p>No Product Templates</p>
      ) : (
        <>
          <Table striped bordered hover>
            <thead>
              <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {productTemplates.map((obj) => (
                <tr key={obj.id}>
                  <td>{obj.id}</td>
                  <td>{obj.name}</td>
                  <td>
                    <Link href={`/catalog/product-templates/${obj.id}/edit`}>
                      <button className="btn btn-outline-primary btn-sm" type="button">
                        Edit
                      </button>
                    </Link>
                    &nbsp;
                    <button className="btn btn-outline-danger btn-sm" type="button">
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
          {totalPage > 1 && (
            <Pagination pageNo={pageNo} totalPage={totalPage} onPageChange={changePage} />
          )}
        </>
      )}
    </>
  );
};
export default ProductTemplate;
