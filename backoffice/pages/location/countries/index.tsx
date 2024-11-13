import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table, Form } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';

import ModalDeleteCustom from '@commonItems/ModalDeleteCustom';
import { handleDeletingResponse } from '@commonServices/ResponseStatusHandlingService';
import type { Country } from '@locationModels/Country';
import { deleteCountry, getPageableCountries } from '@locationServices/CountryService';
import { COUNTRY_URL, DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from 'constants/Common';

const CountryList: NextPage = () => {
  const [countryIdWantToDelete, setCountryIdWantToDelete] = useState<number>(-1);
  const [countryNameWantToDelete, setCountryNameWantToDelete] = useState<string>('');
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [countries, setCountries] = useState<Country[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState<number>(DEFAULT_PAGE_NUMBER);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [itemsPerPage, setItemsPerPage] = useState<number>(DEFAULT_PAGE_SIZE);
  const [goToPage, setGoToPage] = useState<string>('');

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (countryIdWantToDelete == -1) {
      return;
    }
    deleteCountry(countryIdWantToDelete)
      .then((response) => {
        setShowModalDelete(false);
        handleDeletingResponse(response, countryNameWantToDelete);
        setPageNo(DEFAULT_PAGE_NUMBER);
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

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

  const handleItemsPerPageChange = (e: React.ChangeEvent<any>) => {
    const value = parseInt((e.target as HTMLSelectElement).value, 10);
    setItemsPerPage(value);
    setPageNo(0);
  };

  const handleGoToPageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setGoToPage(e.target.value);
  };

  const goToPageHandler = () => {
    const page = parseInt(goToPage, 10) - 1;
    if (page >= 0 && page < totalPage) {
      setPageNo(page);
      setGoToPage('');
    } else {
      alert('Invalid page number');
    }
  };

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
        <>
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
          <div className="pagination-helper mt-3">
            <div className="pagination-tool me-5">
              <p>To page</p>
              <Form.Control
                type="number"
                value={goToPage}
                onChange={handleGoToPageChange}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    goToPageHandler();
                  }
                }}
              />
              <Button variant="primary" onClick={goToPageHandler} className="ml-2">
                Go
              </Button>
            </div>
            <div className="pagination-tool">
              <p>Show</p>
              <Form.Control as="select" value={itemsPerPage} onChange={handleItemsPerPageChange}>
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="15">15</option>
                <option value="20">20</option>
              </Form.Control>
              <p>/ page</p>
            </div>
          </div>
        </>
      )}
    </>
  );
};

export default CountryList;
