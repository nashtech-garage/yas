import type { NextPage } from "next";
import Link from "next/link";
import { useEffect, useState } from "react";
import { Button, Stack, Table } from "react-bootstrap";
import ReactPaginate from "react-paginate";
import type { Product } from "../../../modules/catalog/models/Product";
import { getProducts } from "../../../modules/catalog/services/ProductService";

const ProductList: NextPage = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState<number>(0)
  const [totalPage, setTotalPage] = useState<number>(1)

  useEffect(() => {
    setLoading(true);
    getProducts(pageNo).then((data) => {
      setTotalPage(data.totalPages)
      setProducts(data.productContent)
      setLoading(false);
    });
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected)
  }

  if (isLoading) return <p>Loading...</p>;
  if (!products) return <p>No product</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Products</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href="/catalog/products/create">
            <Button>Create Product</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              <td>{product.id}</td>
              <td>{product.name}</td>
              <td>
                <Stack direction="horizontal" gap={3}>
                  <Link href={`/catalog/products/${product.id}/edit`}>
                    <a>Edit</a>
                  </Link>
                </Stack>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <ReactPaginate
        forcePage={pageNo}
        previousLabel={"Previous"}
        nextLabel={"Next"}
        pageCount={totalPage}
        onPageChange={changePage}
        containerClassName={"paginationBtns"}
        previousLinkClassName={"previousBtn"}
        nextClassName={"nextBtn"}
        disabledClassName={"paginationDisabled"}
        activeClassName={"paginationActive"}

      />
    </>
  );
};

export default ProductList;
