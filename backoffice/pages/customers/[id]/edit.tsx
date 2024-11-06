import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { CUSTOMER_URL, ResponseStatus } from '@constants/Common';
import CustomerBaseInformation from 'modules/customer/components/CustomerBaseInformation';
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
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CustomerCreateVM>();

  useEffect(() => {
    setIsLoading(true);
    if (id) {
      getCustomer(id as string)
        .then((data) => {
          setCustomer(data);
          setIsLoading(false);
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
            <CustomerBaseInformation register={register} errors={errors} customer={customer} />
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
