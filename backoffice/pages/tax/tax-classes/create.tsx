import type { NextPage } from 'next';
import { TaxClass } from '@taxModels/TaxClass';
import { createTaxClass } from '@taxServices/TaxClassService';
import React from 'react';
import { useForm } from 'react-hook-form';
import Link from 'next/link';
import { useRouter } from 'next/router';
import TaxClassGeneralInformation from '@taxComponents/TaxClassGeneralInformation';
import { TAX_CLASS_URL } from 'constants/Common';
import { handleCreatingResponse } from '@commonServices/ResponseStatusHandlingService';

const TaxClassCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<TaxClass>();
  const handleSubmitTaxClass = async (event: TaxClass) => {
    let taxClass: TaxClass = {
      id: 0,
      name: event.name,
    };
    let response = await createTaxClass(taxClass);
    handleCreatingResponse(response);
    router.replace(TAX_CLASS_URL);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create Tax Class</h2>
          <form onSubmit={handleSubmit(handleSubmitTaxClass)}>
            <TaxClassGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
            />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="/tax/tax-class">
              <button className="btn btn-primary" style={{ background: 'red', marginLeft: '30px' }}>
                Cancel
              </button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default TaxClassCreate;
