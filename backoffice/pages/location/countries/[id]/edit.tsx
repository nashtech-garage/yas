import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { toastError } from '@commonServices/ToastService';
import CountryGeneralInformation from '@locationComponents/CountryGeneralInformation';
import { Country } from '@locationModels/Country';
import { editCountry, getCountry } from '@locationServices/CountryService';
import { COUNTRY_URL } from 'constants/Common';

const CountryEdit: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<Country>();
  const [country, setCountry] = useState<Country>();
  const [isLoading, setLoading] = useState(false);
  const { id } = router.query;
  const handleSubmitEdit = async (event: Country) => {
    if (id) {
      let country: Country = {
        id: 0,
        code2: event.code2,
        name: event.name,
        code3: event.code3,
        isBillingEnabled: event.isBillingEnabled,
        isShippingEnabled: event.isShippingEnabled,
        isCityEnabled: event.isCityEnabled,
        isZipCodeEnabled: event.isZipCodeEnabled,
        isDistrictEnabled: event.isDistrictEnabled,
      };

      editCountry(+id, country)
        .then((response) => {
          handleUpdatingResponse(response);
          router.replace(COUNTRY_URL).catch((error) => console.log(error));
        })
        .catch((error) => console.log(error));
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getCountry(+id)
        .then((data) => {
          if (data.id) {
            setCountry(data);
            setLoading(false);
          } else {
            toastError(data?.detail);
            setLoading(false);
            router.push(COUNTRY_URL).catch((error) => console.log(error));
          }
        })
        .catch((error) => console.log(error));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  if (isLoading) return <p>Loading...</p>;
  if (!country) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit country: {id}</h2>
          <form onSubmit={handleSubmit(handleSubmitEdit)}>
            <CountryGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
              country={country}
            />

            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href={`${COUNTRY_URL}`}>
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

export default CountryEdit;
