import { NextPage } from 'next';
import Head from 'next/head';
import { useForm } from 'react-hook-form';
import AddressForm from '../../modules/address/components/AddressForm';
import { Address } from '../../modules/address/models/AddressModel';
import { createAddress } from '../../modules/address/services/AddressService';
import { createUserAddress } from '../../modules/customer/services/CustomerService';
import { toast } from 'react-toastify';
import { CREATE_SUCCESSFULLY } from '../../common/constants/Common';
import { useRouter } from 'next/router';
import styles from '../../styles/address.module.css';
import clsx from 'clsx';

const CreateAddress: NextPage = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Address>();

  const router = useRouter();

  const onSubmit = async (data: any) => {
    const {
      contactName,
      phone,
      addressLine1,
      city,
      zipCode,
      districtId,
      stateOrProvinceId,
      countryId,
    } = data;
    const request: Address = {
      contactName,
      phone,
      addressLine1,
      city,
      zipCode,
      districtId: parseInt(districtId),
      stateOrProvinceId: parseInt(stateOrProvinceId),
      countryId: parseInt(countryId),
    };
    createAddress(request)
      .then((data) => {
        createUserAddress(data);
      })
      .then(() => {
        toast.success(CREATE_SUCCESSFULLY);
        router.push('/address');
      })
      .catch((e) => {
        console.log(e);
      });
  };

  return (
    <>
      <Head>
        <title>Create Address</title>
      </Head>
      <div className="container p-5">
        <h2 className="mb-3">Create Address</h2>
        <form onSubmit={handleSubmit(onSubmit)}>
          <AddressForm register={register} errors={errors} address={undefined} />
          <div className="container" style={{ textAlign: 'end' }}>
            <button className="btn btn-primary" type="submit">
              Save
            </button>
          </div>
        </form>
      </div>
    </>
  );
};

export default CreateAddress;
