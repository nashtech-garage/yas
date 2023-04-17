import { NextPage } from 'next';
import Head from 'next/head';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { UPDATE_SUCCESSFULLY } from '../../common/constants/Common';
import { Input } from '../../common/items/Input';
import { Customer } from '../../modules/profile/models/Customer';
import { ProfileRequest } from '../../modules/profile/models/ProfileRequest';
import { getMyProfile, updateCustomer } from '../../modules/profile/services/ProfileService';
import styles from '../../styles/address.module.css';
import { FaArrowRight } from 'react-icons/fa';
import clsx from 'clsx';
import { useRouter } from 'next/router';
import { TiContacts } from 'react-icons/ti';
import { CgProfile } from 'react-icons/cg';
import UserProfileLeftSideBar from '@/common/components/UserProfileLeftSideBar';
import ProfileLayout from '@/common/components/ProfileLayout';
import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'Profile',
    url: '#',
  },
];

const Profile: NextPage = () => {
  const { handleSubmit, register } = useForm<Customer>();
  const [customer, setCustomer] = useState<Customer>();
  const router = useRouter();

  useEffect(() => {
    getMyProfile()
      .then((data) => {
        setCustomer(data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  const onSubmit = async (data: any, event: any) => {
    const request: ProfileRequest = {
      firstName: event.target.firstName.value,
      lastName: event.target.lastName.value,
      email: event.target.email.value,
    };
    updateCustomer(request).then(async (_res) => {
      toast.success(UPDATE_SUCCESSFULLY);
    });
  };

  return (
    <ProfileLayout breakcrumb={crumb} title="Profile" menuActive="profile">
      <form onSubmit={handleSubmit(onSubmit)} className="w-75">
        <Input
          labelText="Username"
          field="username"
          defaultValue={customer?.username}
          register={register}
          disabled
        />
        <Input
          labelText="First name"
          field="firstName"
          defaultValue={customer?.firstName}
          register={register}
        />
        <Input
          labelText="Last name"
          field="lastName"
          defaultValue={customer?.lastName}
          register={register}
        />
        <Input labelText="Email" field="email" defaultValue={customer?.email} register={register} />
        <div className="text-center">
          <button className="btn btn-primary" type="submit">
            Update
          </button>
          <Link href="/">
            <button className="btn btn-secondary m-3">Cancel</button>
          </Link>
        </div>
      </form>
    </ProfileLayout>
  );
};

export default Profile;
