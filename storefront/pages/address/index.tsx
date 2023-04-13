import { NextPage } from 'next';
import Head from 'next/head';
import React, { useEffect, useState } from 'react';
import { Address } from '../../modules/address/models/AddressModel';
import { deleteAddress } from '../../modules/address/services/AddressService';
import {
  chooseDefaultAddress,
  deleteUserAddress,
  getUserAddress,
} from '../../modules/customer/services/CustomerService';
import ModalDeleteCustom from '../../common/items/ModalDeleteCustom';
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
import { useRouter } from 'next/router';
import UserProfileLeftSideBar from '@/common/components/UserProfileLeftSideBar';
import { TiContacts } from 'react-icons/ti';

const Address: NextPage = () => {
  const router = useRouter();
  const [addresses, setAddresses] = useState<Address[]>([]);

  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [addressIdWantToDelete, setAddressIdWantToDelete] = useState<number>(0);
  const [showModalChooseDefaultAddress, setShowModalChooseDefaultAddress] =
    useState<boolean>(false);
  const [defaultAddress, setDefaultAddress] = useState<number>(0);
  const [currentDefaultAddress, setCurrentDefaultAddress] = useState<number>(0);

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (addressIdWantToDelete == 0) {
      return;
    }
    deleteUserAddress(addressIdWantToDelete || 0)
      .then(() => {
        deleteAddress(addressIdWantToDelete || 0);
        getUserAddress().then((res) => {
          setAddresses(res);
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
    getUserAddress().then((res) => {
      setAddresses(res);
      setCurrentDefaultAddress(res.find((address: any) => address.isActive == true)?.id);
    });
  }, []);

  if (addresses.length == 0) {
    return (
      <>
        <div className="container">
          <div className="row">
            <Head>
              <title>Address</title>
            </Head>
            <div style={{ minHeight: '550px' }}>
              <div
                className="pt-5 col-md-12 mb-2 d-flex justify-content-between"
                style={{ height: '100px' }}
              >
                <h2 className="mb-3">Address list</h2>
                <button className="btn btn-primary p-0 ">
                  <Link
                    href={'/address/create'}
                    className={clsx(styles['link-redirect'], 'align-items-center', 'd-flex', 'p-2')}
                    style={{ width: '100%', display: 'inline-block' }}
                  >
                    <BiPlusMedical />
                    <span style={{ padding: '0 0 0 5px' }}>Create address</span>
                  </Link>
                </button>
              </div>
              <div className="container p-0">
                <div className="row">
                  <div className="col-md-3">
                    <UserProfileLeftSideBar type={'address'} />
                  </div>
                  <div className="col-md-9">
                    <p>No address</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </>
    );
  }
  return (
    <>
      <Head>
        <title>Address</title>
      </Head>
      <div className="container">
        <div className="row">
          <Head>
            <title>Address</title>
          </Head>
          <div style={{ minHeight: '550px' }}>
            <div
              className="pt-5 d-flex justify-content-between col-md-12 mb-2"
              style={{ height: '100px' }}
            >
              <h2 className="mb-3">Address list</h2>
              <button className="p-0 btn btn-primary">
                <Link
                  href={'/address/create'}
                  className={clsx(styles['link-redirect'], 'd-flex', 'align-items-center', 'p-2')}
                  style={{ display: 'inline-block', width: '100%' }}
                >
                  <BiPlusMedical />
                  <span style={{ padding: '0 0 0 5px' }}>Create address</span>
                </Link>
              </button>
            </div>
            <div className="container p-0">
              <div className="row">
                <div className="col-md-3">
                  <UserProfileLeftSideBar type={'address'} />
                </div>
                <div className="col-md-9">
                  <div className="container p-0">
                    <div className="row">
                      {addresses.map((address) => {
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
                                    <p style={{ fontSize: '14px' }}>
                                      Contact name: {address.contactName}
                                    </p>
                                    <p
                                      style={{
                                        fontSize: '14px',
                                        wordBreak: 'break-word',
                                      }}
                                    >
                                      Address: {address.addressLine1}
                                    </p>
                                    <p style={{ fontSize: '14px' }}>
                                      Phone number: {address.phone}
                                    </p>
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
                      })}
                    </div>
                  </div>
                </div>
              </div>
            </div>
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
