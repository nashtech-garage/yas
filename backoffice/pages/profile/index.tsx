import { getMyProfile, updateCustomer } from '../../modules/customer/services/CustomerService';
import { Customer, CustomerUpdateVM } from '../../modules/customer/models/Customer';
import { useForm } from 'react-hook-form';
import { useEffect, useState } from 'react';
import ProfileForm from '../../modules/customer/components/ProfileForm';
import { NextPage } from 'next';
import { ResponseStatus, UPDATE_SUCCESSFULLY } from '../../constants/Common';
import { toast } from 'react-toastify';

const Profile: NextPage = () => {
  const { register, handleSubmit } = useForm<Customer>();
  const [customer, setCustomer] = useState<Customer>();
  const [userId, setUserId] = useState<string>();

  useEffect(() => {
    getMyProfile()
      .then((data) => {
        setCustomer(data);
        setUserId(data.id);
      })
      .catch((err) => {});
  }, []);

  const onSubmit = async (data: any, event: any) => {
    const request: CustomerUpdateVM = {
      firstName: event.target.firstName.value,
      lastName: event.target.lastName.value,
      email: event.target.email.value,
    };
    if (userId) {
      let response = await updateCustomer(userId, request);
      if (response.status === ResponseStatus.SUCCESS) {
        toast.success(UPDATE_SUCCESSFULLY);
      }
    }
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
