import { NextPage } from 'next';
import Head from 'next/head';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { UPDATE_SUCCESSFULLY } from '../../common/constants/Common';
import { Input } from '../../common/items/Input';
import { Customer } from '../../modules/profile/models/Customer';
import { ProfileRequest } from '../../modules/profile/models/ProfileRequest';
import { getMyProfile, updateCustomer } from '../../modules/profile/services/ProfileService';

const Profile: NextPage = () => {
  const { handleSubmit, register } = useForm<Customer>();
  const [customer, setCustomer] = useState<Customer>();

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
    updateCustomer(request).then(async (res) => {
      toast.success(UPDATE_SUCCESSFULLY);
    });
  };

  return (
    <>
      <Head>
        <title>Profile</title>
      </Head>
      <h2>User Profile</h2>
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-6">
            <form onSubmit={handleSubmit(onSubmit)}>
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
              <Input
                labelText="Email"
                field="email"
                defaultValue={customer?.email}
                register={register}
              />
              <div className="text-center">
                <button className="btn btn-primary" type="submit">
                  Update
                </button>
                <Link href="/">
                  <button className="btn btn-secondary m-3">Cancel</button>
                </Link>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
};

export default Profile;
