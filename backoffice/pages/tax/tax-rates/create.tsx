import type { NextPage } from 'next';
import { TaxRate } from '@taxModels/TaxRate';
import { createTaxRate } from '@taxServices/TaxRateService';
import React from 'react';
import { useForm } from 'react-hook-form';
import Link from 'next/link';
import { useRouter } from 'next/router';
import TaxRateGeneralInformation from '@taxComponents/TaxRateGeneralInformation';
import { TAX_RATE_URL } from 'constants/Common';
import { handleCreatingResponse } from '@commonServices/ResponseStatusHandlingService';

const TaxRateCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<TaxRate>();
  const handleSubmitTaxRate = async (event: TaxRate) => {
    let taxRate: TaxRate = {
      id: 0,
      rate: event.rate,
      zipCode: event.zipCode,
      taxClassId: event.taxClassId,
      taxClassName: '',
      countryId: event.countryId,
      countryName: '',
      stateOrProvinceId: event.stateOrProvinceId,
      stateOrProvinceName: '',
    };
    let response = await createTaxRate(taxRate);
    handleCreatingResponse(response);
    router.replace(TAX_RATE_URL);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create Tax Class</h2>
          <form onSubmit={handleSubmit(handleSubmitTaxRate)}>
            <TaxRateGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
            />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href={`${TAX_RATE_URL}`}>
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

export default TaxRateCreate;
