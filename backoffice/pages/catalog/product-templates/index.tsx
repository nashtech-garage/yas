import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import { ProductTemplate } from '@catalogModels/ProductTemplate';
import {
  getPageableProductTemplates,
} from '@catalogServices/ProductTemplateService';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from '@constants/Common';

const ProductTemplate: NextPage = () => {
  const [productTemplates, setProductTemplates] = useState<ProductTemplate[]>();
  const [isLoading,setLoading] = useState<boolean>(false);
  const [pageNo, setPageNo] = useState<number>(DEFAULT_PAGE_NUMBER);
  const [totalPage,setTotalPage] = useState<number>(1);

  useEffect(() => {
    setLoading(true);
    getListProductTemplate();
  }, [pageNo]);

  const getListProductTemplate = () =>{
    getPageableProductTemplates(pageNo,DEFAULT_PAGE_SIZE)
    .then((data)=>{
        setTotalPage(data.totalPages);
        setProductTemplates(data.productTemplateVms)
        setLoading(false);
    })
    .catch((error)=>{console.log(error)})
  }

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

  if (isLoading) return <p>Loading...</p>;
  if (!productTemplates) return <p>No Product Templates</p>;
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
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {productTemplates.map((obj)=>(
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
              <button
                className="btn btn-outline-danger btn-sm"
                type="button"
              >
                Delete
              </button>
            </td>
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
        containerClassName={'pagination-container'}
        previousClassName={'previous-btn'}
        nextClassName={'next-btn'}
        disabledClassName={'pagination-disabled'}
        activeClassName={'pagination-active'}
      />
    </>
  );
};
export default ProductTemplate;
