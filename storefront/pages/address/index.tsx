import { NextPage } from 'next';
import Head from 'next/head';
import React, { useEffect, useState } from 'react';
import queryString from 'query-string';
import { Address } from '../../modules/address/models/AddressModel';
import { deleteAddress } from '../../modules/address/services/AddressService';
import {
  chooseDefaultAddress,
  deleteUserAddress,
  getUserAddress,
} from '../../modules/customer/services/CustomerService';
import ModalDeleteCustom from '../../common/items/ModalDeleteCustom';
import { TiContacts } from 'react-icons/ti';
import { HiCheckCircle } from 'react-icons/hi';
import { FiEdit } from 'react-icons/fi';
import { BiPlusMedical } from 'react-icons/bi';
import { toast } from 'react-toastify';
import { DELETE_SUCCESSFULLY, UPDATE_SUCCESSFULLY } from '../../common/constants/Common';
import { FaTrash } from 'react-icons/fa';
import Link from 'next/link';
import styles from '../../styles/address.module.css';
import clsx from 'clsx';
import ModalChooseDefaultAddress from 'common/items/ModalChooseDefaultAddress';
import ReactPaginate from 'react-paginate';

const Address: NextPage = () => {
  const [addresses, setAddresses] = useState<Address[]>([]);

  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [addressIdWantToDelete, setAddressIdWantToDelete] = useState<number>(0);
  const [showModalChooseDefaultAddress, setShowModalChooseDefaultAddress] =
    useState<boolean>(false);
  const [defaultAddress, setDefaultAddress] = useState<number>(0);
  const [currentDefaultAddress, setCurrentDefaultAddress] = useState<number>(0);

  const [pageNo, setPageNo] = useState<number>(0);
  const pageSize = 9;
  const [totalPage, setTotalPage] = useState<number>(0);

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (addressIdWantToDelete == 0) {
      return;
    }
    deleteUserAddress(addressIdWantToDelete || 0)
      .then(() => {
        deleteAddress(addressIdWantToDelete || 0);
        let params = queryString.stringify({ pageNo: pageNo, pageSize: pageSize });
        getUserAddress(params).then((res) => {
          setAddresses(res.addressList);
        });
        setShowModalDelete(false);
        toast.success(DELETE_SUCCESSFULLY);
      })
      .catch((e) => {
        console.log(e);
      });
  };

  const handleCloseModalChooseDefaultAddress: any = () => setShowModalChooseDefaultAddress(false);
  const handleChoose: any = () => {
    chooseDefaultAddress(defaultAddress)
      .then(() => {
        {
          setShowModalChooseDefaultAddress(false);
          toast.success(UPDATE_SUCCESSFULLY);
          setCurrentDefaultAddress(defaultAddress);
        }
      })
      .catch((e) => console.log(e));
  };
  useEffect(() => {
    let params = queryString.stringify({ pageNo: pageNo, pageSize: pageSize });
    getUserAddress(params).then((res) => {
      setAddresses(res.addressList);
      setTotalPage(res.totalPages);

      setCurrentDefaultAddress(
        res.addressList.find((address: any) => address.isActive == true)?.id
      );
    });
  }, [pageNo]);

  const changePage = ({ selected }: any) => {
    setPageNo(selected);
  };
  return (
    <>
      <Head>
        <title>Address</title>
      </Head>
      <div style={{ minHeight: '550px' }}>
        <div className="container d-flex justify-content-between mb-4 pt-5">
          <h2 className="mb-3">Address list</h2>
          <button className="btn btn-primary p-0">
            <Link
              style={{ width: '100%', display: 'inline-block' }}
              className={clsx(styles['link-redirect'], 'p-2', 'd-flex', 'align-items-center')}
              href={'/address/create'}
            >
              <BiPlusMedical />
              <span style={{ padding: '0 0 0 5px' }}>Create address</span>
            </Link>
          </button>
        </div>
        <div className="container">
          <div className="row">
            {addresses.length == 0 ? (
              <>No address found</>
            ) : (
              addresses.map((address) => {
                return (
                  <div className="col-lg-4 col-md-6 col-sm-12" key={address.id}>
                    <div className={styles['card-wrapper']}>
                      <div className={clsx(styles['card-layout'], 'd-flex')}>
                        <div
                          className="d-flex justify-content-center align-items-center"
                          style={{
                            width: '100px',
                            background: '#ea1161',
                            borderRadius: '5px 0 0 5px',
                            filter: 'brightness(90%)',
                          }}
                        >
                          <div style={{ fontSize: '50px' }}>
                            <TiContacts style={{ color: '#ffffff' }} />
                          </div>
                        </div>
                        <div
                          className="p-2"
                          style={{
                            background: '#ea1161',
                            borderRadius: '0 5px 5px 0',
                            width: '100%',
                          }}
                        >
                          <div>
                            {address.id == currentDefaultAddress && (
                              <div className="m-2" style={{ float: 'right' }}>
                                <div
                                  style={{
                                    width: '15px',
                                    height: '15px',
                                    borderRadius: '50%',
                                    background: '#0eea5d',
                                  }}
                                ></div>
                              </div>
                            )}
                            <p style={{ fontSize: '14px' }}>Contact name: {address.contactName}</p>
                            <p
                              style={{
                                fontSize: '14px',
                                wordBreak: 'break-word',
                              }}
                            >
                              Address: {address.addressLine1}
                            </p>
                            <p style={{ fontSize: '14px' }}>Phone number: {address.phone}</p>
                          </div>
                          <div
                            className="d-flex justify-content-end"
                            style={{ position: 'relative', bottom: '0' }}
                          >
                            <div
                              className="m-1"
                              data-toggle="tooltip"
                              title="Active"
                              style={{ cursor: 'pointer' }}
                              onClick={() => {
                                setShowModalChooseDefaultAddress(true);
                                setDefaultAddress(address.id || 0);
                                if (defaultAddress != 0) {
                                }
                              }}
                            >
                              <HiCheckCircle />
                            </div>
                            <div className="m-1" data-toggle="tooltip" title="Edit">
                              <Link href={{ pathname: `/address/${address.id}/edit` }}>
                                <FiEdit />
                              </Link>
                            </div>
                            <div
                              className="m-1"
                              data-toggle="tooltip"
                              title="Delete"
                              onClick={() => {
                                setShowModalDelete(true);
                                setAddressIdWantToDelete(address.id || 0);
                              }}
                            >
                              <FaTrash className={styles['remove-address']} />
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })
            )}
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
          </div>
        </div>
      </div>
      <ModalDeleteCustom
        showModalDelete={showModalDelete}
        handleClose={handleClose}
        handleDelete={handleDelete}
        action="delete"
      />
      <ModalChooseDefaultAddress
        showModalChooseDefaultAddress={showModalChooseDefaultAddress}
        handleClose={handleCloseModalChooseDefaultAddress}
        handleChoose={handleChoose}
      />
    </>
  );
};

export default Address;
