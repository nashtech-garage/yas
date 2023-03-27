import type { NextPage } from 'next';
import { Country } from '../../../modules/system/models/Country';
import { createCountry } from '../../../modules/system/services/CountryService';
import React from 'react';
import { useForm } from 'react-hook-form';
import Link from 'next/link';
import { useRouter } from 'next/router';
import CountryGeneralInformation from '../../../modules/system/components/CountryGeneralInformation';
import { BRAND_URL } from '../../../constants/Common';
import { handleCreatingResponse } from '../../../modules/catalog/services/ResponseStatusHandlingService';

const CountryCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<Country>();
  const handleSubmitCountry = async (event: Country) => {
    let country: Country = {
      id: 0,
      name: event.name,
      code3: event.code3,
      isBillingEnabled: event.isBillingEnabled,
      isShippingEnabled: event.isShippingEnabled,
      isCityEnabled: event.isCityEnabled,
      isZipCodeEnabled: event.isZipCodeEnabled,
      isDistrictEnabled: event.isDistrictEnabled,
    };
    let response = await createCountry(country);
    handleCreatingResponse(response);
    router.replace('/system/countries');
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create country</h2>
          <form onSubmit={handleSubmit(handleSubmitCountry)}>
            <CountryGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
            />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="/system/country">
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

export default CountryCreate;
