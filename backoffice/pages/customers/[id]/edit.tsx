import { Input } from '@commonItems/Input';
import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { CUSTOMER_URL, ResponseStatus } from '@constants/Common';
import { EMAIL_PATTERN } from 'modules/catalog/constants/validationPattern';
import { Customer, CustomerCreateVM, CustomerUpdateVM } from 'modules/customer/models/Customer';
import { getCustomer, updateCustomer } from 'modules/customer/services/CustomerService';
import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

const EditCustomer: NextPage = () => {
  //Get ID
  const router = useRouter();
  const { id } = router.query;

  const [customer, setCustomer] = useState<Customer>();
  const [isLoading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<CustomerCreateVM>();

  useEffect(() => {
    setLoading(true);
    if (id) {
      getCustomer(id as string)
        .then((data) => {
          setCustomer(data);
          setLoading(false);
        })
        .catch((error) => console.log(error));
    }
  }, [id]);

  const handleSubmitEditCustomer = async (event: CustomerUpdateVM) => {
    let customer: CustomerUpdateVM = {
      email: event.email,
      firstName: event.firstName,
      lastName: event.lastName,
    };

    let response = await updateCustomer(id as string, customer);

    if (response.status === ResponseStatus.SUCCESS) {
      router.replace(CUSTOMER_URL);
    }
    handleUpdatingResponse(response);
  };

  if (isLoading) return <p>Loading...</p>;

  if (!customer) {
    return <p>No customer</p>;
  } else {
    return (
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Update {customer?.username} customer</h2>
          <form onSubmit={handleSubmit(handleSubmitEditCustomer)}>
            <Input
              labelText="Email"
              field="email"
              defaultValue={customer?.email}
              register={register}
              registerOptions={{
                required: { value: true, message: 'Email is required' },
                pattern: {
                  value: EMAIL_PATTERN,
                  message: 'Please provide correct email',
                },
              }}
              error={errors.email?.message}
            />
            <Input
              labelText="First name"
              field="firstName"
              defaultValue={customer?.firstName}
              register={register}
              registerOptions={{
                required: { value: true, message: 'First name is required' },
              }}
              error={errors.firstName?.message}
            />
            <Input
              labelText="Last name"
              field="lastName"
              defaultValue={customer?.lastName}
              register={register}
              registerOptions={{
                required: { value: true, message: 'Last name is required' },
              }}
              error={errors.lastName?.message}
            />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="/customers">
              <button className="btn btn-primary" style={{ background: 'red', marginLeft: '30px' }}>
                Cancel
              </button>
            </Link>
          </form>
        </div>
      </div>
    );
  }
};

export default EditCustomer;
