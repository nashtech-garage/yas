import { NextPage } from 'next';
import Head from 'next/head';
import { useForm } from 'react-hook-form';
import AddressForm from '../../modules/address/components/AddressForm';
import { Address } from '../../modules/address/models/AddressModel';

const CreateAddress: NextPage = () => {
  const { register, handleSubmit } = useForm<Address>();
  const onSubmit = () => {};

  return (
    <>
      <Head>
        <title>Create Address</title>
      </Head>
      <div className="p-5 form-wrapper">
        <div className="row mb-3">
          <h2 style={{ color: '#000000' }}>Create Address</h2>
        </div>
        <form onSubmit={handleSubmit(onSubmit)}>
          <AddressForm register={register} />
        </form>
      </div>
    </>
  );
};

export default CreateAddress;
