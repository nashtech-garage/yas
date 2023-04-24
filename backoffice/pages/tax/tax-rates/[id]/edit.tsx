import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { TaxRate } from '@taxModels/TaxRate';
import { editTaxRate, getTaxRate } from '@taxServices/TaxRateService';
import TaxRateGeneralInformation from '@taxComponents/TaxRateGeneralInformation';
import { useEffect, useState } from 'react';
import { TAX_RATE_URL } from 'constants/Common';
import { toastError } from '@commonServices/ToastService';
import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';

const TaxRateEdit: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<TaxRate>();
  const [taxRate, setTaxRate] = useState<TaxRate>();
  const [isLoading, setLoading] = useState(false);
  const { id } = router.query;
  const handleSubmitEdit = async (event: TaxRate) => {
    if (id) {
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

      editTaxRate(+id, taxRate).then((response) => {
        handleUpdatingResponse(response);
        router.replace(TAX_RATE_URL);
      });
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getTaxRate(+id).then((data) => {
        if (data.id) {
          setTaxRate(data);
          setLoading(false);
        } else {
          toastError(data?.detail);
          setLoading(false);
          router.push(TAX_RATE_URL);
        }
      });
    }
  }, [id]);

  if (isLoading) return <p>Loading...</p>;
  if (!taxRate) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Tax Class: {id}</h2>
          <form onSubmit={handleSubmit(handleSubmitEdit)}>
            <TaxRateGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
              taxRate={taxRate}
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

export default TaxRateEdit;
