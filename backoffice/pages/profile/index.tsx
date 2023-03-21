import { getMyProfile, updateCustomer } from '../../modules/customer/services/CustomerService';
import { Customer } from '../../modules/customer/models/Customer';
import { useForm } from 'react-hook-form';
import { useEffect, useState } from 'react';
import ProfileForm from '../../modules/customer/components/ProfileForm';
import { NextPage } from 'next';
import { UPDATE_SUCCESSFULLY } from '../../constants/Common';
import { ProfileRequest } from '../../modules/profile/models/ProfileRequest';
import { toast } from 'react-toastify';

const Profile: NextPage = () => {
  const { register, handleSubmit } = useForm<Customer>();
  const [customer, setCustomer] = useState<Customer>();

  useEffect(() => {
    getMyProfile()
      .then((data) => {
        setCustomer(data);
      })
      .catch((err) => {});
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
      <h2>Update User</h2>
      <form onSubmit={handleSubmit(onSubmit)}>
        <ProfileForm register={register} customer={customer} />
      </form>
    </>
  );
};

export default Profile;
