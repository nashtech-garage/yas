import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';

import ModalDeleteCustom from '@commonItems/ModalDeleteCustom';
import { handleDeletingResponse } from '@commonServices/ResponseStatusHandlingService';
import type { TaxClass } from '@taxModels/TaxClass';
import { deleteTaxClass, getPageableTaxClasses } from '@taxServices/TaxClassService';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, TAX_CLASS_URL } from 'constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonServices/PaginationService';

const TaxClassList: NextPage = () => {
  const [taxClassIdWantToDelete, setTaxClassIdWantToDelete] = useState<number>(-1);
  const [taxClassNameWantToDelete, setTaxClassNameWantToDelete] = useState<string>('');
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [taxClasses, setTaxClasses] = useState<TaxClass[]>([]);
  const [isLoading, setLoading] = useState(false);

  const { pageNo, totalPage, setTotalPage, itemsPerPage, setPageNo, changePage } = usePagination({
    initialPageNo: DEFAULT_PAGE_NUMBER,
    initialItemsPerPage: DEFAULT_PAGE_SIZE,
  });

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (taxClassIdWantToDelete == -1) {
      return;
    }
    deleteTaxClass(taxClassIdWantToDelete)
      .then((response) => {
        setShowModalDelete(false);
        handleDeletingResponse(response, taxClassNameWantToDelete);
        setPageNo(DEFAULT_PAGE_NUMBER);
        getListTaxClass();
      })
      .catch((error) => console.log(error));
  };

  const getListTaxClass = () => {
    getPageableTaxClasses(pageNo, DEFAULT_PAGE_SIZE)
      .then((data) => {
        setTotalPage(data.totalPages);
        setTaxClasses(data.taxClassContent);
        setLoading(false);
      })
      .catch((error) => console.log(error));
  };

  useEffect(() => {
    setLoading(true);
    getListTaxClass();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageNo]);

  if (isLoading) return <p>Loading...</p>;
  if (!taxClasses) return <p>No Tax Class</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Tax Class</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href={`${TAX_CLASS_URL}/create`}>
            <Button>Create Tax Class</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {taxClasses.map((taxClass) => (
            <tr key={taxClass.id}>
              <td>{taxClass.id}</td>
              <td>{taxClass.name}</td>
              <td>
                <Link href={`${TAX_CLASS_URL}/${taxClass.id}/edit`}>
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
                    setTaxClassIdWantToDelete(taxClass.id);
                    setTaxClassNameWantToDelete(taxClass.name);
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
        nameWantToDelete={taxClassNameWantToDelete}
        handleDelete={handleDelete}
        action="delete"
      />
      {totalPage > 1 && (
        <Pagination
          pageNo={pageNo}
          totalPage={totalPage}
          itemsPerPage={itemsPerPage}
          onPageChange={changePage}
          showHelpers={false}
        />
      )}
    </>
  );
};

export default TaxClassList;
