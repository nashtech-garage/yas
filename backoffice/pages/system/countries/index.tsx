import type { NextPage } from 'next';
import Link from 'next/link';
import ReactPaginate from 'react-paginate';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';
import type { Country } from '../../../modules/system/models/Country';
import { handleDeletingResponse } from '../../../common/services/ResponseStatusHandlingService';
import {
  deleteCountry,
  getPageableCountries,
} from '../../../modules/system/services/CountryService';
import { DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER } from '../../../constants/Common';

const CountryList: NextPage = () => {
  const [countryIdWantToDelete, setCountryIdWantToDelete] = useState<number>(-1);
  const [countryNameWantToDelete, setCountryNameWantToDelete] = useState<string>('');
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [countries, setCountries] = useState<Country[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState<number>(DEFAULT_PAGE_NUMBER);
  const [totalPage, setTotalPage] = useState<number>(1);

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (countryIdWantToDelete == -1) {
      return;
    }
    deleteCountry(countryIdWantToDelete).then((response) => {
      setShowModalDelete(false);
      handleDeletingResponse(response, countryNameWantToDelete);
      setPageNo(DEFAULT_PAGE_NUMBER);
      getListCountry();
    });
  };
  const getListCountry = () => {
    getPageableCountries(pageNo, DEFAULT_PAGE_SIZE).then((data) => {
      setTotalPage(data.totalPages);
      setCountries(data.countryContent);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    getListCountry();
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
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
          <Link href="/system/countries/create">
            <Button>Create Country</Button>
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
          {countries.map((country) => (
            <tr key={country.id}>
              <td>{country.id}</td>
              <td>{country.name}</td>
              <td>
                <Link href={`/system/countries/${country.id}/edit`}>
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

export default CountryList;
