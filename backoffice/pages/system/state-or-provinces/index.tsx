import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState, useRef } from 'react';
import { Button, InputGroup, Stack, Table } from 'react-bootstrap';
import Form from 'react-bootstrap/Form';
import { FaSearch } from 'react-icons/fa';
import ReactPaginate from 'react-paginate';

import type { Country } from '../../../modules/system/models/Country';
import type { StateOrProvince } from '../../../modules/system/models/StateOrProvince';
import { getPageableStateOrProvinces, getCountries } from '../../../modules/system/services/StateOrProvinceService';
import { deleteStateOrProvince, getStateOrProvinces } from '../../../modules/system/services/StateOrProvinceService';
import styles from '../../../styles/Filter.module.css';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';
import { handleDeletingResponse } from '../../../modules/catalog/services/ResponseStatusHandlingService';
import moment from 'moment';
import { DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER } from '../../../constants/Common';

const StateOrProvinceList: NextPage = () => {
  let typingTimeOutRef: null | ReturnType<typeof setTimeout> = null;
  const [stateOrProvinces, setStateOrProvinces] = useState<StateOrProvince[]>([]);
  const [isLoading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState<number>(0);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [countries, setCountries] = useState<Country[]>([]);
  const [countryName, setCountryName] = useState<string>('');
  const [countryId, setCountryId] = useState<string>(7);
  const [stateOrProvinceName, setStateOrProvinceName] = useState<string>('');

  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [stateOrProvinceNameWantToDelete, setStateOrProvinceNameWantToDelete] = useState<string>('');
  const [stateOrProvinceIdWantToDelete, setStateOrProvinceIdWantToDelete] = useState<number>(-1);

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (stateOrProvinceIdWantToDelete == -1) {
      return;
    }
    deleteStateOrProvince(stateOrProvinceIdWantToDelete)
      .then((response) => {
        setShowModalDelete(false);
        handleDeletingResponse(response, stateOrProvinceNameWantToDelete);
        getPageableStateOrProvinces(pageNo,10, countryId).then((data) => {
          setTotalPage(data.totalPages);
          setStateOrProvinces(data.stateOrProvinceContent);
          setLoading(false);
        });
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    setLoading(true);

    getPageableStateOrProvinces(pageNo, 10, countryId).then((data) => {
      console.log(data.stateOrProvinceContent);
      setTotalPage(data.totalPages);
      setStateOrProvinces(data.stateOrProvinceContent);
      setLoading(false);
    });
  }, [pageNo, countryId]);

  useEffect(() => {
    setLoading(true);
    getCountries().then((data) => {
      setCountries(data);
      setLoading(false);
    });
  }, []);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };

  if (isLoading) return <p>Loading...</p>;

  if (stateOrProvinces) return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">State Or Provinces</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href={`/system/state-or-provinces/create?countryId=${countryId}`}>
            <Button>Create</Button>
          </Link>
        </div>
        <br />
      </div>

      {/* Filter */}
      <div className="row mb-5">
        <div className="col-md-6">
          {/* <Form.Label htmlFor="brand-filter">Brand: </Form.Label> */}
          <Form.Select
            id="country-filter"
            onChange={(e) => {
              setPageNo(0);
              setCountryId(e.target.value);
              setCountryName(e.target.value);
            }}
            className={styles.filterButton}
            defaultValue={countryName | ''}
          >
            <option value={''}>All</option>
            {countries.map((country) => (
              <option key={country.id} value={country.id}>
                {country.name}
              </option>
            ))}
          </Form.Select>
        </div>
      </div>

      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Code</th>
            <th>Type</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {stateOrProvinces.map((stateOrProvince) => (
            <tr key={stateOrProvince.id}>
              <td>{stateOrProvince.id}</td>
              <td>{stateOrProvince.name}</td>
               <td>{stateOrProvince.code}</td>
                <td>{stateOrProvince.type}</td>

              <td>
                <Stack direction="horizontal" gap={3}>
                  <Link href={`/system/state-or-provinces/${stateOrProvince.id}/edit`}>
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
                      setStateOrProvinceIdWantToDelete(stateOrProvince.id);
                      setStateOrProvinceNameWantToDelete(stateOrProvince.name);
                    }}
                  >
                    Delete
                  </button>
                </Stack>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <ModalDeleteCustom
        showModalDelete={showModalDelete}
        handleClose={handleClose}
        nameWantToDelete={stateOrProvinceNameWantToDelete}
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

export default StateOrProvinceList;
