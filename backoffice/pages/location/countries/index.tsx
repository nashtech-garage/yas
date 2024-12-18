import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';

import ModalDeleteCustom from '@commonItems/ModalDeleteCustom';
import { handleDeletingResponse } from '@commonServices/ResponseStatusHandlingService';
import type { Country } from '@locationModels/Country';
import { deleteCountry, getPageableCountries } from '@locationServices/CountryService';
import { COUNTRY_URL, DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from 'constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonHooks/usePagination';

const CountryList: NextPage = () => {
  const [countryIdWantToDelete, setCountryIdWantToDelete] = useState<number>(-1);
  const [countryNameWantToDelete, setCountryNameWantToDelete] = useState<string>('');
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [countries, setCountries] = useState<Country[]>([]);
  const [isLoading, setLoading] = useState(false);

  const { pageNo, totalPage, setTotalPage, paginationControls, changePage } = usePagination({
    initialPageNo: DEFAULT_PAGE_NUMBER,
    initialItemsPerPage: DEFAULT_PAGE_SIZE,
  });

  const itemsPerPage = paginationControls?.itemsPerPage?.value ?? DEFAULT_PAGE_SIZE;

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (countryIdWantToDelete == -1) {
      return;
    }
    deleteCountry(countryIdWantToDelete)
      .then((response) => {
        setShowModalDelete(false);
        handleDeletingResponse(response, countryNameWantToDelete);
        changePage({ selected: DEFAULT_PAGE_NUMBER });
        getListCountry();
      })
      .catch((error) => console.log(error));
  };

  const getListCountry = () => {
    getPageableCountries(pageNo, itemsPerPage)
      .then((data) => {
        setTotalPage(data.totalPages);
        setCountries(data.countryContent);
        setLoading(false);
      })
      .catch((error) => console.log(error));
  };

  useEffect(() => {
    setLoading(true);
    getListCountry();
  }, [pageNo, itemsPerPage]);

  if (isLoading) return <p>Loading...</p>;
  if (!countries) return <p>No country</p>;

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Countries</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href={`${COUNTRY_URL}/create`}>
            <Button>Create Country</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Code</th>
            <th>Name</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {countries.map((country) => (
            <tr key={country.id}>
              <td>{country.code2}</td>
              <td>{country.name}</td>
              <td>
                <Link href={`${COUNTRY_URL}/${country.id}/edit`}>
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
                    setCountryIdWantToDelete(country.id);
                    setCountryNameWantToDelete(country.name);
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
        nameWantToDelete={countryNameWantToDelete}
        handleDelete={handleDelete}
        action="delete"
      />
      {totalPage > 1 && (
        <Pagination
          pageNo={pageNo}
          totalPage={totalPage}
          paginationControls={paginationControls}
          onPageChange={changePage}
        />
      )}
    </>
  );
};

export default CountryList;
