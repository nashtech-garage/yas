import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';
import type { Brand } from '../../../modules/catalog/models/Brand';
import { deleteBrand, getPageableBrands } from '../../../modules/catalog/services/BrandService';
import CustomToast from '../../../common/items/CustomToast';
import { useDeletingContext } from '../../../common/hooks/UseToastContext';
import ReactPaginate from 'react-paginate';
import { DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER } from '../../../constants/Common';

const BrandList: NextPage = () => {
  const { toastVariant, toastHeader, showToast, setShowToast, handleDeletingResponse } =
    useDeletingContext();
  const [brandIdWantToDelete, setBrandIdWantToDelete] = useState<number>(-1);
  const [brandNameWantToDelete, setBrandNameWantToDelete] = useState<string>('');
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [brands, setBrands] = useState<Brand[]>([]);
  const [pageNo, setPageNo] = useState<number>(DEFAULT_PAGE_NUMBER);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [isLoading, setLoading] = useState(false);
  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (brandIdWantToDelete == -1) {
      return;
    }
    deleteBrand(brandIdWantToDelete).then((response) => {
      setShowModalDelete(false);
      handleDeletingResponse(response, brandNameWantToDelete);
      getListBrand();
    });
  };
  const getListBrand = () => {
    getPageableBrands(pageNo, DEFAULT_PAGE_SIZE).then((data) => {
      setTotalPage(data.totalPages);
      setBrands(data.brandContent);
      setLoading(false);
    });
  };
  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };
  useEffect(() => {
    setLoading(true);
    getListBrand();
  }, [pageNo]);
  if (isLoading) return <p>Loading...</p>;
  if (!brands) return <p>No brand</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Brands</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href="/catalog/brands/create">
            <Button>Create Brand</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>Slug</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {brands.map((brand) => (
            <tr key={brand.id}>
              <td>{brand.id}</td>
              <td>{brand.name}</td>
              <td>{brand.slug}</td>
              <td>
                <Link href={`/catalog/brands/${brand.id}/edit`}>
                  <button className="btn btn-outline-primary btn-sm" type="button">
                    Edit
                  </button>
                </Link>
                &nbsp;
                <button
                  className="btn btn-outline-danger btn-sm"
                  type="button"
                  onClick={() => {
                    setShowModalDelete(true);
                    setBrandIdWantToDelete(brand.id);
                    setBrandNameWantToDelete(brand.name);
                  }}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <ModalDeleteCustom
        showModalDelete={showModalDelete}
        handleClose={handleClose}
        nameWantToDelete={brandNameWantToDelete}
        handleDelete={handleDelete}
        action="delete"
      />
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
      {showToast && (
        <CustomToast
          variant={toastVariant}
          header={toastHeader}
          show={showToast}
          setShow={setShowToast}
        ></CustomToast>
      )}
    </>
  );
};

export default BrandList;
